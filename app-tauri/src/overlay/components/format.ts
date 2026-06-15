// Number formatting helpers mirroring the String.format calls in the overlay.

/** "%02d" — integer, zero-padded to width 2. */
export const pad2 = (n: number): string =>
  Math.trunc(n).toString().padStart(2, "0")

/** "%02.1f" / "%02.01f" — one decimal place (width-2 padding rarely applies). */
export const oneDecimal = (n: number): string => n.toFixed(1)
