export default function Announcement({ message, onDismiss }) {
  if (!message) return null;

  return (
    <aside className="announcement" role="status" aria-live="polite">
      <div>
        <strong>New donation</strong>
        <p>{message}</p>
      </div>
      <button type="button" className="announcement-dismiss" onClick={onDismiss} aria-label="Dismiss announcement">
        ×
      </button>
    </aside>
  );
}
