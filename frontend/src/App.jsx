
import { useEffect, useState } from "react";
import { fetchStatus } from "./api.js";
import { initialData } from "./data.js";
import { resources } from "./config.js";
import Sidebar from "./components/Sidebar.jsx";
import Overview from "./components/Overview.jsx";
import ResourcePage from "./components/ResourcePage.jsx";
import AuthPage from "./components/AuthPage.jsx";

export default function App() {
  const [token, setToken] = useState(
    () => localStorage.getItem("token")
  );
  const [page, setPage] = useState("overview");
  const [data, setData] = useState(initialData);
  const [backend, setBackend] = useState("checking");

  const checkBackend = () => {
    setBackend("checking");
    fetchStatus()
      .then(() => setBackend("online"))
      .catch(() => setBackend("offline"));
  };

  useEffect(() => {
    if (token) {
      checkBackend();
    }
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
          <Overview data={data} onOpen={setPage} />
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
