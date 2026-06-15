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
import { ValueUnit } from "../components/Progress"
import { pad2 } from "../components/format"
import { LABEL } from "../tokens"

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
            slot={LABEL.temp}
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
            slot={LABEL.usage}
          />
        )}
        {sensorIsValid(cpuConsumption) && (
          <ValueUnit
            label={`${Math.trunc(
              Math.max(
                1,
                getReading(data, cpuConsumption.customReadingId)?.Value ?? 1,
              ),
            )}`}
            unit="W"
            slot={LABEL.watts}
          />
        )}
      </Pill>
    </AnimatedVisible>
  )
}
