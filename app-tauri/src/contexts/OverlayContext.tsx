import * as React from "react"

import type { HardwareMonitorData } from "@/lib/model/hardwareMonitorData"
import type { OverlaySettings } from "@/lib/model/overlaySettings"
import { isTauri } from "@/lib/tauri"
import { mockOverlayData, mockOverlaySettings } from "@/overlay/mockOverlay"
import { applyOverlayWindow } from "@/overlay/overlayWindow"

interface OverlayContextValue {
  settings: OverlaySettings
  data: HardwareMonitorData | null
}

const OverlayContext = React.createContext<OverlayContextValue | null>(null)

export function OverlayProvider({ children }: { children: React.ReactNode }) {
  const [settings, setSettings] = React.useState<OverlaySettings>(
    mockOverlaySettings,
  )
  const [data, setData] = React.useState<HardwareMonitorData | null>(null)
  const tickRef = React.useRef(0)

  // Listen for settings pushed from the settings window (Tauri only).
  React.useEffect(() => {
    if (!isTauri()) return
    let unlisten: (() => void) | undefined
    void (async () => {
      const { listen } = await import("@tauri-apps/api/event")
      unlisten = await listen<OverlaySettings>(
        "overlay-settings-changed",
        (event) => setSettings(event.payload),
      )
    })()
    return () => unlisten?.()
  }, [])

  // Apply window size / position / click-through whenever settings change.
  React.useEffect(() => {
    void applyOverlayWindow(settings)
  }, [settings])

  // Drive the mock data feed. The real native feed replaces this later.
  React.useEffect(() => {
    setData(mockOverlayData(0))
    const interval = window.setInterval(() => {
      tickRef.current += 1
      setData(mockOverlayData(tickRef.current))
    }, settings.pollingRate)
    return () => window.clearInterval(interval)
  }, [settings.pollingRate])

  const value = React.useMemo(() => ({ settings, data }), [settings, data])
  return (
    <OverlayContext.Provider value={value}>{children}</OverlayContext.Provider>
  )
}

export function useOverlay(): OverlayContextValue {
  const ctx = React.useContext(OverlayContext)
  if (!ctx) throw new Error("useOverlay must be used within an OverlayProvider")
  return ctx
}
