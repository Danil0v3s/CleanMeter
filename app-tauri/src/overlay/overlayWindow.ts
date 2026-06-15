// Applies overlay window behavior (size, position, click-through) from the
// current OverlaySettings + the measured content size. The overlay window is
// sized to its content (the bar), so position presets land the bar precisely
// at the chosen edge/corner. All calls are no-op outside Tauri.

import type { OverlaySettings } from "@/lib/model/overlaySettings"
import { isTauri } from "@/lib/tauri"

export interface OverlaySize {
  width: number
  height: number
}

export async function applyOverlayWindow(
  settings: OverlaySettings,
  size: OverlaySize,
): Promise<void> {
  if (!isTauri()) return
  if (size.width < 1 || size.height < 1) return
  try {
    const {
      getCurrentWindow,
      availableMonitors,
      primaryMonitor,
      LogicalSize,
      LogicalPosition,
    } = await import("@tauri-apps/api/window")

    const win = getCurrentWindow()
    const w = Math.ceil(size.width)
    const h = Math.ceil(size.height)
    await win.setSize(new LogicalSize(w, h))

    // Click-through + focusability follow the lock state.
    await win.setIgnoreCursorEvents(settings.isPositionLocked)

    // Custom absolute position.
    if (settings.positionIndex >= 6) {
      await win.setPosition(
        new LogicalPosition(settings.positionX, settings.positionY),
      )
      return
    }

    // Preset: 6 corners/edges relative to the selected monitor.
    const monitors = await availableMonitors()
    const monitor =
      monitors[settings.selectedDisplayIndex] ?? (await primaryMonitor())
    if (!monitor) return

    const sf = monitor.scaleFactor || 1
    const mw = monitor.size.width / sf
    const mh = monitor.size.height / sf
    const ox = monitor.position.x / sf
    const oy = monitor.position.y / sf

    const col = settings.positionIndex % 3 // 0 start, 1 center, 2 end
    const row = settings.positionIndex < 3 ? 0 : 1 // 0 top, 1 bottom

    const x =
      ox + (col === 0 ? 0 : col === 1 ? (mw - w) / 2 : mw - w)
    const y = oy + (row === 0 ? 0 : mh - h)

    await win.setPosition(new LogicalPosition(x, y))
  } catch (e) {
    console.warn("applyOverlayWindow failed", e)
  }
}
