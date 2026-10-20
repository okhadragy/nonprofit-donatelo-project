import { resources } from "../config.js";

export default function Overview({ data, onOpen }) {
  return (
    <>
      <header className="page-head">
        <div>
          <h1>Overview</h1>
          <p>A snapshot of each area. Data on these pages is mock data.</p>
        </div>
      </header>

      <section className="panel">
        <ul className="summary">
          {Object.entries(resources).map(([key, config]) => (
            <li key={key}>
              <div>
                <strong>{config.label}</strong>
                <span>{config.blurb(data[key])}</span>
              </div>
              <button className="btn" onClick={() => onOpen(key)}>
                Open
              </button>
            </li>
          ))}
        </ul>
      </section>
    </>
  );
}
