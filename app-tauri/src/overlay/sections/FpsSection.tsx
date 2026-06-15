import * as React from "react"

import {
  fps,
  frametime,
  type HardwareMonitorData,
} from "@/lib/model/hardwareMonitorData"
import type { OverlaySettings } from "@/lib/model/overlaySettings"
import { overlayColors as c } from "../tokens"
import { AnimatedVisible } from "../components/AnimatedVisible"
import { LineGraph } from "../components/LineGraph"
import { Pill } from "../components/Pill"
import { oneDecimal } from "../components/format"

function FrametimeGraph({
  data,
  isHorizontal,
}: {
  data: HardwareMonitorData
  isHorizontal: boolean
}) {
  const [points, setPoints] = React.useState<number[]>([])
  const largest = React.useRef(0)

  React.useEffect(() => {
    const ft = frametime(data)
    if (ft > largest.current) largest.current = ft
    setPoints((prev) => {
      const next = [
        ...prev,
        largest.current > 0 ? 1 - ft / largest.current : 0,
      ]
      if (next.length > 30) next.shift()
      return next
    })
  }, [data])

  return (
    <LineGraph
      fullWidth={!isHorizontal}
      height={isHorizontal ? 45 : 30}
      series={[{ points, color: c.white }]}
    />
  )
}

export function FpsSection({
  settings,
  data,
}: {
  settings: OverlaySettings
  data: HardwareMonitorData
}) {
  const { framerate, frametime: ftSensor } = settings.sensors
  const visible = framerate.isEnabled || ftSensor.isEnabled

  const framerateText = (
    <span style={{ fontSize: 16, lineHeight: 1, color: c.white }}>
      {fps(data)}
    </span>
  )
  const frametimeText = (
    <span
      style={{ fontSize: 12, lineHeight: 1, color: c.white, paddingBottom: 2 }}
    >
      {oneDecimal(frametime(data))} ms
    </span>
  )

  return (
    <AnimatedVisible visible={visible}>
      {settings.isHorizontal ? (
        <Pill title="FPS" isHorizontal>
          {framerate.isEnabled && framerateText}
          {ftSensor.isEnabled && (
            <>
              <FrametimeGraph data={data} isHorizontal />
              {frametimeText}
            </>
          )}
        </Pill>
      ) : (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            gap: 12,
            width: "100%",
            background: "rgba(0,0,0,0.3)",
            borderRadius: 8,
            padding: "8px 12px",
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
            <span
              style={{
                fontSize: 10,
                lineHeight: 1,
                letterSpacing: 1,
                color: c.inverseSubtler,
              }}
            >
              FPS
            </span>
            <div
              style={{
                display: "flex",
                flex: 1,
                justifyContent: "space-between",
              }}
            >
              {framerate.isEnabled && (
                <span style={{ width: 50, fontSize: 16, color: c.white }}>
                  {fps(data)}
                </span>
              )}
              {ftSensor.isEnabled && (
                <span style={{ width: 50, fontSize: 12, color: c.white }}>
                  {oneDecimal(frametime(data))} ms
                </span>
              )}
            </div>
          </div>
          {ftSensor.isEnabled && (
            <FrametimeGraph data={data} isHorizontal={false} />
          )}
        </div>
      )}
    </AnimatedVisible>
  )
}
