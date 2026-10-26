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

// --- Donation CRUD ---

export async function fetchDonations() {
  const res = await authenticatedFetch(`/donations`);
  if (!res.ok) throw new Error(`Failed to fetch donations`);
  return res.json();
}

export async function createDonation(data) {
  const res = await authenticatedFetch(`/donations`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to create donation (${res.status})`);
  }
  return res.json();
}

export async function updateDonation(id, data) {
  const res = await authenticatedFetch(`/donations/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to update donation (${res.status})`);
  }
  return res.json();
}

export async function deleteDonation(id) {
  const res = await authenticatedFetch(`/donations/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to delete donation (${res.status})`);
  }
}

export async function generateDonationDocument(id, type) {
  const res = await authenticatedFetch(
    `/donations/${id}/document?type=${encodeURIComponent(type)}`
  );
  const document = await res.text();

  if (!res.ok) {
    throw new Error(document || `Failed to generate ${type} document (${res.status})`);
  }

  return document;
}

// --- Donors ---

export async function fetchDonors() {
  const res = await authenticatedFetch(`/donors`);
  if (!res.ok) throw new Error(`Failed to fetch donors`);
  return res.json();
}


// --- Beneficiaries ---

export async function fetchBeneficiaries() {
  const res = await authenticatedFetch(`/beneficiaries`);
  if (!res.ok) throw new Error(`Failed to fetch beneficiaries`);
  return res.json();
}

export async function createBeneficiary(data) {
  const res = await authenticatedFetch(`/beneficiaries`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to create beneficiary (${res.status})`);
  }
  return res.json();
}

export async function updateBeneficiary(id, data) {
  const res = await authenticatedFetch(`/beneficiaries/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to update beneficiary (${res.status})`);
  }
  return res.json();
}

export async function deleteBeneficiary(id) {
  const res = await authenticatedFetch(`/beneficiaries/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to delete beneficiary (${res.status})`);
  }
}

export async function fetchBeneficiaryCategories() {
  const res = await authenticatedFetch(`/beneficiary-categories`);
  if (!res.ok) throw new Error(`Failed to fetch beneficiary categories`);
  return res.json();
}

// --- Resources & Inventory ---

export async function fetchResources() {
  const res = await authenticatedFetch(`/resources`);
  if (!res.ok) throw new Error(`Failed to fetch resources`);
  return res.json();
}

export async function createResource(data) {
  const res = await authenticatedFetch(`/resources`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to create resource (${res.status})`);
  }
  return res.json();
}

export async function updateResource(id, data) {
  const res = await authenticatedFetch(`/resources/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to update resource (${res.status})`);
  }
  return res.json();
}

export async function deleteResource(id) {
  const res = await authenticatedFetch(`/resources/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to delete resource (${res.status})`);
  }
}

export async function distributeResource(data) {
  const res = await authenticatedFetch(`/resources/distribute`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Failed to distribute resource (${res.status})`);
  }
  return res.json();
}

export async function fetchDistributions() {
  const res = await authenticatedFetch(`/resources/distributions`);
  if (!res.ok) throw new Error(`Failed to fetch distributions`);
  return res.json();
}

export async function fetchResourceCategories() {
  const res = await authenticatedFetch(`/resource-categories`);
  if (!res.ok) throw new Error(`Failed to fetch resource categories`);
  return res.json();
}
