import * as React from "react"

import {
  getReading,
  type HardwareMonitorData,
} from "@/lib/model/hardwareMonitorData"
import { sensorIsValid, type OverlaySettings } from "@/lib/model/overlaySettings"
import { overlayColors as c } from "../tokens"
import { AnimatedVisible } from "../components/AnimatedVisible"
import { LineGraph } from "../components/LineGraph"
import { Pill } from "../components/Pill"
import { RateArrow } from "../components/RateArrow"

const clamp01 = (n: number) => Math.min(1, Math.max(0, n))

function NetGraph({
  settings,
  data,
  isHorizontal,
}: {
  settings: OverlaySettings
  data: HardwareMonitorData
  isHorizontal: boolean
}) {
  const { upRate, downRate } = settings.sensors
  const [up, setUp] = React.useState<number[]>([])
  const [down, setDown] = React.useState<number[]>([])
  const largestUp = React.useRef(0)
  const largestDown = React.useRef(0)

  React.useEffect(() => {
    const dl = getReading(data, downRate.customReadingId)?.Value ?? 0
    const ul = getReading(data, upRate.customReadingId)?.Value ?? 0

    setUp((prev) => {
      const next = [...prev, clamp01(ul / Math.max(largestUp.current, 1))]
      if (next.length > 30) next.shift()
      largestUp.current = Math.max(...next)
      return next
    })
    setDown((prev) => {
      const next = [...prev, clamp01(dl / largestDown.current + 0.2)]
      if (next.length > 30) next.shift()
      largestDown.current = Math.max(...next) + 0.2
      return next
    })
  }, [data, upRate.customReadingId, downRate.customReadingId])

  const series = [
    ...(upRate.isEnabled ? [{ points: up, color: c.purple }] : []),
    ...(downRate.isEnabled ? [{ points: down, color: c.cyan }] : []),
  ]

  return (
    <LineGraph
      fullWidth={!isHorizontal}
      height={isHorizontal ? 45 : 30}
      series={series}
    />
  )
}

export function NetSection({
  settings,
  data,
}: {
  settings: OverlaySettings
  data: HardwareMonitorData
}) {
  const { upRate, downRate } = settings.sensors
  const visible = sensorIsValid(upRate) || sensorIsValid(downRate)
  const dlRate = getReading(data, downRate.customReadingId)?.Value ?? 0
  const ulRate = getReading(data, upRate.customReadingId)?.Value ?? 0

  const arrows = (
    <>
      {sensorIsValid(downRate) && (
        <RateArrow color={c.cyan} up={false} rate={dlRate} />
      )}
      {sensorIsValid(upRate) && (
        <RateArrow color={c.purple} up rate={ulRate} />
      )}
    </>
  )

  return (
    <AnimatedVisible visible={visible}>
      {settings.isHorizontal ? (
        <Pill title="NET" isHorizontal>
          {arrows}
          {settings.netGraph && (
            <NetGraph settings={settings} data={data} isHorizontal />
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
                color: c.offWhite,
              }}
            >
              NET
            </span>
            <div style={{ display: "flex", flex: 1, gap: 12 }}>{arrows}</div>
          </div>
          {settings.netGraph && (
            <NetGraph settings={settings} data={data} isHorizontal={false} />
          )}
        </div>
      )}
    </AnimatedVisible>
  )
}
