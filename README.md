# Nonprofit Management System – React + Spring Boot demo

A small UI demo. React shows an admin dashboard with mock data; Spring Boot exposes one
endpoint (`GET /api/status`) so the UI can confirm it is connected. No database, auth, or
business logic yet.

```
nonprofit-demo/
├── backend/    Spring Boot 3 (Java 17, Maven)  → http://localhost:8080
└── frontend/   React 18 + Vite                  → http://localhost:5173
```

## Requirements
- Java 17+
- Maven 3.9+
- Node.js 18+

## Run it
Open two terminals.

**1. Backend**
```bash
cd backend
mvn spring-boot:run
```
Check it: http://localhost:8080/api/status should return `{"status":"UP", ...}`.

**2. Frontend**
```bash
cd frontend
npm install
npm run dev
```
Open http://localhost:5173. The sidebar footer shows **Backend connected** when the API is
reachable, or **Backend offline** (with a Retry link) when it is not. The four pages work either way.

## Where things live
- `frontend/src/data.js` – mock records
- `frontend/src/config.js` – columns and form fields for each page
- `frontend/src/components/ResourcePage.jsx` – the one table/form/CRUD page all four sections reuse
- `frontend/src/api.js` – the only call to the backend
- `backend/.../StatusController.java` – the one endpoint
- `backend/.../CorsConfig.java` – allows `http://localhost:5173`

## Next steps (not included)
Add real controllers per section in the backend, then swap each page's local state in
`App.jsx` for calls in `api.js`. CRUD edits are held in memory and reset on refresh.
