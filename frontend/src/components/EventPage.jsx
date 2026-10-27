import { useState, useEffect } from "react";
import {
  fetchEvents,
  createEvent,
  updateEvent,
  deleteEvent,
  fetchEventCategories,
  createEventCategory,
  updateEventCategory,
  deleteEventCategory,
} from "../api.js";
import ConfirmModal from "./ConfirmModal.jsx";

const pillClass = (value) =>
  ["Active", "Ongoing", "Confirmed", "Attended"].includes(value)
    ? "pill good"
    : ["Planned", "Waitlist", "Pending", "Registered"].includes(value)
    ? "pill warn"
    : "pill";

export default function EventPage() {
  const [activeTab, setActiveTab] = useState("events");

  const [events, setEvents] = useState([]);
  const [categories, setCategories] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [query, setQuery] = useState("");

  const [editingEvent, setEditingEvent] = useState(null);
  const [editingCategory, setEditingCategory] = useState(null);

  const [deletingItem, setDeletingItem] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const loadAll = async () => {
    setLoading(true);
    setError(null);
    try {
      const [eData, cData] = await Promise.all([
        fetchEvents(),
        fetchEventCategories(),
      ]);
      setEvents(eData);
      setCategories(cData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAll();
  }, []);

  // --- Events actions ---
  const openAddEvent = () => {
    setEditingEvent({
      id: null,
      title: "",
      date: new Date().toISOString().split("T")[0],
      location: "",
      status: "Planned",
      categoryId: categories[0] ? String(categories[0].id) : "",
    });
  };

  const openEditEvent = (row) => {
    setEditingEvent({
      id: row.id,
      title: row.title,
      date: row.date,
      location: row.location || "",
      status: row.status,
      categoryId: row.categoryId ? String(row.categoryId) : "",
    });
  };

  const saveEvent = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      const payload = {
        title: editingEvent.title,
        date: editingEvent.date,
        location: editingEvent.location,
        status: editingEvent.status,
        categoryId: editingEvent.categoryId ? Number(editingEvent.categoryId) : null,
      };

      if (editingEvent.id === null) {
        await createEvent(payload);
      } else {
        await updateEvent(editingEvent.id, payload);
      }
      setEditingEvent(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  // --- Categories actions ---
  const openAddCategory = () => {
    setEditingCategory({ id: null, name: "" });
  };

  const openEditCategory = (cat) => {
    setEditingCategory({ id: cat.id, name: cat.name });
  };

  const saveCategory = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      if (editingCategory.id === null) {
        await createEventCategory({ name: editingCategory.name });
      } else {
        await updateEventCategory(editingCategory.id, { name: editingCategory.name });
      }
      setEditingCategory(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  // --- Deletion ---
  const confirmDelete = async () => {
    if (!deletingItem) return;
    setIsDeleting(true);
    setError(null);
    try {
      if (deletingItem.type === "event") {
        await deleteEvent(deletingItem.id);
      } else if (deletingItem.type === "category") {
        await deleteEventCategory(deletingItem.id);
      }
      setDeletingItem(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsDeleting(false);
    }
  };

  // --- Filtered rows ---
  const visibleEvents = events.filter((e) =>
    `${e.title || ""} ${e.location || ""} ${e.categoryName || ""} ${e.status || ""}`
      .toLowerCase()
      .includes(query.toLowerCase())
  );

  const visibleCategories = categories.filter((c) =>
    (c.name || "").toLowerCase().includes(query.toLowerCase())
  );

  return (
    <>
      <header className="page-head">
        <div>
          <h1>Events</h1>
          <p>
            {events.length} events · {categories.length} categories
          </p>
        </div>
        <div style={{ display: "flex", gap: "8px" }}>
          {activeTab === "events" ? (
            <button className="btn primary" onClick={openAddEvent}>
              Add Event
            </button>
          ) : (
            <button className="btn primary" onClick={openAddCategory}>
              Add Category
            </button>
          )}
        </div>
      </header>

      {error && <p style={{ color: "var(--danger)", marginBottom: 12 }}>{error}</p>}

      <div
        style={{
          display: "flex",
          gap: "8px",
          marginBottom: "16px",
          borderBottom: "1px solid var(--line)",
          paddingBottom: "8px",
        }}
      >
        <button
          className={`btn ${activeTab === "events" ? "primary" : ""}`}
          onClick={() => {
            setActiveTab("events");
            setQuery("");
          }}
        >
          Events ({events.length})
        </button>
        <button
          className={`btn ${activeTab === "categories" ? "primary" : ""}`}
          onClick={() => {
            setActiveTab("categories");
            setQuery("");
          }}
        >
          Event Categories ({categories.length})
        </button>
      </div>

      <section className="panel">
        <div className="toolbar">
          <input
            type="search"
            placeholder={`Search ${activeTab}...`}
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>

        <div className="table-wrap">
          {loading ? (
            <p className="empty">Loading…</p>
          ) : activeTab === "events" ? (
            <>
              <table>
                <thead>
                  <tr>
                    <th>Title</th>
                    <th>Category</th>
                    <th>Date</th>
                    <th>Location</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleEvents.map((r) => (
                    <tr key={r.id}>
                      <td><strong>{r.title}</strong></td>
                      <td>{r.categoryName || "—"}</td>
                      <td>{r.date}</td>
                      <td>{r.location || "—"}</td>
                      <td>
                        <span className={pillClass(r.status)}>{r.status}</span>
                      </td>
                      <td>
                        <div className="row-actions">
                          <button
                            className="btn-icon"
                            title="Edit"
                            onClick={() => openEditEvent(r)}
                          >
                            ✎
                          </button>
                          <button
                            className="btn-icon"
                            title="Delete"
                            onClick={() =>
                              setDeletingItem({
                                type: "event",
                                id: r.id,
                                name: r.title,
                              })
                            }
                          >
                            ✕
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {visibleEvents.length === 0 && (
                    <tr>
                      <td colSpan={6} className="empty">
                        No events found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </>
          ) : (
            <>
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Category Name</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleCategories.map((c) => (
                    <tr key={c.id}>
                      <td>{c.id}</td>
                      <td><strong>{c.name}</strong></td>
                      <td>
                        <div className="row-actions">
                          <button
                            className="btn-icon"
                            title="Edit"
                            onClick={() => openEditCategory(c)}
                          >
                            ✎
                          </button>
                          <button
                            className="btn-icon"
                            title="Delete"
                            onClick={() =>
                              setDeletingItem({
                                type: "category",
                                id: c.id,
                                name: c.name,
                              })
                            }
                          >
                            ✕
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {visibleCategories.length === 0 && (
                    <tr>
                      <td colSpan={3} className="empty">
                        No categories found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </>
          )}
        </div>
      </section>

      {/* Modal: Event Form */}
      {editingEvent && (
        <div className="modal-backdrop">
          <form className="modal-card" onSubmit={saveEvent}>
            <h2>{editingEvent.id ? "Edit Event" : "Add Event"}</h2>
            <label>
              Title
              <input
                type="text"
                required
                value={editingEvent.title}
                onChange={(e) =>
                  setEditingEvent({ ...editingEvent, title: e.target.value })
                }
              />
            </label>
            <label>
              Date
              <input
                type="date"
                required
                value={editingEvent.date}
                onChange={(e) =>
                  setEditingEvent({ ...editingEvent, date: e.target.value })
                }
              />
            </label>
            <label>
              Location
              <input
                type="text"
                value={editingEvent.location}
                onChange={(e) =>
                  setEditingEvent({ ...editingEvent, location: e.target.value })
                }
              />
            </label>
            <label>
              Category
              <select
                value={editingEvent.categoryId}
                onChange={(e) =>
                  setEditingEvent({ ...editingEvent, categoryId: e.target.value })
                }
              >
                <option value="">No Category</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Status
              <select
                value={editingEvent.status}
                onChange={(e) =>
                  setEditingEvent({ ...editingEvent, status: e.target.value })
                }
              >
                <option value="Planned">Planned</option>
                <option value="Ongoing">Ongoing</option>
                <option value="Completed">Completed</option>
              </select>
            </label>
            <div className="modal-actions">
              <button
                type="button"
                className="btn"
                onClick={() => setEditingEvent(null)}
              >
                Cancel
              </button>
              <button type="submit" className="btn primary" disabled={isSaving}>
                {isSaving ? "Saving…" : "Save"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Modal: Category Form */}
      {editingCategory && (
        <div className="modal-backdrop">
          <form className="modal-card" onSubmit={saveCategory}>
            <h2>{editingCategory.id ? "Edit Category" : "Add Event Category"}</h2>
            <label>
              Category Name
              <input
                type="text"
                required
                value={editingCategory.name}
                onChange={(e) =>
                  setEditingCategory({ ...editingCategory, name: e.target.value })
                }
              />
            </label>
            <div className="modal-actions">
              <button
                type="button"
                className="btn"
                onClick={() => setEditingCategory(null)}
              >
                Cancel
              </button>
              <button type="submit" className="btn primary" disabled={isSaving}>
                {isSaving ? "Saving…" : "Save"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Confirm Delete Modal */}
      {deletingItem && (
        <ConfirmModal
          title={`Delete ${deletingItem.type}`}
          message={`Are you sure you want to delete "${deletingItem.name}"? This action cannot be undone.`}
          confirmLabel={isDeleting ? "Deleting…" : "Delete"}
          onConfirm={confirmDelete}
          onCancel={() => setDeletingItem(null)}
        />
      )}
    </>
  );
}
