export default function Announcement({ notifications, onDismiss }) {
  if (notifications.length === 0) return null;

  return (
    <aside className="announcement" role="status" aria-live="polite">
      <div>
        {notifications.map((notification) => (
          <div className="announcement-item" key={notification.id}>
            <strong>{notification.channel}</strong>
            <p>{notification.message}</p>
          </div>
        ))}
      </div>
      <button type="button" className="announcement-dismiss" onClick={onDismiss} aria-label="Dismiss notifications">
        ×
      </button>
    </aside>
  );
}
