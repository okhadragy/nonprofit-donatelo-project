import { useEffect, useState } from "react";
import { fetchStatus, fetchDonationTypes, fetchDonors } from "./api.js";
import { initialData } from "./data.js";
import { resources } from "./config.js";
import Sidebar from "./components/Sidebar.jsx";
import Overview from "./components/Overview.jsx";
import ResourcePage from "./components/ResourcePage.jsx";
import DonationPage from "./components/DonationPage.jsx";
import BeneficiaryPage from "./components/BeneficiaryPage.jsx";
import EventPage from "./components/EventPage.jsx";
import AuthPage from "./components/AuthPage.jsx";

export default function App() {
  const [token, setToken] = useState(
    () => localStorage.getItem("token")
  );
  const [page, setPage] = useState("overview");
  const [data, setData] = useState(initialData);
  const [backend, setBackend] = useState("checking");
  const [donationTypes, setDonationTypes] = useState([]);
  const [donors, setDonors] = useState([]);

  const checkBackend = () => {
    setBackend((prev) => {
      const wasPreviouslyOffline = prev === "offline";

      fetchStatus()
        .then(() => {
          if (wasPreviouslyOffline) {
            // Backend restarted — in-memory DB was wiped, old token is stale
            handleLogout();
          } else {
            setBackend("online");
          }
        })
        .catch(() => setBackend("offline"));

      return "checking";
    });
  };

  useEffect(() => {
    if (token) {
      checkBackend();
    }
  }, [token]);

  useEffect(() => {
    if (!token) return;

    checkBackend();

    fetchDonationTypes()
      .then(setDonationTypes)
      .catch((error) => {
        console.error("Could not load donation types:", error);
      });

    fetchDonors()
      .then(setDonors)
      .catch((error) => {
        console.error("Could not load donors:", error);
      });
  }, [token]);

  const handleAuthenticated = (newToken) => {
    localStorage.setItem("token", newToken);
    setToken(newToken);
    setPage("overview");
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setPage("overview");
    setBackend("checking");
  };

  const setRows = (key) => (rows) =>
    setData((current) => ({ ...current, [key]: rows }));

  // No token: require login or registration.
  if (!token) {
    return <AuthPage onAuthenticated={handleAuthenticated} />;
  }

  return (
    <div className="app">
      <Sidebar
        page={page}
        onNavigate={setPage}
        backend={backend}
        onRetry={checkBackend}
      />

      <main>
        <div className="app-account-bar">
          <button type="button" onClick={handleLogout}>
            Log out
          </button>
        </div>

        {page === "overview" ? (
          <Overview data={data} onOpen={setPage} onUpdateData={setData} />
        ) : page === "donations" ? (
          <DonationPage
            donors={donors}
            donationTypes={donationTypes}
          />
        ) : page === "beneficiaries" ? (
          <BeneficiaryPage />
        ) : page === "events" ? (
          <EventPage />
        ) : (
          <ResourcePage
            key={page}
            config={resources[page]}
            rows={data[page]}
            setRows={setRows(page)}
          />
        )}
      </main>
    </div>
  );
}
