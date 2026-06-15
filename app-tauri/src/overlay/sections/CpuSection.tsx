import {
  getReading,
  type HardwareMonitorData,
} from "@/lib/model/hardwareMonitorData"
import {
  defaultBoundaries,
  sensorIsValid,
  type OverlaySettings,
} from "@/lib/model/overlaySettings"
import { AnimatedVisible } from "../components/AnimatedVisible"
import { CustomReadingProgress } from "../components/CustomReadingProgress"
import { Pill } from "../components/Pill"
import { ProgressLabel, ProgressUnit } from "../components/Progress"
import { pad2 } from "../components/format"

export function CpuSection({
  settings,
  data,
}: {
  settings: OverlaySettings
  data: HardwareMonitorData
}) {
  const { cpuTemp, cpuUsage, cpuConsumption } = settings.sensors
  const visible = sensorIsValid(cpuTemp) || sensorIsValid(cpuUsage)

  return (
    <AnimatedVisible visible={visible}>
      <Pill title="CPU" isHorizontal={settings.isHorizontal}>
        {sensorIsValid(cpuTemp) && (
          <CustomReadingProgress
            data={data}
            customReadingId={cpuTemp.customReadingId}
            progressType={settings.progressType}
            progressUnit="°C"
            label={(v) => `${Math.trunc(v)}`}
            boundaries={cpuTemp.boundaries ?? defaultBoundaries()}
          />
        )}
        {sensorIsValid(cpuUsage) && (
          <CustomReadingProgress
            data={data}
            customReadingId={cpuUsage.customReadingId}
            progressType={settings.progressType}
            progressUnit="%"
            label={(v) => pad2(v)}
            boundaries={cpuUsage.boundaries ?? defaultBoundaries()}
          />
        )}
        {sensorIsValid(cpuConsumption) && (
          <div
            style={{
              display: "flex",
              alignItems: "flex-end",
              minWidth: 35,
              paddingBottom: 2,
            }}
          >
            <ProgressLabel>
              {Math.trunc(
                Math.max(
                  1,
                  getReading(data, cpuConsumption.customReadingId)?.Value ?? 1,
                ),
              )}
            </ProgressLabel>
            <ProgressUnit>W</ProgressUnit>
          </div>
        )}
      </Pill>
    </AnimatedVisible>
  )
}
