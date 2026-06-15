import * as React from "react"

import { useOverlay } from "@/contexts/OverlayContext"
import { mapScale } from "./tokens"
import { applyOverlayWindow } from "./overlayWindow"
import { FpsSection } from "./sections/FpsSection"
import { CpuSection } from "./sections/CpuSection"
import { GpuSection } from "./sections/GpuSection"
import { RamSection } from "./sections/RamSection"
import { NetSection } from "./sections/NetSection"

export function OverlayUi() {
  const { settings, data } = useOverlay()
  const barRef = React.useRef<HTMLDivElement>(null)
  const settingsRef = React.useRef(settings)
  settingsRef.current = settings

  // Size the Tauri window to the rendered bar so position presets land the
  // content exactly at the chosen edge. Re-runs on settings change (position,
  // scale, orientation, lock) and on content resize (sections toggling).
  React.useLayoutEffect(() => {
    const el = barRef.current
    if (!el) return
    const measure = () => {
      const rect = el.getBoundingClientRect()
      void applyOverlayWindow(settingsRef.current, {
        width: rect.width,
        height: rect.height,
      })
    }
    measure()
    const ro = new ResizeObserver(measure)
    ro.observe(el)
    return () => ro.disconnect()
  }, [settings])

  if (!data) return null

  const scale = mapScale(settings.scale)
  const horizontal = settings.isHorizontal
  const sections = (
    <>
      <FpsSection settings={settings} data={data} />
      <CpuSection settings={settings} data={data} />
      <GpuSection settings={settings} data={data} />
      <RamSection settings={settings} data={data} />
      <NetSection settings={settings} data={data} />
    </>
  )

  // Outermost: scale + 16px transparent outer padding (Compose modifier order).
  // The element is anchored at the window's top-left and the window is sized to
  // match its rendered bounds.
  return (
    <div
      ref={barRef}
      style={{
        display: "inline-flex",
        transform: `scale(${scale})`,
        transformOrigin: "top left",
        padding: 16,
      }}
    >
      <div
        style={{
          display: "flex",
          flexDirection: horizontal ? "row" : "column",
          alignItems: horizontal ? "center" : "stretch",
          background: "rgba(0, 0, 0, 0.36)",
          borderRadius: horizontal ? 9999 : 12,
          padding: 4,
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: horizontal ? "row" : "column",
            gap: horizontal ? 8 : 4,
            // Content fills the bar height (40px) in horizontal mode.
            ...(horizontal
              ? { height: 40, alignItems: "stretch" }
              : { alignItems: "stretch" }),
          }}
        >
          {sections}
        </div>
      </div>
    </div>
  )
}
