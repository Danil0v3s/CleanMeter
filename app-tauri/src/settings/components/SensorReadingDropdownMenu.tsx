import * as React from "react"
import { Check, ChevronRight } from "lucide-react"

import { cn } from "@/lib/utils"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { ScrollArea } from "@/components/ui/scroll-area"
import type { HwSensor } from "@/lib/model/hardwareMonitorData"
import { HwSensorType } from "@/lib/model/hardwareMonitorData"

const invalidSensor: HwSensor = {
  Name: "Choose a sensor",
  Identifier: "$invalid",
  HardwareIdentifier: "",
  SensorType: HwSensorType.Unknown,
  Value: 0,
}

/** SensorReadingDropdownMenu.kt — trigger + searchable picker dialog. */
export function SensorReadingDropdownMenu({
  options,
  selectedIndex,
  onValueChanged,
  label,
  sensorName,
  dropdownLabel = (s) => `${s.Name} (${s.Value} - ${HwSensorType[s.SensorType]})`,
}: {
  options: HwSensor[]
  selectedIndex: number
  onValueChanged: (sensor: HwSensor) => void
  label?: string
  sensorName: string
  dropdownLabel?: (sensor: HwSensor) => string
}) {
  const [open, setOpen] = React.useState(false)
  const [filter, setFilter] = React.useState("")

  const selected = selectedIndex >= 0 ? options[selectedIndex] : invalidSensor
  const filtered = filter
    ? options.filter(
        (o) =>
          HwSensorType[o.SensorType]
            .toLowerCase()
            .includes(filter.toLowerCase()) ||
          o.Name.toLowerCase().includes(filter.toLowerCase()),
      )
    : options

  return (
    <div className="ml-3 mt-4 border-l pl-5">
      <button
        type="button"
        onClick={() => setOpen(true)}
        className="flex w-full items-center justify-between rounded-lg border bg-card px-3 py-3 text-left"
      >
        <span className="flex items-center gap-2">
          {label && <span className="text-muted-foreground">{label}</span>}
          <span className="text-sm font-medium text-foreground">
            {selected.Identifier !== "$invalid"
              ? `${selected.Name} - ${HwSensorType[selected.SensorType]}`
              : selected.Name}
          </span>
        </span>
        <ChevronRight className="size-4 text-muted-foreground" />
      </button>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-[650px] gap-4">
          <DialogHeader>
            <DialogTitle>Select {sensorName} sensor</DialogTitle>
          </DialogHeader>
          <Input
            autoFocus
            placeholder="Search…"
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
          />
          <ScrollArea className="h-[400px] pr-3">
            <div className="flex flex-col">
              {filtered.map((item) => {
                const isSelected = item.Identifier === selected.Identifier
                return (
                  <button
                    key={item.Identifier}
                    type="button"
                    onClick={() => {
                      onValueChanged(item)
                      setOpen(false)
                    }}
                    className={cn(
                      "flex h-10 items-center justify-between rounded-lg px-3 text-left text-sm",
                      isSelected
                        ? "bg-muted font-medium text-foreground"
                        : "text-foreground hover:bg-muted/50",
                    )}
                  >
                    {dropdownLabel(item)}
                    {isSelected && <Check className="size-4" />}
                  </button>
                )
              })}
            </div>
          </ScrollArea>
        </DialogContent>
      </Dialog>
    </div>
  )
}
