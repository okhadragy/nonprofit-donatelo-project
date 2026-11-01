import { useState, useEffect } from "react";
import {
  fetchVolunteers,
  updateVolunteer,
  deleteVolunteer,
  fetchVolunteerRoles,
  fetchVolunteerQualifications,
  fetchEvents,
  fetchVolunteerAssignmentsByEvent,
  fetchRolesForEvent,
  fetchQualificationsForEvent,
  fetchVolunteerAssignmentsByVolunteer,
  createVolunteerAssignment,
  updateVolunteerAssignment,
  deleteVolunteerAssignment,
} from "../api.js";
import ConfirmModal from "./ConfirmModal.jsx";

const pillClass = (value) =>
  ["Active", "Confirmed", "Attended"].includes(value)
    ? "pill good"
    : ["PENDING", "Pending", "Waitlist", "Inactive"].includes(value)
    ? "pill warn"
    : "pill";

export default function VolunteerPage() {
  const [activeTab, setActiveTab] = useState("volunteers");

  // Master data
  const [volunteers, setVolunteers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [qualifications, setQualifications] = useState([]);
  const [events, setEvents] = useState([]);

  // Volunteer Filters
  const [searchQuery, setSearchQuery] = useState("");
  const [filterRoleId, setFilterRoleId] = useState("");
  const [filterQualId, setFilterQualId] = useState("");

  // Event Staffing state
  const [selectedEventId, setSelectedEventId] = useState("");
  const [eventAssignments, setEventAssignments] = useState([]);
  const [eventRoles, setEventRoles] = useState([]);
  const [eventQualifications, setEventQualifications] = useState([]);
  const [loadingAssignments, setLoadingAssignments] = useState(false);

  // Modals
  const [editingVolunteer, setEditingVolunteer] = useState(null);
  const [assignmentModal, setAssignmentModal] = useState(null);
  const [historyModal, setHistoryModal] = useState(null);
  const [deletingItem, setDeletingItem] = useState(null);

  const [loading, setLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  const showSuccess = (msg) => {
    setSuccessMessage(msg);
    setTimeout(() => setSuccessMessage(null), 3500);
  };

  // 1. Initial Load
  const loadMasterData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [vData, rData, qData, eData] = await Promise.all([
        fetchVolunteers(),
        fetchVolunteerRoles().catch(() => []),
        fetchVolunteerQualifications().catch(() => []),
        fetchEvents().catch(() => []),
      ]);
      setVolunteers(vData);
      setRoles(rData);
      setQualifications(qData);
      setEvents(eData);
      if (eData.length > 0 && !selectedEventId) {
        setSelectedEventId(String(eData[0].id));
      }
    } catch (err) {
      setError(err.message || "Failed to load volunteer data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMasterData();
  }, []);

  // 2. Fetch volunteers with role/qualification filters
  const applyFilters = async () => {
    setError(null);
    try {
      const options = {};
      if (filterRoleId) {
        options.roleIds = [Number(filterRoleId)];
      }
      if (filterQualId) {
        options.qualificationIds = [Number(filterQualId)];
      }
      const data = await fetchVolunteers(options);
      setVolunteers(data);
    } catch (err) {
      setError(err.message || "Failed to filter volunteers");
    }
  };

  useEffect(() => {
    if (!loading) {
      applyFilters();
    }
  }, [filterRoleId, filterQualId]);

  const resetFilters = () => {
    setSearchQuery("");
    setFilterRoleId("");
    setFilterQualId("");
  };

  // 3. Load Event Staffing Data when event changes
  const loadEventStaffing = async (eventId) => {
    if (!eventId) {
      setEventAssignments([]);
      setEventRoles([]);
      setEventQualifications([]);
      return;
    }
    setLoadingAssignments(true);
    try {
      const [assignments, assignedRoles, onSiteQuals] = await Promise.all([
        fetchVolunteerAssignmentsByEvent(eventId),
        fetchRolesForEvent(eventId).catch(() => []),
        fetchQualificationsForEvent(eventId).catch(() => []),
      ]);
      setEventAssignments(assignments);
      setEventRoles(assignedRoles);
      setEventQualifications(onSiteQuals);
    } catch (err) {
      setError(err.message || "Failed to load event staffing details");
    } finally {
      setLoadingAssignments(false);
    }
  };

  useEffect(() => {
    if (activeTab === "assignments" && selectedEventId) {
      loadEventStaffing(selectedEventId);
    }
  }, [selectedEventId, activeTab]);

  // --- Volunteer Actions ---

  const openEditVolunteer = (v) => {
    setEditingVolunteer({
      id: v.id,
      name: v.name,
      status: v.status || "Active",
      preferredRoleIds: v.preferredRoleIds ? [...v.preferredRoleIds] : [],
      qualificationIds: v.qualificationIds ? [...v.qualificationIds] : [],
    });
  };

  const toggleRole = (roleId) => {
    if (!editingVolunteer) return;
    const current = editingVolunteer.preferredRoleIds || [];
    const next = current.includes(roleId)
      ? current.filter((id) => id !== roleId)
      : [...current, roleId];
    setEditingVolunteer({ ...editingVolunteer, preferredRoleIds: next });
  };

  const toggleQualification = (qualId) => {
    if (!editingVolunteer) return;
    const current = editingVolunteer.qualificationIds || [];
    const next = current.includes(qualId)
      ? current.filter((id) => id !== qualId)
      : [...current, qualId];
    setEditingVolunteer({ ...editingVolunteer, qualificationIds: next });
  };

  const saveVolunteer = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      await updateVolunteer(editingVolunteer.id, {
        name: editingVolunteer.name,
        status: editingVolunteer.status,
        preferredRoleIds: editingVolunteer.preferredRoleIds,
        qualificationIds: editingVolunteer.qualificationIds,
      });
      setEditingVolunteer(null);
      showSuccess("Volunteer profile updated successfully!");
      await applyFilters();
    } catch (err) {
      setError(err.message || "Failed to update volunteer");
    } finally {
      setIsSaving(false);
    }
  };

  const openHistoryModal = async (volunteer) => {
    setError(null);
    try {
      const history = await fetchVolunteerAssignmentsByVolunteer(volunteer.id);
      setHistoryModal({
        volunteer,
        assignments: history,
      });
    } catch (err) {
      setError(err.message || "Failed to fetch volunteer history");
    }
  };

  // --- Assignment Actions ---

  const openAddAssignment = () => {
    if (!selectedEventId) {
      setError("Please select an event first");
      return;
    }
    setAssignmentModal({
      id: null,
      eventId: Number(selectedEventId),
      volunteerId: volunteers[0] ? String(volunteers[0].id) : "",
      roleId: roles[0] ? String(roles[0].id) : "",
      hoursWorked: 0,
    });
  };

  const openEditAssignment = (assignment) => {
    setAssignmentModal({
      id: assignment.id,
      eventId: assignment.eventId || Number(selectedEventId),
      volunteerId: String(assignment.volunteerId),
      roleId: String(assignment.roleId),
      hoursWorked: assignment.hoursWorked || 0,
    });
  };

  const saveAssignment = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    try {
      const payload = {
        eventId: Number(assignmentModal.eventId),
        volunteerId: Number(assignmentModal.volunteerId),
        roleId: Number(assignmentModal.roleId),
        hoursWorked: Number(assignmentModal.hoursWorked),
      };

      if (assignmentModal.id) {
        await updateVolunteerAssignment(assignmentModal.id, payload);
        showSuccess("Volunteer assignment updated!");
      } else {
        await createVolunteerAssignment(payload);
        showSuccess("Volunteer assigned to event successfully!");
      }
      setAssignmentModal(null);
      await loadEventStaffing(selectedEventId);
    } catch (err) {
      setError(err.message || "Failed to save assignment");
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
      if (deletingItem.type === "volunteer") {
        await deleteVolunteer(deletingItem.id);
        showSuccess(`Volunteer "${deletingItem.name}" deleted.`);
        await applyFilters();
      } else if (deletingItem.type === "assignment") {
        await deleteVolunteerAssignment(deletingItem.id);
        showSuccess("Volunteer unassigned from event.");
        await loadEventStaffing(selectedEventId);
      }
      setDeletingItem(null);
    } catch (err) {
      setError(err.message || "Failed to delete item");
    } finally {
      setIsDeleting(false);
    }
  };

  // Filtered volunteers for display
  const visibleVolunteers = volunteers.filter((v) => {
    const term = searchQuery.toLowerCase();
    const nameMatch = (v.name || "").toLowerCase().includes(term);
    const emailMatch = (v.email || "").toLowerCase().includes(term);
    const userMatch = (v.username || "").toLowerCase().includes(term);
    const roleMatch = (v.preferredRoles || []).some((r) =>
      r.toLowerCase().includes(term)
    );
    const qualMatch = (v.qualifications || []).some((q) =>
      q.toLowerCase().includes(term)
    );
    return nameMatch || emailMatch || userMatch || roleMatch || qualMatch;
  });

  const selectedEventObj = events.find(
    (e) => String(e.id) === String(selectedEventId)
  );

  const totalEventHours = eventAssignments.reduce(
    (sum, a) => sum + (Number(a.hoursWorked) || 0),
    0
  );

  return (
    <>
      <header className="page-head">
        <div>
          <h1>Volunteer Management</h1>
          <p>
            {volunteers.length} registered volunteers · {events.length} events ·{" "}
            {roles.length} roles cataloged
          </p>
        </div>
        <div style={{ display: "flex", gap: "8px" }}>
          {activeTab === "assignments" && (
            <button className="btn primary" onClick={openAddAssignment}>
              + Assign Volunteer to Event
            </button>
          )}
        </div>
      </header>

      {error && (
        <div
          style={{
            background: "#fde8e8",
            color: "var(--danger)",
            padding: "10px 14px",
            borderRadius: "6px",
            marginBottom: "16px",
            fontSize: "0.9rem",
          }}
        >
          {error}
        </div>
      )}

      {successMessage && (
        <div
          style={{
            background: "#e1f2e9",
            color: "#1f6b45",
            padding: "10px 14px",
            borderRadius: "6px",
            marginBottom: "16px",
            fontSize: "0.9rem",
          }}
        >
          {successMessage}
        </div>
      )}

      {/* Tabs */}
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
          className={`btn ${activeTab === "volunteers" ? "primary" : ""}`}
          onClick={() => setActiveTab("volunteers")}
        >
          Volunteers List ({volunteers.length})
        </button>
        <button
          className={`btn ${activeTab === "assignments" ? "primary" : ""}`}
          onClick={() => setActiveTab("assignments")}
        >
          Event Staffing & Assignments
        </button>
        <button
          className={`btn ${activeTab === "catalog" ? "primary" : ""}`}
          onClick={() => setActiveTab("catalog")}
        >
          Roles & Qualifications Directory
        </button>
      </div>

      {/* TAB 1: VOLUNTEERS */}
      {activeTab === "volunteers" && (
        <section className="panel">
          {/* Filter Toolbar */}
          <div
            className="toolbar"
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: "12px",
              alignItems: "center",
            }}
          >
            <input
              type="search"
              placeholder="Search by name, email, or skill…"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ flex: "1 1 220px", maxWidth: "320px" }}
            />

            {/* Filter by Role */}
            <select
              value={filterRoleId}
              onChange={(e) => setFilterRoleId(e.target.value)}
              style={{ minWidth: "160px" }}
            >
              <option value="">All Roles</option>
              {roles.map((r) => (
                <option key={r.id} value={r.id}>
                  Role: {r.name}
                </option>
              ))}
            </select>

            {/* Filter by Qualification */}
            <select
              value={filterQualId}
              onChange={(e) => setFilterQualId(e.target.value)}
              style={{ minWidth: "170px" }}
            >
              <option value="">All Qualifications</option>
              {qualifications.map((q) => (
                <option key={q.id} value={q.id}>
                  Skill: {q.name}
                </option>
              ))}
            </select>

            {(filterRoleId || filterQualId || searchQuery) && (
              <button className="btn small" onClick={resetFilters}>
                Clear Filters
              </button>
            )}

            <div style={{ marginLeft: "auto", fontSize: "0.85rem", color: "var(--muted)" }}>
              Showing {visibleVolunteers.length} of {volunteers.length}
            </div>
          </div>

          <div className="table-wrap">
            {loading ? (
              <p className="empty">Loading volunteers…</p>
            ) : visibleVolunteers.length === 0 ? (
              <p className="empty">No volunteers match your criteria.</p>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>Volunteer</th>
                    <th>Status</th>
                    <th>Preferred Roles</th>
                    <th>Qualifications</th>
                    <th className="actions">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleVolunteers.map((v) => (
                    <tr key={v.id}>
                      <td>
                        <strong>{v.name}</strong>
                        <div style={{ fontSize: "0.8rem", color: "var(--muted)" }}>
                          @{v.username} · {v.email}
                        </div>
                      </td>
                      <td>
                        <span className={pillClass(v.status)}>{v.status || "Active"}</span>
                      </td>
                      <td>
                        {v.preferredRoles && v.preferredRoles.length > 0 ? (
                          <div style={{ display: "flex", flexWrap: "wrap", gap: "4px" }}>
                            {v.preferredRoles.map((roleName, idx) => (
                              <span
                                key={idx}
                                className="pill"
                                style={{
                                  background: "#edf4fc",
                                  color: "#185a9d",
                                  fontSize: "0.78rem",
                                }}
                              >
                                {roleName}
                              </span>
                            ))}
                          </div>
                        ) : (
                          <span style={{ color: "var(--muted)", fontSize: "0.85rem" }}>None</span>
                        )}
                      </td>
                      <td>
                        {v.qualifications && v.qualifications.length > 0 ? (
                          <div style={{ display: "flex", flexWrap: "wrap", gap: "4px" }}>
                            {v.qualifications.map((qName, idx) => (
                              <span
                                key={idx}
                                className="pill"
                                style={{
                                  background: "#f3e8fd",
                                  color: "#6b21a8",
                                  fontSize: "0.78rem",
                                }}
                              >
                                {qName}
                              </span>
                            ))}
                          </div>
                        ) : (
                          <span style={{ color: "var(--muted)", fontSize: "0.85rem" }}>None</span>
                        )}
                      </td>
                      <td className="actions">
                        <button
                          className="btn small"
                          title="View event assignment history"
                          onClick={() => openHistoryModal(v)}
                        >
                          History
                        </button>
                        <button
                          className="btn small"
                          onClick={() => openEditVolunteer(v)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn small danger"
                          onClick={() =>
                            setDeletingItem({
                              type: "volunteer",
                              id: v.id,
                              name: v.name,
                            })
                          }
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </section>
      )}

      {/* TAB 2: EVENT STAFFING & ASSIGNMENTS */}
      {activeTab === "assignments" && (
        <div>
          {/* Event Picker Bar */}
          <div
            className="panel"
            style={{
              padding: "16px",
              marginBottom: "16px",
              display: "flex",
              alignItems: "center",
              gap: "16px",
              flexWrap: "wrap",
            }}
          >
            <label style={{ display: "flex", alignItems: "center", gap: "8px", fontWeight: "600" }}>
              Selected Event:
              <select
                value={selectedEventId}
                onChange={(e) => setSelectedEventId(e.target.value)}
                style={{ minWidth: "260px" }}
              >
                {events.map((e) => (
                  <option key={e.id} value={e.id}>
                    {e.title} ({e.date ? e.date.substring(0, 10) : "No Date"})
                  </option>
                ))}
              </select>
            </label>

            {selectedEventObj && (
              <span className={pillClass(selectedEventObj.status)}>
                {selectedEventObj.status}
              </span>
            )}

            <button className="btn primary" style={{ marginLeft: "auto" }} onClick={openAddAssignment}>
              + Assign Volunteer
            </button>
          </div>

          {/* Event Insights Banner (Roles present & Qualifications available) */}
          {selectedEventObj && (
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
                gap: "16px",
                marginBottom: "16px",
              }}
            >
              {/* Card 1: Staffing metrics */}
              <div
                className="panel"
                style={{ padding: "16px", borderLeft: "4px solid var(--accent)" }}
              >
                <div style={{ fontSize: "0.85rem", color: "var(--muted)", fontWeight: 600 }}>
                  EVENT STAFFING SUMMARY
                </div>
                <div style={{ fontSize: "1.4rem", fontWeight: 700, margin: "6px 0" }}>
                  {eventAssignments.length} Volunteers · {totalEventHours} Total Hours
                </div>
                <div style={{ fontSize: "0.85rem", color: "var(--muted)" }}>
                  Location: {selectedEventObj.location || "TBD"}
                </div>
              </div>

              {/* Card 2: Roles present */}
              <div
                className="panel"
                style={{ padding: "16px", borderLeft: "4px solid #185a9d" }}
              >
                <div style={{ fontSize: "0.85rem", color: "#185a9d", fontWeight: 600 }}>
                  ROLES STAFFED FOR THIS EVENT
                </div>
                <div style={{ display: "flex", flexWrap: "wrap", gap: "6px", marginTop: "8px" }}>
                  {eventRoles.length > 0 ? (
                    eventRoles.map((r) => (
                      <span
                        key={r.id}
                        className="pill"
                        style={{ background: "#edf4fc", color: "#185a9d" }}
                      >
                        {r.name}
                      </span>
                    ))
                  ) : (
                    <span style={{ fontSize: "0.85rem", color: "var(--muted)" }}>
                      No roles assigned yet
                    </span>
                  )}
                </div>
              </div>

              {/* Card 3: Qualifications available on-site */}
              <div
                className="panel"
                style={{ padding: "16px", borderLeft: "4px solid #6b21a8" }}
              >
                <div style={{ fontSize: "0.85rem", color: "#6b21a8", fontWeight: 600 }}>
                  ON-SITE QUALIFICATIONS & SKILLS
                </div>
                <div style={{ display: "flex", flexWrap: "wrap", gap: "6px", marginTop: "8px" }}>
                  {eventQualifications.length > 0 ? (
                    eventQualifications.map((q) => (
                      <span
                        key={q.id}
                        className="pill"
                        style={{ background: "#f3e8fd", color: "#6b21a8" }}
                      >
                        {q.name}
                      </span>
                    ))
                  ) : (
                    <span style={{ fontSize: "0.85rem", color: "var(--muted)" }}>
                      No special qualifications present
                    </span>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Assignments Table */}
          <section className="panel">
            <div className="toolbar">
              <strong>Volunteer Assignments for this Event</strong>
            </div>

            <div className="table-wrap">
              {loadingAssignments ? (
                <p className="empty">Loading assignments…</p>
              ) : eventAssignments.length === 0 ? (
                <p className="empty">
                  No volunteers currently assigned to this event. Click "+ Assign Volunteer" to add staff.
                </p>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>Volunteer</th>
                      <th>Assigned Role</th>
                      <th>Hours Worked</th>
                      <th className="actions">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {eventAssignments.map((a) => (
                      <tr key={a.id}>
                        <td>
                          <strong>{a.volunteerName || `Volunteer #${a.volunteerId}`}</strong>
                        </td>
                        <td>
                          <span
                            className="pill"
                            style={{ background: "#edf4fc", color: "#185a9d" }}
                          >
                            {a.roleName || `Role #${a.roleId}`}
                          </span>
                        </td>
                        <td>{a.hoursWorked} hrs</td>
                        <td className="actions">
                          <button
                            className="btn small"
                            onClick={() => openEditAssignment(a)}
                          >
                            Edit Hours / Role
                          </button>
                          <button
                            className="btn small danger"
                            onClick={() =>
                              setDeletingItem({
                                type: "assignment",
                                id: a.id,
                                name: `assignment for ${a.volunteerName}`,
                              })
                            }
                          >
                            Unassign
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </section>
        </div>
      )}

      {/* TAB 3: ROLES & QUALIFICATIONS DIRECTORY */}
      {activeTab === "catalog" && (
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "20px" }}>
          {/* Roles List */}
          <section className="panel">
            <div className="toolbar">
              <strong>Available Volunteer Roles ({roles.length})</strong>
            </div>
            <div className="table-wrap">
              {roles.length === 0 ? (
                <p className="empty">No roles created yet.</p>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>Role Name</th>
                      <th>Description</th>
                    </tr>
                  </thead>
                  <tbody>
                    {roles.map((r) => (
                      <tr key={r.id}>
                        <td>
                          <strong>{r.name}</strong>
                        </td>
                        <td style={{ color: "var(--muted)", fontSize: "0.85rem" }}>
                          {r.description}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </section>

          {/* Qualifications List */}
          <section className="panel">
            <div className="toolbar">
              <strong>Volunteer Qualifications & Certifications ({qualifications.length})</strong>
            </div>
            <div className="table-wrap">
              {qualifications.length === 0 ? (
                <p className="empty">No qualifications created yet.</p>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>Qualification</th>
                      <th>Description</th>
                    </tr>
                  </thead>
                  <tbody>
                    {qualifications.map((q) => (
                      <tr key={q.id}>
                        <td>
                          <strong>{q.name}</strong>
                        </td>
                        <td style={{ color: "var(--muted)", fontSize: "0.85rem" }}>
                          {q.description}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </section>
        </div>
      )}

      {/* MODAL 1: EDIT VOLUNTEER PROFILE */}
      {editingVolunteer && (
        <div className="overlay" onClick={() => setEditingVolunteer(null)}>
          <div
            className="modal"
            style={{ maxWidth: "520px" }}
            onClick={(e) => e.stopPropagation()}
          >
            <h2>Edit Volunteer Profile</h2>

            <form onSubmit={saveVolunteer}>
              <label>
                Full Name
                <input
                  type="text"
                  required
                  value={editingVolunteer.name}
                  onChange={(e) =>
                    setEditingVolunteer({ ...editingVolunteer, name: e.target.value })
                  }
                />
              </label>

              <label style={{ marginTop: "10px" }}>
                Status
                <select
                  value={editingVolunteer.status}
                  onChange={(e) =>
                    setEditingVolunteer({ ...editingVolunteer, status: e.target.value })
                  }
                >
                  <option value="Active">Active</option>
                  <option value="PENDING">Pending Approval</option>
                  <option value="Inactive">Inactive</option>
                </select>
              </label>

              {/* Preferred Roles Checkboxes */}
              <div style={{ marginTop: "14px" }}>
                <span style={{ fontWeight: 600, fontSize: "0.9rem" }}>
                  Preferred Roles
                </span>
                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "1fr 1fr",
                    gap: "6px",
                    marginTop: "6px",
                    background: "#f8fafb",
                    padding: "10px",
                    borderRadius: "6px",
                    border: "1px solid var(--line)",
                    maxHeight: "130px",
                    overflowY: "auto",
                  }}
                >
                  {roles.map((r) => {
                    const checked = editingVolunteer.preferredRoleIds.includes(r.id);
                    return (
                      <label
                        key={r.id}
                        style={{
                          display: "flex",
                          alignItems: "center",
                          gap: "6px",
                          fontWeight: 400,
                          fontSize: "0.85rem",
                          cursor: "pointer",
                        }}
                      >
                        <input
                          type="checkbox"
                          checked={checked}
                          onChange={() => toggleRole(r.id)}
                        />
                        {r.name}
                      </label>
                    );
                  })}
                </div>
              </div>

              {/* Qualifications Checkboxes */}
              <div style={{ marginTop: "14px" }}>
                <span style={{ fontWeight: 600, fontSize: "0.9rem" }}>
                  Qualifications & Skills
                </span>
                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "1fr 1fr",
                    gap: "6px",
                    marginTop: "6px",
                    background: "#f8fafb",
                    padding: "10px",
                    borderRadius: "6px",
                    border: "1px solid var(--line)",
                    maxHeight: "130px",
                    overflowY: "auto",
                  }}
                >
                  {qualifications.map((q) => {
                    const checked = editingVolunteer.qualificationIds.includes(q.id);
                    return (
                      <label
                        key={q.id}
                        style={{
                          display: "flex",
                          alignItems: "center",
                          gap: "6px",
                          fontWeight: 400,
                          fontSize: "0.85rem",
                          cursor: "pointer",
                        }}
                      >
                        <input
                          type="checkbox"
                          checked={checked}
                          onChange={() => toggleQualification(q.id)}
                        />
                        {q.name}
                      </label>
                    );
                  })}
                </div>
              </div>

              <div className="modal-actions" style={{ marginTop: "20px" }}>
                <button
                  type="button"
                  className="btn"
                  onClick={() => setEditingVolunteer(null)}
                  disabled={isSaving}
                >
                  Cancel
                </button>
                <button type="submit" className="btn primary" disabled={isSaving}>
                  {isSaving ? "Saving…" : "Save Changes"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 2: ASSIGN VOLUNTEER TO EVENT */}
      {assignmentModal && (
        <div className="overlay" onClick={() => setAssignmentModal(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>
              {assignmentModal.id
                ? "Update Volunteer Assignment"
                : "Assign Volunteer to Event"}
            </h2>

            <form onSubmit={saveAssignment}>
              <label>
                Event
                <select
                  disabled
                  value={assignmentModal.eventId}
                  style={{ background: "#f2f4f6" }}
                >
                  {events.map((e) => (
                    <option key={e.id} value={e.id}>
                      {e.title}
                    </option>
                  ))}
                </select>
              </label>

              <label style={{ marginTop: "10px" }}>
                Volunteer
                <select
                  required
                  value={assignmentModal.volunteerId}
                  onChange={(e) =>
                    setAssignmentModal({
                      ...assignmentModal,
                      volunteerId: e.target.value,
                    })
                  }
                >
                  {volunteers.map((v) => (
                    <option key={v.id} value={v.id}>
                      {v.name} ({v.preferredRoles?.join(", ") || "No role specified"})
                    </option>
                  ))}
                </select>
              </label>

              <label style={{ marginTop: "10px" }}>
                Role for this Event
                <select
                  required
                  value={assignmentModal.roleId}
                  onChange={(e) =>
                    setAssignmentModal({
                      ...assignmentModal,
                      roleId: e.target.value,
                    })
                  }
                >
                  {roles.map((r) => (
                    <option key={r.id} value={r.id}>
                      {r.name}
                    </option>
                  ))}
                </select>
              </label>

              <label style={{ marginTop: "10px" }}>
                Hours Worked
                <input
                  type="number"
                  step="0.5"
                  min="0"
                  required
                  value={assignmentModal.hoursWorked}
                  onChange={(e) =>
                    setAssignmentModal({
                      ...assignmentModal,
                      hoursWorked: e.target.value,
                    })
                  }
                />
              </label>

              <div className="modal-actions" style={{ marginTop: "16px" }}>
                <button
                  type="button"
                  className="btn"
                  onClick={() => setAssignmentModal(null)}
                  disabled={isSaving}
                >
                  Cancel
                </button>
                <button type="submit" className="btn primary" disabled={isSaving}>
                  {isSaving ? "Saving…" : "Save Assignment"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 3: VOLUNTEER HISTORY */}
      {historyModal && (
        <div className="overlay" onClick={() => setHistoryModal(null)}>
          <div
            className="modal"
            style={{ maxWidth: "560px" }}
            onClick={(e) => e.stopPropagation()}
          >
            <h2>Volunteer History: {historyModal.volunteer.name}</h2>
            <p style={{ margin: "0 0 12px 0", color: "var(--muted)", fontSize: "0.85rem" }}>
              All events and recorded hours for this volunteer:
            </p>

            <div style={{ maxHeight: "300px", overflowY: "auto" }}>
              {historyModal.assignments.length === 0 ? (
                <p className="empty">No assignments found for this volunteer.</p>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>Event</th>
                      <th>Role</th>
                      <th>Hours</th>
                    </tr>
                  </thead>
                  <tbody>
                    {historyModal.assignments.map((a) => (
                      <tr key={a.id}>
                        <td>
                          <strong>{a.eventTitle || `Event #${a.eventId}`}</strong>
                        </td>
                        <td>
                          <span
                            className="pill"
                            style={{ background: "#edf4fc", color: "#185a9d" }}
                          >
                            {a.roleName || "Assigned"}
                          </span>
                        </td>
                        <td>{a.hoursWorked} hrs</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>

            <div className="modal-actions" style={{ marginTop: "16px" }}>
              <button
                type="button"
                className="btn"
                onClick={() => setHistoryModal(null)}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* CONFIRM DELETE MODAL */}
      <ConfirmModal
        isOpen={Boolean(deletingItem)}
        title={
          deletingItem?.type === "volunteer"
            ? `Delete Volunteer "${deletingItem?.name}"?`
            : "Unassign Volunteer?"
        }
        message={
          deletingItem?.type === "volunteer"
            ? "Are you sure you want to delete this volunteer? All their event assignment history will be permanently deleted."
            : "Are you sure you want to remove this volunteer from the event?"
        }
        confirmText="Delete"
        loading={isDeleting}
        onConfirm={confirmDelete}
        onCancel={() => setDeletingItem(null)}
      />
    </>
  );
}

