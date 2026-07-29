import * as React from "react"
import { Loader2 } from "lucide-react"

import { useSettings } from "@/contexts/SettingsContext"
import { useTheme } from "@/components/theme-provider"
import { useWindow } from "@/contexts/WindowContext"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import { isTauri, safeInvoke } from "@/lib/tauri"

type ServiceStatus = { installed: boolean; running: boolean }
type ServiceKey = "presentmon" | "hardwaremonitor"

const SERVICES: { key: ServiceKey; label: string; description: string }[] = [
  {
    key: "presentmon",
    label: "PresentMon",
    description: "Captures in-game FPS and frame times.",
  },
  {
    key: "hardwaremonitor",
    label: "Hardware Monitor",
    description: "Reads CPU, GPU, memory and network sensors.",
  },
]

const UNKNOWN: ServiceStatus = { installed: false, running: false }

export function AdminConsent() {
  const { onEvent } = useSettings()
  const { resolvedTheme } = useTheme()
  const { exit } = useWindow()

  const [statuses, setStatuses] = React.useState<Record<ServiceKey, ServiceStatus>>({
    presentmon: UNKNOWN,
    hardwaremonitor: UNKNOWN,
  })
  const [busy, setBusy] = React.useState<ServiceKey | null>(null)

  const refresh = React.useCallback(async () => {
    if (!isTauri()) return
    const entries = await Promise.all(
      SERVICES.map(
        async (s) =>
          [
            s.key,
            (await safeInvoke<ServiceStatus>("check_service", { key: s.key })) ??
              UNKNOWN,
          ] as const,
      ),
    )
    setStatuses((prev) => ({ ...prev, ...Object.fromEntries(entries) }))
  }, [])

  // Poll status so the UI reflects the result of the (async, self-elevating)
  // install script once the user clears the UAC prompt.
  React.useEffect(() => {
    if (!isTauri()) return
    void refresh()
    const id = setInterval(() => void refresh(), 2000)
    return () => clearInterval(id)
  }, [refresh])

  const enable = async (key: ServiceKey) => {
    setBusy(key)
    await safeInvoke("install_service", { key })
    // Give the UAC prompt + service start a beat; polling reports the outcome.
    window.setTimeout(() => setBusy((b) => (b === key ? null : b)), 5000)
  }

  // Outside Tauri (browser dev) there are no services to gate on.
  const allRunning = !isTauri() || SERVICES.every((s) => statuses[s.key].running)

  return (
    <div className="flex size-full flex-col items-center justify-center gap-6 p-12 text-center">
      <div className="w-[400px] overflow-hidden rounded-xl bg-card pt-6">
        <img
          src={`/icons/onboarding_${resolvedTheme === "dark" ? "dark" : "light"}.png`}
          alt=""
          className="mx-auto"
        />
      </div>
      <h1 className="text-2xl text-foreground">Administrative Privileges</h1>
      <p className="max-w-md text-sm leading-relaxed text-muted-foreground">
        Thank you for choosing CleanMeter!
        <br />
        <br />
        CleanMeter relies on two background services. Installing or starting them
        requires administrator approval — you&apos;ll see a prompt for each.
      </p>

      <div className="flex w-[440px] flex-col gap-2">
        {SERVICES.map((s) => {
          const st = statuses[s.key]
          const statusLabel = st.running
            ? "Running"
            : st.installed
              ? "Stopped"
              : "Not installed"
          return (
            <div
              key={s.key}
              className="flex items-center gap-3 rounded-lg border bg-card p-3 text-left"
            >
              <span
                className={cn(
                  "size-2 shrink-0 rounded-full",
                  st.running ? "bg-green-500" : st.installed ? "bg-amber-500" : "bg-muted-foreground/40",
                )}
              />
              <div className="min-w-0 flex-1">
                <div className="text-sm font-medium text-foreground">{s.label}</div>
                <div className="truncate text-xs text-muted-foreground">
                  {s.description}
                </div>
              </div>
              <span className="text-xs text-muted-foreground">{statusLabel}</span>
              {!st.running && (
                <Button
                  size="sm"
                  disabled={busy === s.key}
                  onClick={() => void enable(s.key)}
                >
                  {busy === s.key ? (
                    <Loader2 className="size-3 animate-spin" />
                  ) : st.installed ? (
                    "Start"
                  ) : (
                    "Install"
                  )}
                </Button>
              )}
            </div>
          )
        })}
      </div>

      <div className="flex gap-3">
        <Button variant="secondary" onClick={exit}>
          Close app
        </Button>
        <Button
          disabled={!allRunning}
          onClick={() => onEvent({ type: "ConsentGiven" })}
        >
          Continue
        </Button>
      </div>
    </div>
  )
}
