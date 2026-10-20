import { useState } from "react";

const pillClass = (value) =>
  ["Active", "Ongoing"].includes(value) ? "pill good" : ["Planned", "Waitlist"].includes(value) ? "pill warn" : "pill";

// One reusable page: table + search + add/edit modal + delete.
export default function ResourcePage({ config, rows, setRows }) {
  const [editing, setEditing] = useState(null); // null = closed, otherwise the record in the form
  const [query, setQuery] = useState("");

  const blank = () => ({
    id: null,
    ...Object.fromEntries(config.fields.map((f) => [f.key, f.options ? f.options[0] : ""])),
  });

  const visible = rows.filter((r) => Object.values(r).join(" ").toLowerCase().includes(query.toLowerCase()));

  const save = (e) => {
    e.preventDefault();
    if (editing.id === null) {
      setRows([...rows, { ...editing, id: Date.now() }]);
    } else {
      setRows(rows.map((r) => (r.id === editing.id ? editing : r)));
    }
    setEditing(null);
  };

  const remove = (row) => {
    if (window.confirm(`Delete this ${config.singular}?`)) {
      setRows(rows.filter((r) => r.id !== row.id));
    }
  };

  return (
    <>
      <header className="page-head">
        <div>
          <h1>{config.label}</h1>
          <p>{rows.length} records</p>
        </div>
        <button className="btn primary" onClick={() => setEditing(blank())}>
          Add {config.singular}
        </button>
      </header>

      <section className="panel">
        <div className="toolbar">
          <input
            type="search"
            placeholder={`Search ${config.label.toLowerCase()}`}
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                {config.fields.map((f) => (
                  <th key={f.key}>{f.label}</th>
                ))}
                <th className="actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              {visible.map((row) => (
                <tr key={row.id}>
                  {config.fields.map((f) => (
                    <td key={f.key}>
                      {f.pill ? (
                        <span className={pillClass(row[f.key])}>{row[f.key]}</span>
                      ) : f.format ? (
                        f.format(row[f.key])
                      ) : (
                        row[f.key]
                      )}
                    </td>
                  ))}
                  <td className="actions">
                    <button className="btn small" onClick={() => setEditing({ ...row })}>
                      Edit
                    </button>
                    <button className="btn small danger" onClick={() => remove(row)}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {visible.length === 0 && (
            <p className="empty">
              {rows.length === 0 ? `No ${config.label.toLowerCase()} yet. Add the first ${config.singular}.` : "No matches for that search."}
            </p>
          )}
        </div>
      </section>

      {editing && (
        <div className="overlay" onClick={() => setEditing(null)} onKeyDown={(e) => e.key === "Escape" && setEditing(null)}>
          <form className="modal" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()} onSubmit={save}>
            <h2>{editing.id === null ? `Add ${config.singular}` : `Edit ${config.singular}`}</h2>

            {config.fields.map((f, i) => (
              <label key={f.key}>
                {f.label}
                {f.type === "select" ? (
                  <select value={editing[f.key]} onChange={(e) => setEditing({ ...editing, [f.key]: e.target.value })}>
                    {f.options.map((o) => (
                      <option key={o}>{o}</option>
                    ))}
                  </select>
                ) : (
                  <input
                    type={f.type}
                    required
                    min={f.type === "number" ? 0 : undefined}
                    autoFocus={i === 0}
                    value={editing[f.key]}
                    onChange={(e) => setEditing({ ...editing, [f.key]: e.target.value })}
                  />
                )}
              </label>
            ))}

            <div className="modal-actions">
              <button type="button" className="btn" onClick={() => setEditing(null)}>
                Cancel
              </button>
              <button type="submit" className="btn primary">
                Save {config.singular}
              </button>
            </div>
          </form>
        </div>
      )}
    </>
  );
}
