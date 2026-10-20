// One entry per page. The table columns and the add/edit form are both built from `fields`.
const money = (v) => `$${Number(v).toLocaleString()}`;

export const resources = {
  donations: {
    label: "Donations",
    singular: "donation",
    blurb: (rows) => `${rows.length} gifts, ${money(rows.reduce((s, r) => s + Number(r.amount), 0))} received`,
    fields: [
      { key: "donor", label: "Donor", type: "text" },
      { key: "amount", label: "Amount (EGP)", type: "number", format: money },
      { key: "date", label: "Date", type: "date" },
      { key: "method", label: "Method", type: "select", options: ["Cash", "Card", "Bank transfer"] },
    ],
  },
  volunteers: {
    label: "Volunteers",
    singular: "volunteer",
    blurb: (rows) => `${rows.filter((r) => r.status === "Active").length} active of ${rows.length}`,
    fields: [
      { key: "name", label: "Name", type: "text" },
      { key: "email", label: "Email", type: "email" },
      { key: "role", label: "Role", type: "text" },
      { key: "status", label: "Status", type: "select", options: ["Active", "Inactive"], pill: true },
    ],
  },
  events: {
    label: "Events",
    singular: "event",
    blurb: (rows) => `${rows.filter((r) => r.status === "Planned").length} planned, ${rows.length} in total`,
    fields: [
      { key: "title", label: "Title", type: "text" },
      { key: "date", label: "Date", type: "date" },
      { key: "location", label: "Location", type: "text" },
      { key: "status", label: "Status", type: "select", options: ["Planned", "Ongoing", "Completed"], pill: true },
    ],
  },
  beneficiaries: {
    label: "Beneficiaries",
    singular: "beneficiary",
    blurb: (rows) => `${rows.length} people and families supported`,
    fields: [
      { key: "name", label: "Name", type: "text" },
      { key: "city", label: "City", type: "text" },
      { key: "category", label: "Category", type: "select", options: ["Family", "Student", "Elder", "Orphan"] },
      { key: "status", label: "Status", type: "select", options: ["Active", "Waitlist"], pill: true },
    ],
  },
};
