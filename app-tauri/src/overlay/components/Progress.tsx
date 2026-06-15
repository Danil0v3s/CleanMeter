import { ProgressType, type Boundaries } from "@/lib/model/overlaySettings"
import { overlayColors as c } from "../tokens"

export function ProgressLabel({ children }: { children: React.ReactNode }) {
  return (
    <span style={{ fontSize: 16, lineHeight: 1, color: c.white }}>
      {children}
    </span>
  )
}

export function ProgressUnit({ children }: { children: React.ReactNode }) {
  return (
    <span
      style={{
        fontSize: 10,
        lineHeight: 1,
        color: c.white,
        paddingBottom: 1,
      }}
    >
      {children}
    </span>
  )
}

function boundaryColor(value: number, b: Boundaries): string {
  if (value <= b.low / 100) return c.green
  if (value <= b.medium / 100) return c.yellow
  if (value > b.medium / 100) return c.red
  return c.white
}

function CircularProgress({ value, color }: { value: number; color: string }) {
  const size = 24
  const stroke = 3
  const r = (size - stroke) / 2
  const circ = 2 * Math.PI * r
  const v = Math.min(1, Math.max(0, value))
  return (
    <svg width={size} height={size} style={{ display: "block" }}>
      <circle
        cx={size / 2}
        cy={size / 2}
        r={r}
        fill="none"
        stroke={c.clearGray}
        strokeWidth={stroke}
      />
      <circle
        cx={size / 2}
        cy={size / 2}
        r={r}
        fill="none"
        stroke={color}
        strokeWidth={stroke}
        strokeLinecap="round"
        strokeDasharray={circ}
        strokeDashoffset={circ * (1 - v)}
        transform={`rotate(-90 ${size / 2} ${size / 2})`}
      />
    </svg>
  )
}

function BarProgress({ value }: { value: number }) {
  const integerValue = Math.trunc(value * 10)
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 2 }}>
      {Array.from({ length: 10 }, (_, it) => {
        const inverseValue = Math.abs(it - 9)
        let color = "transparent"
        if (integerValue >= inverseValue) {
          if (inverseValue >= 8) color = c.red
          else if (inverseValue >= 5) color = c.yellow
          else color = c.green
        }
        return (
          <div
            key={it}
            style={{
              width: 24,
              height: 1,
              borderRadius: 9999,
              background: color,
            }}
          />
        )
      })}
    </div>
  )
}

export function Progress({
  value,
  label,
  unit,
  progressType,
  boundaries,
}: {
  value: number
  label: string
  unit: string
  progressType: ProgressType
  boundaries: Boundaries
}) {
  const color = boundaryColor(value, boundaries)
  return (
    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
      {progressType === ProgressType.Circular && (
        <CircularProgress value={value} color={color} />
      )}
      {progressType === ProgressType.Bar && <BarProgress value={value} />}
      <div
        style={{
          display: "flex",
          alignItems: "flex-end",
          minWidth: 35,
          paddingBottom: 2,
        }}
      >
        <ProgressLabel>{label}</ProgressLabel>
        <ProgressUnit>{unit}</ProgressUnit>
      </div>
    </div>
  )
}
