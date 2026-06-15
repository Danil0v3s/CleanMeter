// Overlay window root. Pixel-perfect overlay is built in Commit 3.
export function OverlayApp() {
  return (
    <div
      style={{
        display: "flex",
        height: "100vh",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <div
        style={{
          padding: "8px 16px",
          borderRadius: 999,
          background: "rgba(0,0,0,0.36)",
          color: "white",
          fontFamily: "Inter, sans-serif",
          fontSize: 14,
        }}
      >
        Overlay
      </div>
    </div>
  )
}
