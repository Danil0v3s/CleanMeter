// Mirror of CheckboxSectionOption.kt + SettingsViewModel.kt (events/state)

import type { HardwareMonitorData, HwSensorType } from "./hardwareMonitorData"
import type { Boundaries, OverlaySettings, ProgressType } from "./overlaySettings"

export enum SensorType {
  Framerate = "Framerate",
  Frametime = "Frametime",
  CpuTemp = "CpuTemp",
  CpuUsage = "CpuUsage",
  CpuConsumption = "CpuConsumption",
  GpuTemp = "GpuTemp",
  GpuUsage = "GpuUsage",
  VramUsage = "VramUsage",
  TotalVramUsed = "TotalVramUsed",
  GpuConsumption = "GpuConsumption",
  RamUsage = "RamUsage",
  UpRate = "UpRate",
  DownRate = "DownRate",
  NetGraph = "NetGraph",
}

export enum SectionType {
  Fps = "Fps",
  Gpu = "Gpu",
  Cpu = "Cpu",
  Ram = "Ram",
  Network = "Network",
}

export interface CheckboxSectionOption {
  isSelected: boolean
  name: string
  type: SensorType
  dataType: HwSensorType
  optionReadingId: string
  useCustomSensor: boolean
  useCheckbox: boolean
}

export interface IntOffset {
  x: number
  y: number
}

// Mirror of the SettingsEvent sealed class.
export type SettingsEvent =
  | { type: "OptionsToggle"; data: CheckboxSectionOption }
  | { type: "SwitchToggle"; section: SectionType; isEnabled: boolean }
  | { type: "CustomSensorSelect"; sensor: SensorType; sensorId: string }
  | { type: "DisplaySelect"; displayIndex: number }
  | { type: "OverlayPositionIndexSelect"; index: number }
  | {
      type: "OverlayCustomPositionSelect"
      offset: IntOffset
      isPositionLocked: boolean
    }
  | { type: "OverlayCustomPositionEnable"; isEnabled: boolean }
  | { type: "OverlayOrientationSelect"; isHorizontal: boolean }
  | { type: "OverlayOpacityChange"; opacity: number }
  | { type: "OverlayScaleChange"; scale: number }
  | { type: "OverlayGraphChange"; progressType: ProgressType }
  | { type: "DarkThemeToggle"; isEnabled: boolean }
  | { type: "FpsApplicationSelect"; applicationName: string }
  | { type: "BoundarySet"; sensorType: SensorType; boundaries: Boundaries }
  | { type: "PollingRateSelect"; pollingRate: number }
  | { type: "ConsentGiven" }
  | { type: "ToggleLoggingEnabled" }

export interface SettingsState {
  overlaySettings: OverlaySettings | null
  hardwareData: HardwareMonitorData | null
  isRecording: boolean
  adminConsent: boolean
  logSink: string
}

export const initialSettingsState = (): SettingsState => ({
  overlaySettings: null,
  hardwareData: null,
  isRecording: false,
  adminConsent: false,
  logSink: "",
})
