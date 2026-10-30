
import { useState } from "react";
import { login, register } from "../api.js";

export default function AuthPage({ onAuthenticated }) {
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState({
    name: "",
    email: "",
    username: "",
    password: "",
    role: "DONOR",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const update = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const result =
        mode === "login"
          ? await login({
              username: form.username,
              password: form.password,
            })
          : await register(form);

      // Adjust "token" if your AuthResponse uses a different field name.
      if (result?.token) {
        localStorage.setItem("token", result.token);
        onAuthenticated(result.token);
      } else if (mode === "register") {
        // Registration succeeded, but the backend didn't return a JWT.
        setMode("login");
        setError("Account created. Please log in.");
      } else {
        setError("Login succeeded, but no token was returned by the server.");
      }
    } catch (err) {
      setError(err.message || "Something went wrong. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <div className="auth-brand">Nonprofit Portal</div>
        <h1>{mode === "login" ? "Welcome back" : "Create your account"}</h1>
        <p className="auth-subtitle">
          {mode === "login"
            ? "Sign in to manage your nonprofit resources."
            : "Register to get started."}
        </p>

        {mode === "register" && (
          <>
            <label htmlFor="name">Full name</label>
            <input
              id="name"
              name="name"
              value={form.name}
              onChange={update}
              placeholder="Your full name"
              required
              autoComplete="name"
            />

            <label htmlFor="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              value={form.email}
              onChange={update}
              placeholder="you@example.com"
              required
              autoComplete="email"
            />

            <label htmlFor="role">I want to join as</label>
            <select
              id="role"
              name="role"
              value={form.role}
              onChange={update}
            >
              <option value="DONOR">Donor</option>
              <option value="VOLUNTEER">Volunteer</option>
            </select>
          </>
        )}

        <label htmlFor="username">Username</label>
        <input
          id="username"
          name="username"
          value={form.username}
          onChange={update}
          placeholder="Your username"
          required
          autoComplete="username"
        />

        <label htmlFor="password">Password</label>
        <input
          id="password"
          name="password"
          type="password"
          value={form.password}
          onChange={update}
          placeholder="Enter your password"
          required
          minLength={8}
          autoComplete={
            mode === "login" ? "current-password" : "new-password"
          }
        />

        {error && (
          <p
            className={`auth-message ${
              error.startsWith("Account created") ? "success" : ""
            }`}
            role="alert"
          >
            {error}
          </p>
        )}

        <button className="auth-submit" type="submit" disabled={loading}>
          {loading
            ? "Please wait..."
            : mode === "login"
              ? "Sign in"
              : "Create account"}
        </button>

        <p className="auth-switch">
          {mode === "login"
            ? "Don't have an account?"
            : "Already have an account?"}{" "}
          <button
            type="button"
            onClick={() => {
              setMode(mode === "login" ? "register" : "login");
              setError("");
            }}
          >
            {mode === "login" ? "Register" : "Sign in"}
          </button>
        </p>
      </form>
    </div>
  );
}
