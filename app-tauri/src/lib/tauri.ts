// Thin Tauri bridge. Everything here is a no-op when running outside Tauri
// (e.g. plain `vite` in a browser), so the UI works in both contexts.

import { invoke } from "@tauri-apps/api/core"

export function isTauri(): boolean {
  return typeof window !== "undefined" && "__TAURI_INTERNALS__" in window
}

export async function openExternal(url: string): Promise<void> {
  if (isTauri()) {
    try {
      const { openUrl } = await import("@tauri-apps/plugin-opener")
      await openUrl(url)
      return
    } catch (e) {
      console.warn("openUrl failed", e)
    }
  }
  window.open(url, "_blank", "noopener,noreferrer")
}

export async function safeInvoke<T = unknown>(
  cmd: string,
  args?: Record<string, unknown>,
): Promise<T | undefined> {
  if (!isTauri()) return undefined
  try {
    return await invoke<T>(cmd, args)
  } catch (e) {
    console.warn(`invoke ${cmd} failed`, e)
    return undefined
  }
}
