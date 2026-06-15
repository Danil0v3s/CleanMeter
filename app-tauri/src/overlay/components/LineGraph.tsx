import * as React from "react"

export interface GraphSeries {
  points: number[]
  color: string
}

const LIST_SIZE = 30

/**
 * Canvas line graph matching drawLine.kt: points are y-values in [0,1]
 * spread across `LIST_SIZE` slots; edges fade out via a horizontal gradient
 * mask (destination-in), mirroring the Compose DstIn gradient.
 */
export function LineGraph({
  fullWidth,
  height,
  series,
}: {
  fullWidth: boolean
  height: number
  series: GraphSeries[]
}) {
  const canvasRef = React.useRef<HTMLCanvasElement>(null)

  React.useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext("2d")
    if (!ctx) return

    const dpr = window.devicePixelRatio || 1
    const cssWidth = fullWidth ? canvas.clientWidth : 100
    const cssHeight = height

    canvas.width = Math.max(1, Math.round(cssWidth * dpr))
    canvas.height = Math.round(cssHeight * dpr)
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
    ctx.clearRect(0, 0, cssWidth, cssHeight)

    for (const s of series) {
      if (s.points.length < 2) continue
      ctx.strokeStyle = s.color
      ctx.lineWidth = 1
      ctx.beginPath()
      s.points.forEach((p, i) => {
        const x = cssWidth * (i / LIST_SIZE)
        const y = cssHeight * (1 - p)
        if (i === 0) ctx.moveTo(x, y)
        else ctx.lineTo(x, y)
      })
      ctx.stroke()
    }

    // Fade the left/right edges (Transparent, Black, Black, Black, Transparent).
    ctx.globalCompositeOperation = "destination-in"
    const grad = ctx.createLinearGradient(0, 0, cssWidth, 0)
    grad.addColorStop(0, "rgba(0,0,0,0)")
    grad.addColorStop(0.25, "rgba(0,0,0,1)")
    grad.addColorStop(0.5, "rgba(0,0,0,1)")
    grad.addColorStop(0.75, "rgba(0,0,0,1)")
    grad.addColorStop(1, "rgba(0,0,0,0)")
    ctx.fillStyle = grad
    ctx.fillRect(0, 0, cssWidth, cssHeight)
    ctx.globalCompositeOperation = "source-over"
  }, [series, fullWidth, height])

  return (
    <canvas
      ref={canvasRef}
      style={{
        width: fullWidth ? "100%" : 100,
        height,
        display: "block",
      }}
    />
  )
}
