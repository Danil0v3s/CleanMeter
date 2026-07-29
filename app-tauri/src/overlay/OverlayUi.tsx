import * as React from "react"

import { useOverlay } from "@/contexts/OverlayContext"
import { isTauri, safeInvoke } from "@/lib/tauri"
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

  // The overlay is draggable only in custom-position mode while unlocked. In
  // every other state the window is click-through (setIgnoreCursorEvents), so
  // these handlers never receive events anyway.
  const draggable = settings.positionIndex >= 6 && !settings.isPositionLocked

  // Manual drag-to-move. We can't use `data-tauri-drag-region`/startDragging
  // because the overlay window is created with `focus: false` (WS_EX_NOACTIVATE
  // on Windows) so it never activates — the OS move loop won't start. Instead we
  // track the pointer in screen space and reposition the window ourselves, then
  // persist the final spot so it survives restarts.
  React.useEffect(() => {
    const el = barRef.current
    if (!draggable || !isTauri() || !el) return
    let cleanup = () => {}
    let cancelled = false
    void (async () => {
      const { getCurrentWindow, LogicalPosition } = await import(
        "@tauri-apps/api/window"
      )
      if (cancelled) return
      const win = getCurrentWindow()
      const sf = (await win.scaleFactor()) || 1

      let dragging = false
      let startScreenX = 0
      let startScreenY = 0
      let baseX = 0 // window's logical top-left at drag start
      let baseY = 0

      const onDown = (e: PointerEvent) => {
        if (e.button !== 0) return
        e.preventDefault()
        void (async () => {
          const p = await win.outerPosition() // physical px
          baseX = p.x / sf
          baseY = p.y / sf
          startScreenX = e.screenX
          startScreenY = e.screenY
          dragging = true
          try {
            el.setPointerCapture(e.pointerId)
          } catch {
            /* capture is best-effort */
          }
        })()
      }
      const onMove = (e: PointerEvent) => {
        if (!dragging) return
        const nx = Math.round(baseX + (e.screenX - startScreenX))
        const ny = Math.round(baseY + (e.screenY - startScreenY))
        void win.setPosition(new LogicalPosition(nx, ny))
      }
      const onUp = (e: PointerEvent) => {
        if (!dragging) return
        dragging = false
        try {
          el.releasePointerCapture(e.pointerId)
        } catch {
          /* release is best-effort */
        }
        void (async () => {
          const p = await win.outerPosition()
          const x = Math.round(p.x / sf)
          const y = Math.round(p.y / sf)
          // Persist through the same path the settings window uses, so the new
          // spot is saved and both windows stay in sync.
          void safeInvoke("set_overlay_settings", {
            settings: { ...settingsRef.current, positionX: x, positionY: y },
          })
        })()
      }

      el.addEventListener("pointerdown", onDown)
      el.addEventListener("pointermove", onMove)
      el.addEventListener("pointerup", onUp)
      el.addEventListener("pointercancel", onUp)
      cleanup = () => {
        el.removeEventListener("pointerdown", onDown)
        el.removeEventListener("pointermove", onMove)
        el.removeEventListener("pointerup", onUp)
        el.removeEventListener("pointercancel", onUp)
      }
    })()
    return () => {
      cancelled = true
      cleanup()
    }
  }, [draggable])

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
        // Size to content, not the (capped) available window width — otherwise
        // the background stops growing and children bleed out once they exceed
        // the window width.
        width: "max-content",
        transform: `scale(${scale})`,
        transformOrigin: "top left",
        padding: 16,
        // When unlocked, signal the bar is grabbable and disable text selection
        // so a drag doesn't select the labels underneath.
        cursor: draggable ? "move" : "default",
        userSelect: draggable ? "none" : undefined,
        touchAction: draggable ? "none" : undefined,
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
          // Hint that the overlay is in "move mode" while unlocked.
          outline: draggable ? "2px dashed rgba(255, 255, 255, 0.6)" : undefined,
          outlineOffset: 2,
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
