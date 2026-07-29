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
  mergeOverlaySettings,
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
import { isTauri, safeInvoke } from "@/lib/tauri"

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

  // Serialized settings we last exchanged with the backend, used to break the
  // emit -> overlay-settings-changed -> receive -> emit echo loop between this
  // window and the overlay window (which can push position changes from drags).
  const lastSyncedRef = React.useRef<string>("")

  const onEvent = React.useCallback((event: SettingsEvent) => {
    setState((prev) => reduceSettings(prev, event))
  }, [])

  // True once the backend pipe is delivering data (first reading arrived).
  const [connected, setConnected] = React.useState(false)

  // True once persisted settings have been loaded (or determined absent). We
  // must not relay/persist before this, or the default settings would clobber
  // the saved file on startup.
  const [hydrated, setHydrated] = React.useState(false)
  React.useEffect(() => {
    if (!isTauri()) {
      setHydrated(true)
      return
    }
    let cancelled = false
    void (async () => {
      const saved = await safeInvoke<OverlaySettings | null>(
        "get_overlay_settings",
      )
      if (!cancelled && saved) {
        setState((prev) => ({
          ...prev,
          overlaySettings: mergeOverlaySettings(saved),
        }))
      }
      if (!cancelled) setHydrated(true)
    })()
    return () => {
      cancelled = true
    }
  }, [])

  // Real native feed: keep the sensor/PresentMon dropdowns backed by live data.
  React.useEffect(() => {
    if (!isTauri()) return
    let unlisten: (() => void) | undefined
    void (async () => {
      const { listen } = await import("@tauri-apps/api/event")
      unlisten = await listen<HardwareMonitorData>("hardware-data", (event) => {
        setConnected(true)
        setState((prev) => ({ ...prev, hardwareData: event.payload }))
      })
    })()
    return () => unlisten?.()
  }, [])

  // Mirror changes the overlay window pushes back (e.g. a drag-to-move updating
  // positionX/Y), so this window's state doesn't hold a stale position that a
  // later re-lock or unrelated toggle would clobber the dragged spot with.
  React.useEffect(() => {
    if (!isTauri()) return
    let unlisten: (() => void) | undefined
    void (async () => {
      const { listen } = await import("@tauri-apps/api/event")
      unlisten = await listen<OverlaySettings>(
        "overlay-settings-changed",
        (event) => {
          const merged = mergeOverlaySettings(event.payload)
          const json = JSON.stringify(merged)
          if (json === lastSyncedRef.current) return // our own echo
          lastSyncedRef.current = json
          setState((prev) => ({ ...prev, overlaySettings: merged }))
        },
      )
    })()
    return () => unlisten?.()
  }, [])

  // Relay settings to the overlay window and persist them (no-op outside Tauri).
  // Gated on `hydrated` so the saved file isn't overwritten with defaults at start.
  const overlaySettings = state.overlaySettings
  React.useEffect(() => {
    if (!hydrated || !overlaySettings) return
    const json = JSON.stringify(overlaySettings)
    // Skip if this exact value just arrived from the backend (our own echo, or a
    // position drag persisted by the overlay) — otherwise we'd bounce it back.
    if (json === lastSyncedRef.current) return
    lastSyncedRef.current = json
    void safeInvoke("set_overlay_settings", { settings: overlaySettings })
  }, [hydrated, overlaySettings])

  // Push the polling rate to the backend once connected and on every change
  // (mirrors the Kotlin sendInitialPollingRate + onPollingRateSelect).
  const pollingRate = overlaySettings?.pollingRate
  React.useEffect(() => {
    if (!connected || pollingRate == null) return
    void safeInvoke("select_polling_rate", { interval: pollingRate })
  }, [connected, pollingRate])

  // Push the selected PresentMon app to the backend (mirrors onFpsApplicationSelect).
  // Skip the empty default — the backend stays on its "Auto" selection.
  const currentPresentMonApp = overlaySettings?.currentPresentMonApp
  React.useEffect(() => {
    if (!connected || !currentPresentMonApp) return
    void safeInvoke("select_present_mon_app", { name: currentPresentMonApp })
  }, [connected, currentPresentMonApp])

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
