/** A network up/down arrow, tinted via CSS mask, fading with the rate. */
export function RateArrow({
  color,
  up,
  rate,
}: {
  color: string
  up: boolean
  rate: number
}) {
  return (
    <div style={{ display: "flex", alignItems: "flex-end" }}>
      <div style={{ paddingRight: 4, paddingBottom: 3 }}>
        <div
          style={{
            width: 24,
            height: 24,
            background: color,
            opacity: Math.min(1, rate),
            transform: up ? "rotate(180deg)" : undefined,
            WebkitMaskImage: "url(/icons/arrow_down.svg)",
            maskImage: "url(/icons/arrow_down.svg)",
            WebkitMaskRepeat: "no-repeat",
            maskRepeat: "no-repeat",
            WebkitMaskPosition: "center",
            maskPosition: "center",
            WebkitMaskSize: "contain",
            maskSize: "contain",
          }}
        />
      </div>
    </div>
  )
}
