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
  fetchTickets,
  createTicket,
  updateTicket,
  deleteTicket,
  fetchEventRegistrations,
  createEventRegistration,
  updateEventRegistration,
  deleteEventRegistration,
  fetchDecoratedEvent,
  sendEventCommunication,
  fetchEventObserverLogs,
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
  const [tickets, setTickets] = useState([]);
  const [registrations, setRegistrations] = useState([]);
  const [observerLogs, setObserverLogs] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [query, setQuery] = useState("");
  const [toastMessage, setToastMessage] = useState(null);

  const [editingEvent, setEditingEvent] = useState(null);
  const [editingCategory, setEditingCategory] = useState(null);
  const [editingTicket, setEditingTicket] = useState(null);
  const [editingRegistration, setEditingRegistration] = useState(null);

  // Decorator Pattern Modal State
  const [decoratingEvent, setDecoratingEvent] = useState(null);
  const [decoratorOptions, setDecoratorOptions] = useState({
    fundraising: true,
    reminder: true,
    followup: false,
    fundraisingGoal: "$50,000",
  });
  const [decoratedResult, setDecoratedResult] = useState(null);
  const [isDecoratingLoading, setIsDecoratingLoading] = useState(false);

  // Communication / Reminder Modal State
  const [communicatingEvent, setCommunicatingEvent] = useState(null);
  const [commForm, setCommForm] = useState({
    subject: "",
    message: "",
    type: "REMINDER",
  });
  const [commResult, setCommResult] = useState(null);
  const [isSendingComm, setIsSendingComm] = useState(false);

  const [deletingItem, setDeletingItem] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 4000);
  };

  const loadAll = async () => {
    setLoading(true);
    setError(null);
    try {
      const [eData, cData, tData, rData, logs] = await Promise.all([
        fetchEvents(),
        fetchEventCategories(),
        fetchTickets(),
        fetchEventRegistrations(),
        fetchEventObserverLogs().catch(() => []),
      ]);
      setEvents(eData);
      setCategories(cData);
      setTickets(tData);
      setRegistrations(rData);
      setObserverLogs(logs);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAll();
  }, []);

  const refreshLogs = async () => {
    try {
      const logs = await fetchEventObserverLogs();
      setObserverLogs(logs);
    } catch (err) {
      console.warn("Could not load observer logs:", err);
    }
  };

  // --- Events actions ---
  const openAddEvent = () => {
    setEditingEvent({
      id: null,
      title: "",
      date: new Date().toISOString().slice(0, 10),
      location: "",
      status: "Planned",
      categoryId: categories[0] ? String(categories[0].id) : "",
    });
  };

  const openEditEvent = (row) => {
    setEditingEvent({
      id: row.id,
      title: row.title,
      date: row.date || "",
      location: row.location || "",
      status: row.status || "Planned",
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
        showToast("Event created and EventSubject notified observers!");
      } else {
        await updateEvent(editingEvent.id, payload);
        showToast("Event updated and EventSubject notified observers!");
      }
      setEditingEvent(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  // --- Decorator Pattern Actions ---
  const openDecoratorModal = async (ev) => {
    setDecoratingEvent(ev);
    const initialOpts = {
      fundraising: true,
      reminder: true,
      followup: false,
      fundraisingGoal: "$50,000",
    };
    setDecoratorOptions(initialOpts);
    await loadDecoratedPreview(ev.id, initialOpts);
  };

  const loadDecoratedPreview = async (eventId, opts) => {
    setIsDecoratingLoading(true);
    try {
      const result = await fetchDecoratedEvent(eventId, opts);
      setDecoratedResult(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setIsDecoratingLoading(false);
    }
  };

  const handleToggleDecorator = async (key) => {
    const nextOpts = { ...decoratorOptions, [key]: !decoratorOptions[key] };
    setDecoratorOptions(nextOpts);
    if (decoratingEvent) {
      await loadDecoratedPreview(decoratingEvent.id, nextOpts);
    }
  };

  const handleGoalChange = async (val) => {
    const nextOpts = { ...decoratorOptions, fundraisingGoal: val };
    setDecoratorOptions(nextOpts);
    if (decoratingEvent) {
      await loadDecoratedPreview(decoratingEvent.id, nextOpts);
    }
  };

  // --- Communication / Reminders Actions ---
  const openCommunicationModal = (ev) => {
    setCommunicatingEvent(ev);
    setCommResult(null);
    setCommForm({
      subject: `Event Reminder: ${ev.title}`,
      message: `Dear Attendee,\n\nThis is a friendly reminder that "${ev.title}" will take place on ${ev.date} at ${ev.location || "the scheduled venue"}.\n\nPlease arrive on time and present your ticket/confirmation at the check-in desk.\n\nBest regards,\nNon-Profit Events Team`,
      type: "REMINDER",
    });
  };

  const handleSendCommunication = async (e) => {
    e.preventDefault();
    if (!communicatingEvent) return;
    setIsSendingComm(true);
    setError(null);
    try {
      const result = await sendEventCommunication(communicatingEvent.id, commForm);
      setCommResult(result);
      showToast(`Dispatched to ${result.recipientsCount} attendee(s) via EventEmailNotifier!`);
      await refreshLogs();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSendingComm(false);
    }
  };

  // --- Tickets actions ---
  const openAddTicket = () => {
    setEditingTicket({
      id: null,
      name: "",
      price: "",
      eventId: events[0] ? String(events[0].id) : "",
    });
  };

  const openEditTicket = (row) => {
    setEditingTicket({
      id: row.id,
      name: row.name,
      price: row.price != null ? String(row.price) : "",
      eventId: row.eventId ? String(row.eventId) : "",
    });
  };

  const saveTicket = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      const payload = {
        name: editingTicket.name,
        price: Number(editingTicket.price),
        eventId: Number(editingTicket.eventId),
      };

      if (editingTicket.id === null) {
        await createTicket(payload);
        showToast("Ticket created successfully!");
      } else {
        await updateTicket(editingTicket.id, payload);
        showToast("Ticket updated successfully!");
      }
      setEditingTicket(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  // --- Registrations actions ---
  const openAddRegistration = () => {
    setEditingRegistration({
      id: null,
      attendeeName: "",
      attendanceStatus: "Registered",
      eventId: events[0] ? String(events[0].id) : "",
      ticketId: tickets[0] ? String(tickets[0].id) : "",
    });
  };

  const openEditRegistration = (row) => {
    setEditingRegistration({
      id: row.id,
      attendeeName: row.attendeeName,
      attendanceStatus: row.attendanceStatus || "Registered",
      eventId: row.eventId ? String(row.eventId) : "",
      ticketId: row.ticketId ? String(row.ticketId) : "",
    });
  };

  const saveRegistration = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      const payload = {
        attendeeName: editingRegistration.attendeeName,
        attendanceStatus: editingRegistration.attendanceStatus,
        eventId: Number(editingRegistration.eventId),
        ticketId: editingRegistration.ticketId ? Number(editingRegistration.ticketId) : null,
      };

      if (editingRegistration.id === null) {
        await createEventRegistration(payload);
        showToast("Attendee registered successfully!");
      } else {
        await updateEventRegistration(editingRegistration.id, payload);
        showToast("Registration updated successfully!");
      }
      setEditingRegistration(null);
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

  const openEditCategory = (row) => {
    setEditingCategory({ id: row.id, name: row.name });
  };

  const saveCategory = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      if (editingCategory.id === null) {
        await createEventCategory({ name: editingCategory.name });
        showToast("Event Category created successfully!");
      } else {
        await updateEventCategory(editingCategory.id, { name: editingCategory.name });
        showToast("Event Category updated successfully!");
      }
      setEditingCategory(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  // --- Delete confirmation ---
  const confirmDelete = async () => {
    if (!deletingItem) return;
    setIsDeleting(true);
    setError(null);
    try {
      if (deletingItem.type === "event") {
        await deleteEvent(deletingItem.id);
        showToast("Event deleted and cancellation dispatched to observers.");
      } else if (deletingItem.type === "category") {
        await deleteEventCategory(deletingItem.id);
        showToast("Event category deleted.");
      } else if (deletingItem.type === "ticket") {
        await deleteTicket(deletingItem.id);
        showToast("Ticket deleted.");
      } else if (deletingItem.type === "registration") {
        await deleteEventRegistration(deletingItem.id);
        showToast("Registration removed.");
      }
      setDeletingItem(null);
      await loadAll();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsDeleting(false);
    }
  };

  // Filtered lists
  const visibleEvents = events.filter((e) =>
    `${e.title} ${e.categoryName || ""} ${e.location || ""} ${e.status || ""}`
      .toLowerCase()
      .includes(query.toLowerCase())
  );

  const visibleTickets = tickets.filter((t) =>
    `${t.name} ${t.eventTitle || ""}`
      .toLowerCase()
      .includes(query.toLowerCase())
  );

  const visibleRegistrations = registrations.filter((r) =>
    `${r.attendeeName} ${r.eventTitle || ""} ${r.ticketName || ""} ${r.attendanceStatus || ""}`
      .toLowerCase()
      .includes(query.toLowerCase())
  );

  const visibleCategories = categories.filter((c) =>
    c.name.toLowerCase().includes(query.toLowerCase())
  );

  const visibleLogs = observerLogs.filter((l) =>
    l.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <>
      <header className="page-head">
        <div>
          <h1>Events & Ticketing</h1>
          <p>
            {events.length} events · {tickets.length} tickets · {registrations.length} registrations · {categories.length} categories
          </p>
        </div>
        <div style={{ display: "flex", gap: "8px" }}>
          {activeTab === "events" ? (
            <button className="btn primary" onClick={openAddEvent}>
              + Add Event
            </button>
          ) : activeTab === "tickets" ? (
            <button className="btn primary" onClick={openAddTicket}>
              + Add Ticket
            </button>
          ) : activeTab === "registrations" ? (
            <button className="btn primary" onClick={openAddRegistration}>
              + Register Attendee
            </button>
          ) : activeTab === "categories" ? (
            <button className="btn primary" onClick={openAddCategory}>
              + Add Category
            </button>
          ) : (
            <button className="btn" onClick={refreshLogs}>
              ↻ Refresh Logs
            </button>
          )}
        </div>
      </header>

      {toastMessage && (
        <div
          style={{
            marginBottom: "16px",
            padding: "10px 16px",
            borderRadius: "6px",
            background: "#e6f4ea",
            color: "#137333",
            border: "1px solid #ceead6",
            fontSize: "0.95rem",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <span>✓ {toastMessage}</span>
          <button
            onClick={() => setToastMessage(null)}
            style={{
              background: "none",
              border: 0,
              cursor: "pointer",
              fontWeight: "bold",
              color: "#137333",
            }}
          >
            ✕
          </button>
        </div>
      )}

      {error && (
        <p className="empty" style={{ color: "var(--danger)", marginBottom: "16px" }}>
          {error}
        </p>
      )}

      {/* Tabs */}
      <div
        style={{
          display: "flex",
          gap: "8px",
          marginBottom: "16px",
          borderBottom: "1px solid var(--line)",
          paddingBottom: "8px",
          flexWrap: "wrap",
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
          className={`btn ${activeTab === "tickets" ? "primary" : ""}`}
          onClick={() => {
            setActiveTab("tickets");
            setQuery("");
          }}
        >
          Tickets ({tickets.length})
        </button>
        <button
          className={`btn ${activeTab === "registrations" ? "primary" : ""}`}
          onClick={() => {
            setActiveTab("registrations");
            setQuery("");
          }}
        >
          Registrations ({registrations.length})
        </button>
        <button
          className={`btn ${activeTab === "categories" ? "primary" : ""}`}
          onClick={() => {
            setActiveTab("categories");
            setQuery("");
          }}
        >
          Categories ({categories.length})
        </button>
        <button
          className={`btn ${activeTab === "observer" ? "primary" : ""}`}
          onClick={() => {
            setActiveTab("observer");
            setQuery("");
            refreshLogs();
          }}
        >
          Observer Logs ({observerLogs.length})
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
                    <th>Pattern Actions</th>
                    <th>Manage</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleEvents.map((r) => {
                    const eventRegs = registrations.filter((reg) => reg.eventId === r.id);
                    return (
                      <tr key={r.id}>
                        <td><strong>{r.title}</strong></td>
                        <td>{r.categoryName || "—"}</td>
                        <td>{r.date}</td>
                        <td>{r.location || "—"}</td>
                        <td>
                          <span className={pillClass(r.status)}>{r.status}</span>
                        </td>
                        <td>
                          <div style={{ display: "flex", gap: "6px" }}>
                            <button
                              className="btn"
                              style={{ padding: "4px 8px", fontSize: "0.82rem" }}
                              title="Demo GoF Decorator Pattern for this Event"
                              onClick={() => openDecoratorModal(r)}
                            >
                              🎨 Decorator
                            </button>
                            <button
                              className="btn"
                              style={{ padding: "4px 8px", fontSize: "0.82rem" }}
                              title="Send reminder/communication to attendees via EventObserver"
                              onClick={() => openCommunicationModal(r)}
                            >
                              ✉️ Remind ({eventRegs.length})
                            </button>
                          </div>
                        </td>
                        <td>
                          <div className="row-actions">
                            <button
                              className="btn-icon"
                              title="Edit Event"
                              onClick={() => openEditEvent(r)}
                            >
                              ✎
                            </button>
                            <button
                              className="btn-icon"
                              title="Delete Event"
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
                    );
                  })}
                  {visibleEvents.length === 0 && (
                    <tr>
                      <td colSpan={7} className="empty">
                        No events found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </>
          ) : activeTab === "tickets" ? (
            <>
              <table>
                <thead>
                  <tr>
                    <th>Ticket Name</th>
                    <th>Price</th>
                    <th>Associated Event</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleTickets.map((t) => (
                    <tr key={t.id}>
                      <td><strong>{t.name}</strong></td>
                      <td>${Number(t.price).toLocaleString()}</td>
                      <td>{t.eventTitle || "—"}</td>
                      <td>
                        <div className="row-actions">
                          <button
                            className="btn-icon"
                            title="Edit"
                            onClick={() => openEditTicket(t)}
                          >
                            ✎
                          </button>
                          <button
                            className="btn-icon"
                            title="Delete"
                            onClick={() =>
                              setDeletingItem({
                                type: "ticket",
                                id: t.id,
                                name: t.name,
                              })
                            }
                          >
                            ✕
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {visibleTickets.length === 0 && (
                    <tr>
                      <td colSpan={4} className="empty">
                        No tickets found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </>
          ) : activeTab === "registrations" ? (
            <>
              <table>
                <thead>
                  <tr>
                    <th>Attendee Name</th>
                    <th>Event</th>
                    <th>Ticket</th>
                    <th>Attendance Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleRegistrations.map((rg) => (
                    <tr key={rg.id}>
                      <td><strong>{rg.attendeeName}</strong></td>
                      <td>{rg.eventTitle || "—"}</td>
                      <td>{rg.ticketName || "Standard Entry"}</td>
                      <td>
                        <span className={pillClass(rg.attendanceStatus)}>
                          {rg.attendanceStatus || "Registered"}
                        </span>
                      </td>
                      <td>
                        <div className="row-actions">
                          <button
                            className="btn-icon"
                            title="Edit"
                            onClick={() => openEditRegistration(rg)}
                          >
                            ✎
                          </button>
                          <button
                            className="btn-icon"
                            title="Delete"
                            onClick={() =>
                              setDeletingItem({
                                type: "registration",
                                id: rg.id,
                                name: rg.attendeeName,
                              })
                            }
                          >
                            ✕
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {visibleRegistrations.length === 0 && (
                    <tr>
                      <td colSpan={5} className="empty">
                        No registrations found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </>
          ) : activeTab === "categories" ? (
            <>
              <table>
                <thead>
                  <tr>
                    <th>Category Name</th>
                    <th>Associated Events Count</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleCategories.map((c) => {
                    const count = events.filter((e) => e.categoryId === c.id).length;
                    return (
                      <tr key={c.id}>
                        <td><strong>{c.name}</strong></td>
                        <td>{count}</td>
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
                    );
                  })}
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
          ) : (
            /* Observer Tab */
            <>
              <div style={{ padding: "16px", borderBottom: "1px solid var(--line)" }}>
                <strong>Event Observer Notification Pipeline</strong>
                <p style={{ margin: "4px 0 0", color: "var(--muted)", fontSize: "0.9rem" }}>
                  Real-time notification dispatches executed by <code>EventSubject</code> and delivered to <code>EventEmailNotifier</code>.
                </p>
              </div>
              <table>
                <thead>
                  <tr>
                    <th style={{ width: "60px" }}>#</th>
                    <th>Observer Notification Dispatch Entry</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleLogs.map((log, idx) => (
                    <tr key={idx}>
                      <td style={{ color: "var(--muted)" }}>{idx + 1}</td>
                      <td>
                        <code style={{ fontSize: "0.85rem", wordBreak: "break-word" }}>{log}</code>
                      </td>
                    </tr>
                  ))}
                  {visibleLogs.length === 0 && (
                    <tr>
                      <td colSpan={2} className="empty">
                        No observer events logged yet. Creating/updating an event or sending a reminder will record dispatches here.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </>
          )}
        </div>
      </section>

      {/* ============================================================ */}
      {/* Modal: Decorator Pattern Live Interactive Demo */}
      {/* ============================================================ */}
      {decoratingEvent && (
        <div className="modal-backdrop">
          <div className="modal-card document-modal" style={{ maxWidth: "680px" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
              <div>
                <span className="pill good" style={{ marginBottom: "6px", display: "inline-block" }}>
                  Design Pattern: Decorator (GoF)
                </span>
                <h2 style={{ margin: "4px 0" }}>Dynamic Event Description Decorator</h2>
                <p style={{ margin: "2px 0 12px", color: "var(--muted)", fontSize: "0.9rem" }}>
                  Demonstrates runtime structural decoration wrapping <code>EventComponent</code> with concrete decorators.
                </p>
              </div>
              <button
                type="button"
                className="btn-icon"
                onClick={() => setDecoratingEvent(null)}
              >
                ✕
              </button>
            </div>

            {/* Event Header info */}
            <div
              style={{
                background: "#f4f7f9",
                padding: "10px 14px",
                borderRadius: "6px",
                marginBottom: "14px",
                fontSize: "0.9rem",
              }}
            >
              <strong>Target Event:</strong> {decoratingEvent.title} ({decoratingEvent.date} @ {decoratingEvent.location || "TBD"})
            </div>

            {/* Pattern Checkbox Toggles */}
            <div style={{ marginBottom: "14px" }}>
              <strong style={{ fontSize: "0.9rem", display: "block", marginBottom: "8px" }}>
                Select Active Decorators:
              </strong>

              <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
                <label style={{ display: "flex", alignItems: "flex-start", gap: "8px", cursor: "pointer" }}>
                  <input
                    type="checkbox"
                    checked={decoratorOptions.fundraising}
                    onChange={() => handleToggleDecorator("fundraising")}
                  />
                  <div>
                    <strong>FundraisingDecorator</strong>
                    <div style={{ fontSize: "0.82rem", color: "var(--muted)" }}>
                      Decorates description with fundraising target goals, donation appeals, and donor tier rewards.
                    </div>
                  </div>
                </label>

                {decoratorOptions.fundraising && (
                  <div style={{ marginLeft: "24px", marginTop: "2px", display: "flex", alignItems: "center", gap: "8px" }}>
                    <span style={{ fontSize: "0.85rem" }}>Target Goal:</span>
                    <input
                      type="text"
                      value={decoratorOptions.fundraisingGoal}
                      onChange={(e) => handleGoalChange(e.target.value)}
                      style={{ padding: "4px 8px", width: "120px", fontSize: "0.85rem" }}
                    />
                  </div>
                )}

                <label style={{ display: "flex", alignItems: "flex-start", gap: "8px", cursor: "pointer" }}>
                  <input
                    type="checkbox"
                    checked={decoratorOptions.reminder}
                    onChange={() => handleToggleDecorator("reminder")}
                  />
                  <div>
                    <strong>ReminderDecorator</strong>
                    <div style={{ fontSize: "0.82rem", color: "var(--muted)" }}>
                      Decorates description with schedule arrival notices, check-in instructions, and QR badge alerts.
                    </div>
                  </div>
                </label>

                <label style={{ display: "flex", alignItems: "flex-start", gap: "8px", cursor: "pointer" }}>
                  <input
                    type="checkbox"
                    checked={decoratorOptions.followup}
                    onChange={() => handleToggleDecorator("followup")}
                  />
                  <div>
                    <strong>FollowUpDecorator</strong>
                    <div style={{ fontSize: "0.82rem", color: "var(--muted)" }}>
                      Decorates description with post-event feedback surveys, thank-you notes, and certificates.
                    </div>
                  </div>
                </label>
              </div>
            </div>

            {/* Pattern Chain Visualization */}
            <div
              style={{
                fontSize: "0.82rem",
                background: "#eef2f6",
                padding: "8px 12px",
                borderRadius: "6px",
                marginBottom: "12px",
                color: "#2c3e50",
              }}
            >
              <strong>Decorator Chain: </strong>
              <code>Event (ConcreteComponent)</code>
              {decoratorOptions.fundraising && <span> → <code>FundraisingDecorator</code></span>}
              {decoratorOptions.reminder && <span> → <code>ReminderDecorator</code></span>}
              {decoratorOptions.followup && <span> → <code>FollowUpDecorator</code></span>}
            </div>

            {/* Result display */}
            <strong style={{ fontSize: "0.9rem", display: "block", marginBottom: "4px" }}>
              Decorated Output (from Backend):
            </strong>
            <pre
              className="document-content"
              style={{
                maxHeight: "220px",
                fontSize: "0.85rem",
                background: "#fafbfc",
                border: "1px solid var(--line)",
              }}
            >
              {isDecoratingLoading
                ? "Calculating decorator composition…"
                : decoratedResult?.decoratedDescription || "No decorators applied."}
            </pre>

            <div className="modal-actions" style={{ marginTop: "14px" }}>
              <button
                type="button"
                className="btn"
                onClick={() => {
                  navigator.clipboard.writeText(decoratedResult?.decoratedDescription || "");
                  showToast("Copied decorated text to clipboard!");
                }}
              >
                📋 Copy Text
              </button>
              <button
                type="button"
                className="btn primary"
                onClick={() => setDecoratingEvent(null)}
              >
                Done
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ============================================================ */}
      {/* Modal: Send Reminder / Communication to Attendees */}
      {/* ============================================================ */}
      {communicatingEvent && (
        <div className="modal-backdrop">
          <form className="modal-card" style={{ maxWidth: "560px" }} onSubmit={handleSendComm}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
              <div>
                <span className="pill warn" style={{ marginBottom: "6px", display: "inline-block" }}>
                  Observer Pattern: EventSubject & EventEmailNotifier
                </span>
                <h2 style={{ margin: "4px 0" }}>Send Attendee Communication</h2>
              </div>
              <button
                type="button"
                className="btn-icon"
                onClick={() => setCommunicatingEvent(null)}
              >
                ✕
              </button>
            </div>

            <p style={{ margin: "2px 0 12px", color: "var(--muted)", fontSize: "0.9rem" }}>
              Triggers simulated email notices to all attendees registered for <strong>{communicatingEvent.title}</strong>.
            </p>

            {/* Recipient summary */}
            {(() => {
              const eventRegs = registrations.filter((r) => r.eventId === communicatingEvent.id);
              return (
                <div
                  style={{
                    background: "#f4f7f9",
                    padding: "8px 12px",
                    borderRadius: "6px",
                    fontSize: "0.88rem",
                    marginBottom: "12px",
                  }}
                >
                  <strong>Registered Recipients ({eventRegs.length}): </strong>
                  {eventRegs.length > 0
                    ? eventRegs.map((r) => r.attendeeName).join(", ")
                    : "No attendees registered yet (dispatches broadcast notice)"}
                </div>
              );
            })()}

            <label>
              Communication Type
              <select
                value={commForm.type}
                onChange={(e) => setCommForm({ ...commForm, type: e.target.value })}
              >
                <option value="REMINDER">Event Reminder & Check-in</option>
                <option value="SCHEDULE_UPDATE">Schedule / Location Update</option>
                <option value="IMPORTANT_NOTICE">Important Announcement</option>
                <option value="SURVEY">Post-Event Survey & Follow-up</option>
              </select>
            </label>

            <label>
              Subject Line
              <input
                type="text"
                required
                value={commForm.subject}
                onChange={(e) => setCommForm({ ...commForm, subject: e.target.value })}
              />
            </label>

            <label>
              Message Content
              <textarea
                rows={5}
                required
                style={{ width: "100%", fontFamily: "inherit", padding: "8px", borderRadius: "6px", border: "1px solid var(--line)" }}
                value={commForm.message}
                onChange={(e) => setCommForm({ ...commForm, message: e.target.value })}
              />
            </label>

            {commResult && (
              <div
                style={{
                  background: "#e6f4ea",
                  color: "#137333",
                  padding: "8px 12px",
                  borderRadius: "6px",
                  fontSize: "0.88rem",
                  marginTop: "8px",
                }}
              >
                ✓ Sent to {commResult.recipientsCount} recipient(s) at {new Date(commResult.sentAt).toLocaleTimeString()}! Dispatched via <code>EventEmailNotifier</code>.
              </div>
            )}

            <div className="modal-actions" style={{ marginTop: "14px" }}>
              <button
                type="button"
                className="btn"
                onClick={() => setCommunicatingEvent(null)}
              >
                Close
              </button>
              <button
                type="submit"
                className="btn primary"
                disabled={isSendingComm}
              >
                {isSendingComm ? "Dispatching Notices…" : "✉️ Dispatch Notices"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* ============================================================ */}
      {/* Modal: Event Form */}
      {/* ============================================================ */}
      {editingEvent && (
        <div className="modal-backdrop">
          <form className="modal-card" onSubmit={saveEvent}>
            <h2>{editingEvent.id ? "Edit Event" : "Create New Event"}</h2>
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
              Category
              <select
                value={editingEvent.categoryId}
                onChange={(e) =>
                  setEditingEvent({ ...editingEvent, categoryId: e.target.value })
                }
              >
                <option value="">(None)</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
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
                <option value="Cancelled">Cancelled</option>
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
                {isSaving ? "Saving…" : "Save Event"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* ============================================================ */}
      {/* Modal: Ticket Form */}
      {/* ============================================================ */}
      {editingTicket && (
        <div className="modal-backdrop">
          <form className="modal-card" onSubmit={saveTicket}>
            <h2>{editingTicket.id ? "Edit Ticket" : "Add Ticket"}</h2>
            <label>
              Ticket Name
              <input
                type="text"
                required
                value={editingTicket.name}
                onChange={(e) =>
                  setEditingTicket({ ...editingTicket, name: e.target.value })
                }
              />
            </label>
            <label>
              Price ($)
              <input
                type="number"
                min="0"
                step="0.01"
                required
                value={editingTicket.price}
                onChange={(e) =>
                  setEditingTicket({ ...editingTicket, price: e.target.value })
                }
              />
            </label>
            <label>
              Associated Event
              <select
                required
                value={editingTicket.eventId}
                onChange={(e) =>
                  setEditingTicket({ ...editingTicket, eventId: e.target.value })
                }
              >
                {events.map((ev) => (
                  <option key={ev.id} value={ev.id}>
                    {ev.title}
                  </option>
                ))}
              </select>
            </label>
            <div className="modal-actions">
              <button
                type="button"
                className="btn"
                onClick={() => setEditingTicket(null)}
              >
                Cancel
              </button>
              <button type="submit" className="btn primary" disabled={isSaving}>
                {isSaving ? "Saving…" : "Save Ticket"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* ============================================================ */}
      {/* Modal: Registration Form */}
      {/* ============================================================ */}
      {editingRegistration && (
        <div className="modal-backdrop">
          <form className="modal-card" onSubmit={saveRegistration}>
            <h2>{editingRegistration.id ? "Edit Registration" : "Register Attendee"}</h2>
            <label>
              Attendee Name
              <input
                type="text"
                required
                value={editingRegistration.attendeeName}
                onChange={(e) =>
                  setEditingRegistration({
                    ...editingRegistration,
                    attendeeName: e.target.value,
                  })
                }
              />
            </label>
            <label>
              Event
              <select
                required
                value={editingRegistration.eventId}
                onChange={(e) =>
                  setEditingRegistration({
                    ...editingRegistration,
                    eventId: e.target.value,
                  })
                }
              >
                {events.map((ev) => (
                  <option key={ev.id} value={ev.id}>
                    {ev.title}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Ticket (Optional)
              <select
                value={editingRegistration.ticketId}
                onChange={(e) =>
                  setEditingRegistration({
                    ...editingRegistration,
                    ticketId: e.target.value,
                  })
                }
              >
                <option value="">No Ticket</option>
                {tickets
                  .filter((t) => String(t.eventId) === String(editingRegistration.eventId))
                  .map((t) => (
                    <option key={t.id} value={t.id}>
                      {t.name} (${t.price})
                    </option>
                  ))}
              </select>
            </label>
            <label>
              Attendance Status
              <select
                value={editingRegistration.attendanceStatus}
                onChange={(e) =>
                  setEditingRegistration({
                    ...editingRegistration,
                    attendanceStatus: e.target.value,
                  })
                }
              >
                <option value="Registered">Registered</option>
                <option value="Confirmed">Confirmed</option>
                <option value="Attended">Attended</option>
                <option value="Cancelled">Cancelled</option>
              </select>
            </label>
            <div className="modal-actions">
              <button
                type="button"
                className="btn"
                onClick={() => setEditingRegistration(null)}
              >
                Cancel
              </button>
              <button type="submit" className="btn primary" disabled={isSaving}>
                {isSaving ? "Saving…" : "Save Registration"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* ============================================================ */}
      {/* Modal: Category Form */}
      {/* ============================================================ */}
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
                {isSaving ? "Saving…" : "Save Category"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* ============================================================ */}
      {/* Confirm Delete Modal */}
      {/* ============================================================ */}
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
