import { useEffect, useState } from "react";
import { resources } from "../config.js";
import { fetchEvents, fetchDonations, fetchBeneficiaries } from "../api.js";

export default function Overview({ data, onOpen, onUpdateData }) {
  const [liveData, setLiveData] = useState(data);
  const [loading, setLoading] = useState(false);
  const [lastUpdated, setLastUpdated] = useState(new Date());

  const refreshLiveDashboard = async () => {
    setLoading(true);
    try {
      const [events, donations, beneficiaries] = await Promise.allSettled([
        fetchEvents(),
        fetchDonations(),
        fetchBeneficiaries(),
      ]);

      setLiveData((prev) => {
        const next = { ...prev };
        if (events.status === "fulfilled") next.events = events.value;
        if (donations.status === "fulfilled") next.donations = donations.value;
        if (beneficiaries.status === "fulfilled") next.beneficiaries = beneficiaries.value;
        if (onUpdateData) {
          onUpdateData(next);
        }
        return next;
      });
      setLastUpdated(new Date());
    } catch (e) {
      console.warn("Could not load live dashboard data:", e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    refreshLiveDashboard();
  }, []);

  const current = liveData || data;

  return (
    <>
      <header className="page-head">
        <div>
          <h1>Overview Dashboard</h1>
          <p>
            Real-time live snapshot connected to backend services and database.
          </p>
        </div>
        <div style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
          <span style={{ fontSize: "0.85rem", opacity: 0.75 }}>
            Synced: {lastUpdated.toLocaleTimeString()}
          </span>
          <button
            className="btn"
            onClick={refreshLiveDashboard}
            disabled={loading}
          >
            {loading ? "Syncing…" : "↻ Refresh"}
          </button>
        </div>
      </header>

      <section className="panel">
        <ul className="summary">
          {Object.entries(resources).map(([key, config]) => {
            const rows = current[key] || [];
            return (
              <li key={key}>
                <div>
                  <strong>{config.label}</strong>
                  <span>{config.blurb(rows)}</span>
                </div>
                <button className="btn" onClick={() => onOpen(key)}>
                  Open {config.label}
                </button>
              </li>
            );
          })}
        </ul>
      </section>
    </>
  );
}
