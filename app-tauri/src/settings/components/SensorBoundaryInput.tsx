import { cn } from "@/lib/utils"
import type { Boundaries, Sensor } from "@/lib/model/overlaySettings"
import { isGraphSensor } from "@/lib/model/overlaySettings"
import { SensorType } from "@/lib/model/settings"
import { Disclaimer } from "./primitives"

function unitFor(sensorType: SensorType): string {
  switch (sensorType) {
    case SensorType.CpuTemp:
    case SensorType.GpuTemp:
      return "°"
    case SensorType.CpuUsage:
    case SensorType.GpuUsage:
    case SensorType.VramUsage:
    case SensorType.RamUsage:
      return "%"
    default:
      return ""
  }
}

function BoundaryInput({
  label,
  dotClassName,
  unit,
  minValue,
  maxValue,
  onMaxValueChange,
}: {
  label: string
  dotClassName: string
  unit: string
  minValue: number
  maxValue: number
  onMaxValueChange: (value: number) => void
}) {
  return (
    <div className="flex flex-1 flex-col gap-2">
      <div className="flex items-center gap-1.5">
        <span className={cn("size-1.5 rounded-full", dotClassName)} />
        <span className="text-sm font-medium text-foreground">{label}</span>
      </div>
      <div className="flex h-10 items-center divide-x rounded-lg border">
        <span className="flex-1 px-3 text-sm text-muted-foreground">
          {minValue}
          {unit}
        </span>
        <input
          value={`${maxValue}${unit}`}
          onChange={(e) => {
            const raw = e.target.value.replace(unit, "")
            const intValue = Number.parseInt(raw, 10)
            if (!Number.isNaN(intValue) && intValue <= 100) {
              onMaxValueChange(intValue)
            }
          }}
          className="w-full flex-1 bg-transparent px-3 text-sm text-foreground outline-none"
        />
      </div>
    </div>
  )
}

/** SensorBoundaryInput.kt — low/medium/high boundary editors for graph sensors. */
export function SensorBoundaryInput({
  sensor,
  sensorType,
  onBoundaryChange,
}: {
  sensor: Sensor
  sensorType: SensorType
  onBoundaryChange: (sensorType: SensorType, boundaries: Boundaries) => void
}) {
  if (!isGraphSensor(sensor) || !sensor.boundaries) return null
  const boundaries = sensor.boundaries
  const unit = unitFor(sensorType)

  return (
    <div className="ml-3 mt-3 border-l pl-5">
      <div className="flex flex-col gap-4 rounded-lg bg-muted/50 p-4">
        <div className="flex gap-4">
          <BoundaryInput
            label="Low"
            dotClassName="bg-green-500"
            unit={unit}
            minValue={0}
            maxValue={boundaries.low}
            onMaxValueChange={(v) =>
              onBoundaryChange(sensorType, { ...boundaries, low: v })
            }
          />
          <BoundaryInput
            label="Medium"
            dotClassName="bg-yellow-400"
            unit={unit}
            minValue={boundaries.low}
            maxValue={boundaries.medium}
            onMaxValueChange={(v) =>
              onBoundaryChange(sensorType, { ...boundaries, medium: v })
            }
          />
          <BoundaryInput
            label="High"
            dotClassName="bg-red-500"
            unit={unit}
            minValue={boundaries.medium}
            maxValue={boundaries.high}
            onMaxValueChange={(v) =>
              onBoundaryChange(sensorType, { ...boundaries, high: v })
            }
          />
        </div>
        <Disclaimer>
          Colors are visible only when the graph is enabled in the style
          settings
        </Disclaimer>
      </div>
    </div>
  )
}
