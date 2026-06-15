import * as React from "react"

import { isTauri } from "@/lib/tauri"

interface WindowContextValue {
  // Close minimizes to the tray (mirrors the Compose behavior).
  close: () => void
  minimize: () => void
  exit: () => void
}

const WindowContext = React.createContext<WindowContextValue | null>(null)

async function currentWindow() {
  const { getCurrentWindow } = await import("@tauri-apps/api/window")
  return getCurrentWindow()
}

export function WindowProvider({ children }: { children: React.ReactNode }) {
  const value = React.useMemo<WindowContextValue>(
    () => ({
      close: () => {
        if (!isTauri()) return
        void currentWindow().then((w) => w.hide())
      },
      minimize: () => {
        if (!isTauri()) return
        void currentWindow().then((w) => w.minimize())
      },
      exit: () => {
        if (!isTauri()) return
        void currentWindow().then((w) => w.close())
      },
    }),
    [],
  )

  return (
    <WindowContext.Provider value={value}>{children}</WindowContext.Provider>
  )
}

export function useWindow(): WindowContextValue {
  const ctx = React.useContext(WindowContext)
  if (!ctx) throw new Error("useWindow must be used within a WindowProvider")
  return ctx
}
