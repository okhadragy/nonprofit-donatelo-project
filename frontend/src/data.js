// Mock data. Everything except the status check lives here, in the browser.
export const initialData = {
  donations: [
    { id: 1, donor: "Amira Hassan", amount: 500, date: "2026-09-02", method: "Card" },
    { id: 2, donor: "Northgate Bakery", amount: 1200, date: "2026-09-14", method: "Bank transfer" },
    { id: 3, donor: "Daniel Okafor", amount: 75, date: "2026-09-21", method: "Cash" },
    { id: 4, donor: "Lina Petrova", amount: 250, date: "2026-10-03", method: "Card" },
    { id: 5, donor: "Greenline Logistics", amount: 3000, date: "2026-10-11", method: "Bank transfer" },
  ],
  volunteers: [
    { id: 1, name: "Yusuf Karim", email: "yusuf@example.org", role: "Driver", status: "Active" },
    { id: 2, name: "Sara Lindqvist", email: "sara@example.org", role: "Teacher", status: "Active" },
    { id: 3, name: "Mateo Ruiz", email: "mateo@example.org", role: "Event helper", status: "Inactive" },
    { id: 4, name: "Nour Abdallah", email: "nour@example.org", role: "Fundraiser", status: "Active" },
  ],
  events: [
    { id: 1, title: "Autumn Food Drive", date: "2026-10-28", location: "Community Hall", status: "Planned" },
    { id: 2, title: "School Supplies Day", date: "2026-10-18", location: "Central School", status: "Ongoing" },
    { id: 3, title: "Charity Run", date: "2026-09-12", location: "Riverside Park", status: "Completed" },
    { id: 4, title: "Winter Coat Collection", date: "2026-11-15", location: "Main Library", status: "Planned" },
  ],
  beneficiaries: [
    { id: 1, name: "Hadir family", city: "Giza", category: "Family", status: "Active" },
    { id: 2, name: "Omar Fathy", city: "Cairo", category: "Student", status: "Active" },
    { id: 3, name: "Mrs. Salma", city: "Alexandria", category: "Elder", status: "Waitlist" },
    { id: 4, name: "Ali & Mona", city: "Cairo", category: "Orphan", status: "Active" },
  ],
};
