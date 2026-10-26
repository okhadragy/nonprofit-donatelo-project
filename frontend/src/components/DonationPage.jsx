import { useState, useEffect } from "react";
import {
  fetchDonations,
  createDonation,
  updateDonation,
  deleteDonation,
  generateDonationDocument,
} from "../api.js";
import ConfirmModal from "./ConfirmModal.jsx";

const money = (v) => `$${Number(v).toLocaleString()}`;

const pillClass = (value) =>
  ["Active", "Ongoing"].includes(value) ? "pill good" : ["Planned", "Waitlist"].includes(value) ? "pill warn" : "pill";

export default function DonationPage({ donors, donationTypes }) {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [deletingRow, setDeletingRow] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [query, setQuery] = useState("");
  const [documentResult, setDocumentResult] = useState(null);
  const [generatingDocument, setGeneratingDocument] = useState(null);

  const loadDonations = () => {
    setLoading(true);
    setError(null);
    fetchDonations()
      .then((data) => {
        setRows(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  };

  useEffect(() => {
    loadDonations();
  }, []);

  const blank = () => ({
    id: null,
    amount: "",
    date: "",
    method: "Cash",
    donorId: donors.length > 0 ? String(donors[0].id) : "",
    donationTypeId: donationTypes.length > 0 ? String(donationTypes[0].id) : "",
  });

  const startEdit = (row) => {
    setEditing({
      id: row.id,
      amount: row.amount,
      date: row.date,
      method: row.method,
      donorId: row.donorId != null ? String(row.donorId) : "",
      donationTypeId: row.donationTypeId != null ? String(row.donationTypeId) : "",
    });
  };

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);

    const payload = {
      amount: Number(editing.amount),
      date: editing.date,
      method: editing.method,
      donorId: editing.donorId ? Number(editing.donorId) : null,
      donationTypeId: editing.donationTypeId ? Number(editing.donationTypeId) : null,
    };

    try {
      if (editing.id === null) {
        await createDonation(payload);
      } else {
        await updateDonation(editing.id, payload);
      }
      setEditing(null);
      loadDonations();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const remove = (row) => {
    setDeletingRow(row);
  };

  const confirmDelete = async () => {
    if (!deletingRow) return;
    setDeleting(true);
    setError(null);
    try {
      await deleteDonation(deletingRow.id);
      setDeletingRow(null);
      loadDonations();
    } catch (err) {
      setError(err.message);
    } finally {
      setDeleting(false);
    }
  };

  const generateDocument = async (row, type) => {
    setGeneratingDocument(`${row.id}-${type}`);
    setError(null);
    try {
      const content = await generateDonationDocument(row.id, type);
      setDocumentResult({
        title: `${type === "tax" ? "Tax document" : "Receipt"} for ${row.donorName || "donation"}`,
        content,
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setGeneratingDocument(null);
    }
  };

  const downloadDocument = () => {
    if (!documentResult) return;
    const blob = new Blob([documentResult.content], { type: "text/plain;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `${documentResult.title.toLowerCase().replace(/[^a-z0-9]+/g, "-")}.txt`;
    link.click();
    URL.revokeObjectURL(url);
  };

  const visible = rows.filter((r) =>
    Object.values(r).join(" ").toLowerCase().includes(query.toLowerCase())
  );

  return (
    <>
      <header className="page-head">
        <div>
          <h1>Donations</h1>
          <p>{rows.length} records</p>
        </div>
        <button className="btn primary" onClick={() => setEditing(blank())}>
          Add donation
        </button>
      </header>

      {error && <p style={{ color: "var(--danger)", marginBottom: 12 }}>{error}</p>}

      <section className="panel">
        <div className="toolbar">
          <input
            type="search"
            placeholder="Search donations"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>

        <div className="table-wrap">
          {loading ? (
            <p className="empty">Loading donations…</p>
          ) : (
            <>
              <table>
                <thead>
                  <tr>
                    <th>Donor</th>
                    <th>Amount (EGP)</th>
                    <th>Date</th>
                    <th>Method</th>
                    <th>Type</th>
                    <th className="actions">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {visible.map((row) => (
                    <tr key={row.id}>
                      <td>{row.donorName || "—"}</td>
                      <td>{money(row.amount)}</td>
                      <td>{row.date}</td>
                      <td>{row.method}</td>
                      <td>{row.donationTypeName || "—"}</td>
                      <td className="actions">
                        <button className="btn small" onClick={() => startEdit(row)}>
                          Edit
                        </button>
                        <button
                          className="btn small"
                          onClick={() => generateDocument(row, "receipt")}
                          disabled={generatingDocument !== null}
                        >
                          {generatingDocument === `${row.id}-receipt` ? "Generating…" : "Receipt"}
                        </button>
                        <button
                          className="btn small"
                          onClick={() => generateDocument(row, "tax")}
                          disabled={generatingDocument !== null}
                        >
                          {generatingDocument === `${row.id}-tax` ? "Generating…" : "Tax"}
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
                  {rows.length === 0
                    ? "No donations yet. Add the first donation."
                    : "No matches for that search."}
                </p>
              )}
            </>
          )}
        </div>
      </section>

      {editing && (
        <div className="overlay" onClick={() => setEditing(null)} onKeyDown={(e) => e.key === "Escape" && setEditing(null)}>
          <form className="modal" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()} onSubmit={save}>
            <h2>{editing.id === null ? "Add donation" : "Edit donation"}</h2>

            <label>
              Donor
              <select
                value={editing.donorId}
                onChange={(e) => setEditing({ ...editing, donorId: e.target.value })}
              >
                <option value="">— Select donor —</option>
                {donors.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.name}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Amount (EGP)
              <input
                type="number"
                required
                min={0}
                step="0.01"
                autoFocus
                value={editing.amount}
                onChange={(e) => setEditing({ ...editing, amount: e.target.value })}
              />
            </label>

            <label>
              Date
              <input
                type="date"
                required
                value={editing.date}
                onChange={(e) => setEditing({ ...editing, date: e.target.value })}
              />
            </label>

            <label>
              Method
              <select
                value={editing.method}
                onChange={(e) => setEditing({ ...editing, method: e.target.value })}
              >
                {["Cash", "Card", "Check"].map((o) => (
                  <option key={o}>{o}</option>
                ))}
              </select>
            </label>

            <label>
              Type
              <select
                value={editing.donationTypeId}
                onChange={(e) => setEditing({ ...editing, donationTypeId: e.target.value })}
              >
                <option value="">— Select type —</option>
                {donationTypes.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.name}
                  </option>
                ))}
              </select>
            </label>

            <div className="modal-actions">
              <button type="button" className="btn" onClick={() => setEditing(null)}>
                Cancel
              </button>
              <button type="submit" className="btn primary" disabled={saving}>
                {saving ? "Saving…" : "Save donation"}
              </button>
            </div>
          </form>
        </div>
      )}

      {documentResult && (
        <div className="overlay" onClick={() => setDocumentResult(null)}>
          <section
            className="modal document-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="document-title"
            onClick={(e) => e.stopPropagation()}
          >
            <h2 id="document-title">{documentResult.title}</h2>
            <pre className="document-content">{documentResult.content}</pre>
            <div className="modal-actions">
              <button type="button" className="btn" onClick={() => setDocumentResult(null)}>
                Close
              </button>
              <button type="button" className="btn primary" onClick={downloadDocument}>
                Download .txt
              </button>
            </div>
          </section>
        </div>
      )}

      <ConfirmModal
        isOpen={Boolean(deletingRow)}
        title="Delete donation?"
        message={
          deletingRow
            ? `Are you sure you want to delete this donation of ${money(deletingRow.amount)}${
                deletingRow.donorName ? ` from ${deletingRow.donorName}` : ""
              }? This action cannot be undone.`
            : ""
        }
        confirmText="Delete Donation"
        onConfirm={confirmDelete}
        onCancel={() => setDeletingRow(null)}
        loading={deleting}
      />
    </>
  );
}
