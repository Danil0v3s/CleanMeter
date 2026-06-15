import * as React from "react"

/**
 * Renders text in a fixed-width slot. The font size is the largest value in
 * [minFontSize, maxFontSize] that fits the text within `width` — it shrinks for
 * long values (e.g. "1200") and grows back up to the ceiling for short ones
 * (e.g. "60"), so the slot never changes width and the layout never jiggles.
 */
export function AutoFitText({
  text,
  width,
  maxFontSize,
  minFontSize,
  color = "#fff",
  align = "left",
  style,
}: {
  text: string
  width: number
  maxFontSize: number
  minFontSize: number
  color?: string
  align?: "left" | "center" | "right"
  style?: React.CSSProperties
}) {
  const measureRef = React.useRef<HTMLSpanElement>(null)
  const [fontSize, setFontSize] = React.useState(maxFontSize)

  React.useLayoutEffect(() => {
    const el = measureRef.current
    if (!el) return
    // Measure the natural text width at the ceiling size.
    el.style.fontSize = `${maxFontSize}px`
    const natural = el.scrollWidth
    let next = maxFontSize
    if (natural > width && natural > 0) {
      next = Math.max(
        minFontSize,
        Math.floor((maxFontSize * width) / natural),
      )
    }
    setFontSize((prev) => (prev === next ? prev : next))
  }, [text, width, maxFontSize, minFontSize])

  return (
    <span
      style={{
        display: "inline-flex",
        width,
        overflow: "hidden",
        justifyContent:
          align === "right"
            ? "flex-end"
            : align === "center"
              ? "center"
              : "flex-start",
        ...style,
      }}
    >
      <span
        ref={measureRef}
        style={{
          fontSize,
          lineHeight: 1,
          color,
          whiteSpace: "nowrap",
        }}
      >
        {text}
      </span>
    </span>
  )
}
