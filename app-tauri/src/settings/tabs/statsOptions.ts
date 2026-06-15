import { HwSensorType } from "@/lib/model/hardwareMonitorData"
import type { OverlaySettings } from "@/lib/model/overlaySettings"
import { type CheckboxSectionOption, SensorType } from "@/lib/model/settings"

const opt = (
  o: Omit<CheckboxSectionOption, "optionReadingId" | "useCustomSensor" | "useCheckbox"> &
    Partial<
      Pick<
        CheckboxSectionOption,
        "optionReadingId" | "useCustomSensor" | "useCheckbox"
      >
    >,
): CheckboxSectionOption => ({
  optionReadingId: "",
  useCustomSensor: false,
  useCheckbox: true,
  ...o,
})

/** Mirror of checkboxSectionOptions(overlaySettings) in StatsUi.kt. */
export function checkboxSectionOptions(
  s: OverlaySettings,
): CheckboxSectionOption[] {
  return [
    opt({
      isSelected: s.sensors.framerate.isEnabled,
      name: "Frame count",
      type: SensorType.Framerate,
      dataType: HwSensorType.SmallData,
    }),
    opt({
      isSelected: s.sensors.frametime.isEnabled,
      name: "Frame time graph",
      type: SensorType.Frametime,
      dataType: HwSensorType.Unknown,
    }),
    opt({
      isSelected: s.sensors.cpuTemp.isEnabled,
      name: "CPU temperature",
      type: SensorType.CpuTemp,
      optionReadingId: s.sensors.cpuTemp.customReadingId,
      useCustomSensor: true,
      dataType: HwSensorType.Temperature,
    }),
    opt({
      isSelected: s.sensors.cpuUsage.isEnabled,
      name: "CPU usage",
      type: SensorType.CpuUsage,
      optionReadingId: s.sensors.cpuUsage.customReadingId,
      useCustomSensor: true,
      dataType: HwSensorType.Load,
    }),
    opt({
      isSelected: s.sensors.cpuConsumption.isEnabled,
      name: "CPU consumption",
      type: SensorType.CpuConsumption,
      optionReadingId: s.sensors.cpuConsumption.customReadingId,
      useCustomSensor: true,
      dataType: HwSensorType.Power,
    }),
    opt({
      isSelected: s.sensors.gpuTemp.isEnabled,
      name: "GPU temperature",
      type: SensorType.GpuTemp,
      optionReadingId: s.sensors.gpuTemp.customReadingId,
      useCustomSensor: true,
      dataType: HwSensorType.Temperature,
    }),
    opt({
      isSelected: s.sensors.gpuUsage.isEnabled,
      name: "GPU usage",
      type: SensorType.GpuUsage,
      optionReadingId: s.sensors.gpuUsage.customReadingId,
      useCustomSensor: true,
      dataType: HwSensorType.Load,
    }),
    opt({
      isSelected: s.sensors.vramUsage.isEnabled,
      name: "VRAM usage",
      type: SensorType.VramUsage,
      optionReadingId: s.sensors.vramUsage.customReadingId,
      useCustomSensor: true,
      dataType: HwSensorType.Load,
    }),
    opt({
      isSelected: s.sensors.vramUsage.isEnabled,
      name: "Total VRAM used",
      type: SensorType.TotalVramUsed,
      optionReadingId: s.sensors.totalVramUsed.customReadingId,
      useCustomSensor: true,
      useCheckbox: false,
      dataType: HwSensorType.SmallData,
    }),
    opt({
      isSelected: s.sensors.gpuConsumption.isEnabled,
      name: "GPU consumption",
      type: SensorType.GpuConsumption,
      optionReadingId: s.sensors.gpuConsumption.customReadingId,
      useCustomSensor: true,
      dataType: HwSensorType.Power,
    }),
    opt({
      isSelected: s.sensors.ramUsage.isEnabled,
      name: "RAM usage",
      type: SensorType.RamUsage,
      dataType: HwSensorType.Load,
    }),
    opt({
      isSelected: s.sensors.downRate.isEnabled,
      name: "Receive speed",
      type: SensorType.DownRate,
      optionReadingId: s.sensors.downRate.customReadingId,
      useCustomSensor: true,
      dataType: HwSensorType.Throughput,
    }),
    opt({
      isSelected: s.sensors.upRate.isEnabled,
      name: "Send speed",
      type: SensorType.UpRate,
      optionReadingId: s.sensors.upRate.customReadingId,
      useCustomSensor: true,
      dataType: HwSensorType.Throughput,
    }),
    opt({
      isSelected: s.netGraph,
      name: "Network graph",
      type: SensorType.NetGraph,
      dataType: HwSensorType.Unknown,
    }),
  ]
}

export function filterOptions(
  options: CheckboxSectionOption[],
  ...types: SensorType[]
): CheckboxSectionOption[] {
  return options.filter((o) => types.includes(o.type))
}
