// Exact overlay colors from ui/ColorTokens.kt — used for pixel-perfect rendering.
export const overlayColors = {
  green: "#1CAD69",
  yellow: "#FCC748",
  red: "#ED4335",
  purple: "#A48AFB",
  cyan: "#2ED3B7",
  // #11d3d3d3 → ARGB alpha 0x11 (=17/255 ≈ 0.067) over #d3d3d3
  clearGray: "rgba(211, 211, 211, 0.067)",
  offWhite: "#C0C0C0",
  // text.inverseSubtler used for the pill title (#F5F5F6 dark / #f5f5f6)
  inverseSubtler: "#F5F5F6",
  white: "#FFFFFF",
} as const

/** scale.map(0,1, .5,1) — maps the user [0,1] scale to a [0.5,1] CSS scale. */
export function mapScale(scale: number): number {
  return 0.5 + scale * 0.5
}

export const OVERLAY_FONT = "Inter, ui-sans-serif, system-ui, sans-serif"
