import * as React from "react"

/** Mirrors AnimatedVisibility(scaleIn+fadeIn / scaleOut+fadeOut). */
export function AnimatedVisible({
  visible,
  children,
}: {
  visible: boolean
  children: React.ReactNode
}) {
  const [mounted, setMounted] = React.useState(visible)
  const [shown, setShown] = React.useState(visible)

  React.useEffect(() => {
    if (visible) {
      setMounted(true)
      const id = requestAnimationFrame(() => setShown(true))
      return () => cancelAnimationFrame(id)
    }
    setShown(false)
    const t = window.setTimeout(() => setMounted(false), 150)
    return () => window.clearTimeout(t)
  }, [visible])

  if (!mounted) return null
  return (
    <div
      style={{
        display: "flex",
        flexShrink: 0,
        transition: "opacity 150ms ease, transform 150ms ease",
        opacity: shown ? 1 : 0,
        transform: shown ? "scale(1)" : "scale(0.8)",
      }}
    >
      {children}
    </div>
  )
}
