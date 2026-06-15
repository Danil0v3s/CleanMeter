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
import { Progress, ValueUnit } from "../components/Progress"
import { oneDecimal, pad2 } from "../components/format"
import { LABEL } from "../tokens"

export function GpuSection({
  settings,
  data,
}: {
  settings: OverlaySettings
  data: HardwareMonitorData
}) {
  const { gpuTemp, gpuUsage, vramUsage, totalVramUsed, gpuConsumption } =
    settings.sensors
  const visible =
    sensorIsValid(gpuTemp) ||
    sensorIsValid(gpuUsage) ||
    sensorIsValid(vramUsage)

  const vramReading = data.Sensors.find(
    (s) =>
      s.Identifier === vramUsage.customReadingId &&
      s.Name.toLowerCase().includes("memory"),
  )

  return (
    <AnimatedVisible visible={visible}>
      <Pill title="GPU" isHorizontal={settings.isHorizontal}>
        {sensorIsValid(gpuTemp) && (
          <CustomReadingProgress
            data={data}
            customReadingId={gpuTemp.customReadingId}
            progressType={settings.progressType}
            progressUnit="°C"
            label={(v) => `${Math.trunc(v)}`}
            boundaries={gpuTemp.boundaries ?? defaultBoundaries()}
            slot={LABEL.temp}
          />
        )}
        {sensorIsValid(gpuUsage) && (
          <CustomReadingProgress
            data={data}
            customReadingId={gpuUsage.customReadingId}
            progressType={settings.progressType}
            progressUnit="%"
            label={(v) => pad2(v)}
            boundaries={gpuUsage.boundaries ?? defaultBoundaries()}
            slot={LABEL.usage}
          />
        )}
        {sensorIsValid(vramUsage) && sensorIsValid(totalVramUsed) && (
          <Progress
            value={Math.max(1, vramReading?.Value ?? 1) / 100}
            label={oneDecimal(
              Math.max(
                1,
                getReading(data, totalVramUsed.customReadingId)?.Value ?? 1,
              ) / 1000,
            )}
            unit="GB"
            progressType={settings.progressType}
            boundaries={vramUsage.boundaries ?? defaultBoundaries()}
            slot={LABEL.vram}
          />
        )}
        {sensorIsValid(gpuConsumption) && (
          <ValueUnit
            label={`${Math.trunc(
              Math.max(
                1,
                getReading(data, gpuConsumption.customReadingId)?.Value ?? 1,
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
