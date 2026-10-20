const API_URL = "http://localhost:8080/api";

// Calls the single Spring Boot endpoint to confirm the backend is reachable.
export async function fetchStatus() {
  const res = await fetch(`${API_URL}/status`);
  if (!res.ok) throw new Error(`Backend responded with ${res.status}`);
  return res.json();
}
