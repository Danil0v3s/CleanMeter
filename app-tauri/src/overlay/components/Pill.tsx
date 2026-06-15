import { overlayColors as c } from "../tokens"

/** Pill.kt — title + content row, pill (horizontal) or rounded box (vertical). */
export function Pill({
  title,
  isHorizontal,
  minWidth = 20,
  children,
}: {
  title: string
  isHorizontal: boolean
  minWidth?: number
  children: React.ReactNode
}) {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "row",
        alignItems: "center",
        gap: 12,
        background: "rgba(0, 0, 0, 0.3)",
        padding: isHorizontal ? "4px 12px" : "8px 12px",
        ...(isHorizontal
          ? { height: "100%", minWidth, borderRadius: 9999 }
          : { width: "100%", borderRadius: 8 }),
      }}
    >
      <span
        style={{
          fontSize: 10,
          lineHeight: 1,
          letterSpacing: 1,
          color: c.inverseSubtler,
        }}
      >
        {title}
      </span>
      {children}
    </div>
  )
}
