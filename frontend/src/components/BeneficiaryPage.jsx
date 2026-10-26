import { useState, useEffect } from "react";
import {
  fetchBeneficiaries,
  createBeneficiary,
  updateBeneficiary,
  deleteBeneficiary,
  fetchResources,
  createResource,
} from "../api.js";
import ConfirmModal from "./ConfirmModal.jsx";

const pillClass = (value) =>
  ["Active", "Ongoing"].includes(value)
    ? "pill good"
    : ["Planned", "Waitlist", "Pending"].includes(value)
    ? "pill warn"
    : "pill";

export default function BeneficiaryPage() {
  const [activeTab, setActiveTab] = useState("beneficiaries");

  const [beneficiaries, setBeneficiaries] = useState([]);
  const [resources, setResources] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [query, setQuery] = useState("");

  const [editingBeneficiary, setEditingBeneficiary] = useState(null);
  const [editingResource, setEditingResource] = useState(null);
  const [distributeModal, setDistributeModal] = useState(null);

  const [deletingItem, setDeletingItem] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const loadAll = async () => {
    setLoading(true);
    setError(null);
    try {
      const [bData, rData] = await Promise.all([
        fetchBeneficiaries(),
        fetchResources(),
      ]);
      setBeneficiaries(bData);
      setResources(rData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAll();
  }, []);

  const openAddBeneficiary = () => {
    setEditingBeneficiary({ id: null, name: "", city: "", category: "Family", status: "Active" });
  };

  const openEditBeneficiary = (row) => {
    setEditingBeneficiary({ id: row.id, name: row.name, city: row.city, category: row.category, status: row.status });
  };

  const saveBeneficiary = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      if (editingBeneficiary.id === null) {
        await createBeneficiary(editingBeneficiary);
      } else {
        await updateBeneficiary(editingBeneficiary.id, editingBeneficiary);
      }
      setEditingBeneficiary(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  const openAddResource = () => {
    setEditingResource({ id: null, name: "", description: "", category: "" });
  };

  const saveResource = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      await createResource(editingResource);
      setEditingResource(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  const openDistributeModal = () => {
    setDistributeModal({
      beneficiaryId: beneficiaries[0] ? String(beneficiaries[0].id) : "",
      resourceId: resources[0] ? String(resources[0].id) : "",
    });
  };

  const saveDistribution = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      await fetch("http://localhost:8080/api/resources/distribute", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify({
          resourceId: Number(distributeModal.resourceId),
          beneficiaryId: Number(distributeModal.beneficiaryId),
        }),
      });
      setDistributeModal(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  const confirmDelete = async () => {
    if (!deletingItem) return;
    setIsDeleting(true);
    setError(null);
    try {
      if (deletingItem.type === "beneficiary") {
        await deleteBeneficiary(deletingItem.item.id);
      }
      setDeletingItem(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsDeleting(false);
    }
  };

  const visibleBeneficiaries = beneficiaries.filter((b) =>
    `${b.name} ${b.city} ${b.category} ${b.status}`.toLowerCase().includes(query.toLowerCase())
  );

  const visibleResources = resources.filter((r) =>
    `${r.name} ${r.category} ${r.description}`.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <>
      <header className="page-head">
        <div>
          <h1>Beneficiaries & Resources</h1>
          <p>{beneficiaries.length} beneficiaries · {resources.length} resource types</p>
        </div>
        <div style={{ display: "flex", gap: "8px" }}>
          <button className="btn" onClick={openDistributeModal}>
            Distribute
          </button>
          {activeTab === "resources" ? (
            <button className="btn primary" onClick={openAddResource}>Add resource</button>
          ) : (
            <button className="btn primary" onClick={openAddBeneficiary}>Add beneficiary</button>
          )}
        </div>
      </header>

      {error && <p style={{ color: "var(--danger)", marginBottom: 12 }}>{error}</p>}

      <div style={{ display: "flex", gap: "8px", marginBottom: "16px", borderBottom: "1px solid var(--line)", paddingBottom: "8px" }}>
        <button className={`btn ${activeTab === "beneficiaries" ? "primary" : ""}`} onClick={() => { setActiveTab("beneficiaries"); setQuery(""); }}>
          Beneficiaries ({beneficiaries.length})
        </button>
        <button className={`btn ${activeTab === "resources" ? "primary" : ""}`} onClick={() => { setActiveTab("resources"); setQuery(""); }}>
          Resources ({resources.length})
        </button>
      </div>

      <section className="panel">
        <div className="toolbar">
          <input
            type="search"
            placeholder={activeTab === "beneficiaries" ? "Search beneficiaries..." : "Search resources..."}
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>

        <div className="table-wrap">
          {loading ? (
            <p className="empty">Loading…</p>
          ) : activeTab === "beneficiaries" ? (
            <>
              <table>
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>City</th>
                    <th>Category</th>
                    <th>Status</th>
                    <th className="actions">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleBeneficiaries.map((row) => (
                    <tr key={row.id}>
                      <td><strong>{row.name}</strong></td>
                      <td>{row.city}</td>
                      <td>{row.category}</td>
                      <td><span className={pillClass(row.status)}>{row.status}</span></td>
                      <td className="actions">
                        <button className="btn small" onClick={() => openEditBeneficiary(row)}>Edit</button>
                        <button className="btn small danger" onClick={() => setDeletingItem({ type: "beneficiary", item: row })}>Delete</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {visibleBeneficiaries.length === 0 && (
                <p className="empty">{beneficiaries.length === 0 ? "No beneficiaries yet." : "No matches."}</p>
              )}
            </>
          ) : (
            <>
              <table>
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Category</th>
                    <th>Description</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleResources.map((row) => (
                    <tr key={row.id}>
                      <td><strong>{row.name}</strong></td>
                      <td>{row.category}</td>
                      <td>{row.description || "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {visibleResources.length === 0 && (
                <p className="empty">{resources.length === 0 ? "No resources yet." : "No matches."}</p>
              )}
            </>
          )}
        </div>
      </section>

      {editingBeneficiary && (
        <div className="overlay" onClick={() => setEditingBeneficiary(null)}>
          <form className="modal" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()} onSubmit={saveBeneficiary}>
            <h2>{editingBeneficiary.id === null ? "Add Beneficiary" : "Edit Beneficiary"}</h2>

            <label>Name
              <input type="text" required autoFocus value={editingBeneficiary.name}
                onChange={(e) => setEditingBeneficiary({ ...editingBeneficiary, name: e.target.value })} />
            </label>

            <label>City
              <input type="text" required value={editingBeneficiary.city}
                onChange={(e) => setEditingBeneficiary({ ...editingBeneficiary, city: e.target.value })} />
            </label>

            <label>Category
              <select value={editingBeneficiary.category}
                onChange={(e) => setEditingBeneficiary({ ...editingBeneficiary, category: e.target.value })}>
                {["Family", "Student", "Elder", "Orphan"].map((c) => <option key={c}>{c}</option>)}
              </select>
            </label>

            <label>Status
              <select value={editingBeneficiary.status}
                onChange={(e) => setEditingBeneficiary({ ...editingBeneficiary, status: e.target.value })}>
                {["Active", "Waitlist"].map((s) => <option key={s}>{s}</option>)}
              </select>
            </label>

            <div className="modal-actions">
              <button type="button" className="btn" onClick={() => setEditingBeneficiary(null)}>Cancel</button>
              <button type="submit" className="btn primary" disabled={isSaving}>
                {isSaving ? "Saving…" : "Save"}
              </button>
            </div>
          </form>
        </div>
      )}

      {editingResource && (
        <div className="overlay" onClick={() => setEditingResource(null)}>
          <form className="modal" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()} onSubmit={saveResource}>
            <h2>Add Resource</h2>

            <label>Name
              <input type="text" required autoFocus value={editingResource.name}
                onChange={(e) => setEditingResource({ ...editingResource, name: e.target.value })} />
            </label>

            <label>Category
              <input type="text" required value={editingResource.category}
                onChange={(e) => setEditingResource({ ...editingResource, category: e.target.value })} />
            </label>

            <label>Description
              <input type="text" value={editingResource.description}
                onChange={(e) => setEditingResource({ ...editingResource, description: e.target.value })} />
            </label>

            <div className="modal-actions">
              <button type="button" className="btn" onClick={() => setEditingResource(null)}>Cancel</button>
              <button type="submit" className="btn primary" disabled={isSaving}>
                {isSaving ? "Saving…" : "Save"}
              </button>
            </div>
          </form>
        </div>
      )}

      {distributeModal && (
        <div className="overlay" onClick={() => setDistributeModal(null)}>
          <form className="modal" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()} onSubmit={saveDistribution}>
            <h2>Distribute Resource</h2>

            <label>Beneficiary
              <select required value={distributeModal.beneficiaryId}
                onChange={(e) => setDistributeModal({ ...distributeModal, beneficiaryId: e.target.value })}>
                <option value="">— Select —</option>
                {beneficiaries.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
              </select>
            </label>

            <label>Resource
              <select required value={distributeModal.resourceId}
                onChange={(e) => setDistributeModal({ ...distributeModal, resourceId: e.target.value })}>
                <option value="">— Select —</option>
                {resources.map((r) => <option key={r.id} value={r.id}>{r.name}</option>)}
              </select>
            </label>

            <div className="modal-actions">
              <button type="button" className="btn" onClick={() => setDistributeModal(null)}>Cancel</button>
              <button type="submit" className="btn primary" disabled={isSaving}>
                {isSaving ? "Distributing…" : "Confirm"}
              </button>
            </div>
          </form>
        </div>
      )}

      <ConfirmModal
        isOpen={Boolean(deletingItem)}
        title="Delete Beneficiary?"
        message={deletingItem ? `Delete "${deletingItem.item.name}"? This cannot be undone.` : ""}
        confirmText="Delete"
        onConfirm={confirmDelete}
        onCancel={() => setDeletingItem(null)}
        loading={isDeleting}
      />
    </>
  );
}
