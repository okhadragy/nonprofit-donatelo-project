import { resources } from "../config.js";

const statusText = {
  checking: "Checking backend…",
  online: "Backend connected",
  offline: "Backend offline",
};

export default function Sidebar({ page, onNavigate, backend, onRetry }) {
  const items = [["overview", "Overview"], ...Object.entries(resources).map(([k, c]) => [k, c.label])];

  return (
    <aside className="sidebar">
      <div className="brand">Donatelo</div>

      <nav className="nav">
        {items.map(([key, label]) => (
          <button key={key} className={page === key ? "active" : ""} onClick={() => onNavigate(key)}>
            {label}
          </button>
        ))}
      </nav>

      <div className="status">
        <span className={`dot ${backend}`} />
        <span>{statusText[backend]}</span>
        {backend === "offline" && (
          <button className="link-btn" onClick={onRetry}>
            Retry
          </button>
        )}
      </div>
    </aside>
  );
}
