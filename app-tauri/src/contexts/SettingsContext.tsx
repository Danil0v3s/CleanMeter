import * as React from "react"

import {
  cpuReadings,
  gpuReadings,
  networkReadings,
  type HardwareMonitorData,
  type HwHardware,
  type HwSensor,
} from "@/lib/model/hardwareMonitorData"
import { mockHardwareData } from "@/lib/model/mockData"
import {
  defaultOverlaySettings,
  type OverlaySettings,
  type Sensor,
} from "@/lib/model/overlaySettings"
import {
  SensorType,
  type IntOffset,
  type SettingsEvent,
  type SettingsState,
} from "@/lib/model/settings"
import { reduceSettings } from "@/lib/model/settingsReducer"
import { safeInvoke } from "@/lib/tauri"

interface SettingsContextValue {
  state: SettingsState
  overlaySettings: OverlaySettings
  onEvent: (event: SettingsEvent) => void
  // getters mirroring the Compose TabContent wiring
  getCpuSensorReadings: () => HwSensor[]
  getGpuSensorReadings: () => HwSensor[]
  getNetworkSensorReadings: () => HwSensor[]
  getHardwareSensors: () => HwHardware[]
  getPresentMonApps: () => string[]
  getSensor: (type: SensorType) => Sensor
  getOverlayPosition: () => IntOffset
}

const SettingsContext = React.createContext<SettingsContextValue | null>(null)

function initialState(): SettingsState {
  return {
    overlaySettings: defaultOverlaySettings(),
    hardwareData: mockHardwareData(),
    isRecording: false,
    adminConsent: false,
    logSink: "",
  }
}

export function SettingsProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = React.useState<SettingsState>(initialState)

  const onEvent = React.useCallback((event: SettingsEvent) => {
    setState((prev) => reduceSettings(prev, event))
  }, [])

  // Relay settings to the overlay window (no-op outside Tauri).
  const overlaySettings = state.overlaySettings
  React.useEffect(() => {
    if (overlaySettings) {
      void safeInvoke("set_overlay_settings", { settings: overlaySettings })
    }
  }, [overlaySettings])

  const hardwareData: HardwareMonitorData | null = state.hardwareData

  const value = React.useMemo<SettingsContextValue>(() => {
    const settings = state.overlaySettings ?? defaultOverlaySettings()
    return {
      state,
      overlaySettings: settings,
      onEvent,
      getCpuSensorReadings: () =>
        hardwareData ? cpuReadings(hardwareData) : [],
      getGpuSensorReadings: () =>
        hardwareData ? gpuReadings(hardwareData) : [],
      getNetworkSensorReadings: () =>
        hardwareData ? networkReadings(hardwareData) : [],
      getHardwareSensors: () => hardwareData?.Hardwares ?? [],
      getPresentMonApps: () => hardwareData?.PresentMonApps ?? [],
      getSensor: (type: SensorType) => getSensor(settings, type),
      getOverlayPosition: () => ({
        x: settings.positionX,
        y: settings.positionY,
      }),
    }
  }, [state, hardwareData, onEvent])

  return (
    <SettingsContext.Provider value={value}>
      {children}
    </SettingsContext.Provider>
  )
}

// Mirror of the getSensor mapping in Settings.kt (TabContent).
function getSensor(settings: OverlaySettings, type: SensorType): Sensor {
  const s = settings.sensors
  switch (type) {
    case SensorType.Framerate:
      return s.framerate
    case SensorType.Frametime:
      return s.frametime
    case SensorType.CpuTemp:
      return s.cpuTemp
    case SensorType.CpuUsage:
      return s.cpuUsage
    case SensorType.GpuTemp:
      return s.gpuTemp
    case SensorType.GpuUsage:
      return s.gpuUsage
    case SensorType.VramUsage:
      return s.vramUsage
    case SensorType.TotalVramUsed:
      return s.totalVramUsed
    case SensorType.RamUsage:
      return s.ramUsage
    case SensorType.UpRate:
      return s.upRate
    case SensorType.DownRate:
      return s.downRate
    case SensorType.NetGraph:
      return s.upRate // no sensor for netgraph
    case SensorType.CpuConsumption:
      return s.cpuConsumption
    case SensorType.GpuConsumption:
      return s.gpuConsumption
  }
}

export function useSettings(): SettingsContextValue {
  const ctx = React.useContext(SettingsContext)
  if (!ctx)
    throw new Error("useSettings must be used within a SettingsProvider")
  return ctx
}
