// Applies overlay window behavior (size, position, click-through) from the
// current OverlaySettings. All calls are guarded and no-op outside Tauri.

import type { OverlaySettings } from "@/lib/model/overlaySettings"
import { isTauri } from "@/lib/tauri"

const HORIZONTAL = { width: 1280, height: 80 }
const VERTICAL = { width: 350, height: 1280 }

export async function applyOverlayWindow(
  settings: OverlaySettings,
): Promise<void> {
  if (!isTauri()) return
  try {
    const {
      getCurrentWindow,
      availableMonitors,
      primaryMonitor,
      LogicalSize,
      LogicalPosition,
    } = await import("@tauri-apps/api/window")

    const win = getCurrentWindow()
    const size = settings.isHorizontal ? HORIZONTAL : VERTICAL
    await win.setSize(new LogicalSize(size.width, size.height))

    // Click-through + focusability follow the lock state.
    await win.setIgnoreCursorEvents(settings.isPositionLocked)

    // Position: 6 presets (Top/Bottom × Start/Center/End) or absolute custom.
    if (settings.positionIndex >= 6) {
      await win.setPosition(
        new LogicalPosition(settings.positionX, settings.positionY),
      )
      return
    }

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

    const x = ox + (col === 0 ? 0 : col === 1 ? (mw - size.width) / 2 : mw - size.width)
    const y = oy + (row === 0 ? 0 : mh - size.height)

    await win.setPosition(new LogicalPosition(x, y))
  } catch (e) {
    console.warn("applyOverlayWindow failed", e)
  }
}
