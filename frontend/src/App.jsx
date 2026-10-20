import { useEffect, useState } from "react";
import { fetchStatus } from "./api.js";
import { initialData } from "./data.js";
import { resources } from "./config.js";
import Sidebar from "./components/Sidebar.jsx";
import Overview from "./components/Overview.jsx";
import ResourcePage from "./components/ResourcePage.jsx";

export default function App() {
  const [page, setPage] = useState("overview");
  const [data, setData] = useState(initialData);
  const [backend, setBackend] = useState("checking"); // checking | online | offline

  const checkBackend = () => {
    setBackend("checking");
    fetchStatus()
      .then(() => setBackend("online"))
      .catch(() => setBackend("offline"));
  };

  useEffect(() => {
    checkBackend();
  }, []);

  const setRows = (key) => (rows) => setData((d) => ({ ...d, [key]: rows }));

  return (
    <div className="app">
      <Sidebar page={page} onNavigate={setPage} backend={backend} onRetry={checkBackend} />
      <main>
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
