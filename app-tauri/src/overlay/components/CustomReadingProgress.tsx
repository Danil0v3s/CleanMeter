import {
  getReading,
  type HardwareMonitorData,
} from "@/lib/model/hardwareMonitorData"
import { type Boundaries, type ProgressType } from "@/lib/model/overlaySettings"
import type { LabelSlot } from "../tokens"
import { Progress } from "./Progress"

/** CustomReadingProgress.kt — resolves a reading and renders a Progress. */
export function CustomReadingProgress({
  data,
  customReadingId,
  progressType,
  progressUnit,
  label,
  boundaries,
  slot,
}: {
  data: HardwareMonitorData
  customReadingId: string
  progressType: ProgressType
  progressUnit: string
  label: (value: number) => string
  boundaries: Boundaries
  slot: LabelSlot
}) {
  const reading = getReading(data, customReadingId)
  const value = Math.max(1, reading?.Value ?? 1)
  return (
    <Progress
      value={value / 100}
      label={label(value)}
      unit={progressUnit}
      progressType={progressType}
      boundaries={boundaries}
      slot={slot}
    />
  )
}
