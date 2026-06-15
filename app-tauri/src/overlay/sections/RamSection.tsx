import {
  ramUsage,
  ramUsagePercent,
  type HardwareMonitorData,
} from "@/lib/model/hardwareMonitorData"
import {
  defaultBoundaries,
  type OverlaySettings,
} from "@/lib/model/overlaySettings"
import { AnimatedVisible } from "../components/AnimatedVisible"
import { Pill } from "../components/Pill"
import { Progress } from "../components/Progress"
import { oneDecimal } from "../components/format"

export function RamSection({
  settings,
  data,
}: {
  settings: OverlaySettings
  data: HardwareMonitorData
}) {
  const { ramUsage: ram } = settings.sensors
  return (
    <AnimatedVisible visible={ram.isEnabled}>
      <Pill title="RAM" isHorizontal={settings.isHorizontal}>
        <Progress
          value={ramUsagePercent(data)}
          label={oneDecimal(ramUsage(data))}
          unit="GB"
          progressType={settings.progressType}
          boundaries={ram.boundaries ?? defaultBoundaries()}
        />
      </Pill>
    </AnimatedVisible>
  )
}
