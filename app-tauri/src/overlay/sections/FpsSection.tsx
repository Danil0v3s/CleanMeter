import * as React from "react"

import {
  fps,
  frametime,
  type HardwareMonitorData,
} from "@/lib/model/hardwareMonitorData"
import type { OverlaySettings } from "@/lib/model/overlaySettings"
import { LABEL, overlayColors as c } from "../tokens"
import { AnimatedVisible } from "../components/AnimatedVisible"
import { AutoFitText } from "../components/AutoFitText"
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
    <AutoFitText
      text={`${fps(data)}`}
      width={LABEL.fps.width}
      maxFontSize={LABEL.fps.maxFontSize}
      minFontSize={LABEL.fps.minFontSize}
    />
  )
  const frametimeText = (
    <AutoFitText
      text={`${oneDecimal(frametime(data))} ms`}
      width={LABEL.frametime.width}
      maxFontSize={LABEL.frametime.maxFontSize}
      minFontSize={LABEL.frametime.minFontSize}
      style={{ paddingBottom: 2 }}
    />
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
              {framerate.isEnabled && framerateText}
              {ftSensor.isEnabled && frametimeText}
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
