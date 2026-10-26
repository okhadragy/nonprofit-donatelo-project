const API_URL = "http://localhost:8080/api";


export async function authenticatedFetch(path, options = {}) {
  const token = localStorage.getItem("token");

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      ...options.headers,
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  });

  if (response.status === 401) {
    localStorage.removeItem("token");
    window.location.reload();
    throw new Error("Your session has expired. Please log in again.");
  }

  return response;
}


export async function fetchDonationTypes() {
  const res = await authenticatedFetch(`/donation-types`);
  if (!res.ok) throw new Error(`Failed to fetch donation types`);
  return res.json();
}


export async function fetchStatus() {
  const res = await authenticatedFetch(`/status`);
  if (!res.ok) throw new Error(`Backend responded with ${res.status}`);
  return res.json();
}

async function authRequest(endpoint, body) {
  const response = await fetch(`${API_URL}/auth/${endpoint}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  const result = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(
      result.message || result.error || "Authentication failed."
    );
  }

  return result;
}

export function login(credentials) {
  return authRequest("login", credentials);
}

export function register(user) {
  return authRequest("register", user);
}

