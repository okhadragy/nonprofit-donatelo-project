export default function ConfirmModal({
  isOpen,
  title = "Delete item?",
  message = "Are you sure you want to delete this item? This action cannot be undone.",
  confirmText = "Delete",
  cancelText = "Cancel",
  onConfirm,
  onCancel,
  loading = false,
}) {
  if (!isOpen) return null;

  return (
    <div
      className="overlay"
      onClick={loading ? undefined : onCancel}
      onKeyDown={(e) => !loading && e.key === "Escape" && onCancel()}
      tabIndex={-1}
    >
      <div
        className="alarm-modal"
        role="alertdialog"
        aria-modal="true"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="alarm-header">
          <div className="alarm-icon" aria-hidden="true">
            <svg
              width="22"
              height="22"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
              <line x1="12" y1="9" x2="12" y2="13" />
              <line x1="12" y1="17" x2="12.01" y2="17" />
            </svg>
          </div>
          <h2 className="alarm-title">{title}</h2>
        </div>

        <p className="alarm-body">{message}</p>

        <div className="alarm-actions">
          <button
            type="button"
            className="btn"
            onClick={onCancel}
            disabled={loading}
          >
            {cancelText}
          </button>
          <button
            type="button"
            className="btn danger-filled"
            onClick={onConfirm}
            disabled={loading}
          >
            {loading ? "Deleting…" : confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
