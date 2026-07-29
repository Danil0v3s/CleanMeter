import * as React from "react"

import type { HardwareMonitorData } from "@/lib/model/hardwareMonitorData"
import {
  mergeOverlaySettings,
  type OverlaySettings,
} from "@/lib/model/overlaySettings"
import { isTauri, safeInvoke } from "@/lib/tauri"
import { mockOverlayData, mockOverlaySettings } from "@/overlay/mockOverlay"

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

  // Hydrate from the persisted settings, then listen for live changes pushed
  // from the settings window (Tauri only).
  React.useEffect(() => {
    if (!isTauri()) return
    let unlisten: (() => void) | undefined
    void (async () => {
      const saved = await safeInvoke<OverlaySettings | null>(
        "get_overlay_settings",
      )
      if (saved) setSettings(mergeOverlaySettings(saved))
      const { listen } = await import("@tauri-apps/api/event")
      unlisten = await listen<OverlaySettings>(
        "overlay-settings-changed",
        (event) => setSettings(event.payload),
      )
    })()
    return () => unlisten?.()
  }, [])

  // Real native feed: the Rust pipe client emits decoded backend readings.
  React.useEffect(() => {
    if (!isTauri()) return
    let unlisten: (() => void) | undefined
    void (async () => {
      const { listen } = await import("@tauri-apps/api/event")
      unlisten = await listen<HardwareMonitorData>("hardware-data", (event) =>
        setData(event.payload),
      )
    })()
    return () => unlisten?.()
  }, [])

  // Outside Tauri (browser preview) there's no backend, so fall back to the
  // animated mock feed.
  React.useEffect(() => {
    if (isTauri()) return
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
