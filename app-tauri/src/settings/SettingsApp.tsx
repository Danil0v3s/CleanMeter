import * as React from "react"
import { Gauge, HelpCircle, Layers, Settings as SettingsIcon } from "lucide-react"

import { cn } from "@/lib/utils"
import { ThemeProvider, useTheme } from "@/components/theme-provider"
import { TooltipProvider } from "@/components/ui/tooltip"
import { SettingsProvider, useSettings } from "@/contexts/SettingsContext"
import { WindowProvider } from "@/contexts/WindowContext"
import { TopBar } from "@/settings/components/TopBar"
import { AdminConsent } from "@/settings/components/AdminConsent"
import { StatsTab } from "@/settings/tabs/StatsTab"
import { StyleTab } from "@/settings/tabs/StyleTab"
import { AppSettingsTab } from "@/settings/tabs/AppSettingsTab"
import { HelpTab } from "@/settings/tabs/HelpTab"

export function SettingsApp() {
  return (
    <ThemeProvider defaultTheme="light" enableHotkey={false}>
      <WindowProvider>
        <SettingsProvider>
          <TooltipProvider delayDuration={0}>
            <ThemeSync />
            <Shell />
          </TooltipProvider>
        </SettingsProvider>
      </WindowProvider>
    </ThemeProvider>
  )
}

/** Keeps the visual theme in sync with overlaySettings.isDarkTheme. */
function ThemeSync() {
  const { overlaySettings } = useSettings()
  const { setTheme } = useTheme()
  React.useEffect(() => {
    setTheme(overlaySettings.isDarkTheme ? "dark" : "light")
  }, [overlaySettings.isDarkTheme, setTheme])
  return null
}

type TabId = 0 | 1 | 2 | 3

function TabButton({
  active,
  onClick,
  icon,
  label,
  className,
}: {
  active: boolean
  onClick: () => void
  icon: React.ReactNode
  label?: string
  className?: string
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        "flex h-11 items-center gap-2 rounded-full border px-4 text-sm font-medium transition-colors",
        active
          ? "border-transparent bg-foreground text-background"
          : "bg-card text-muted-foreground hover:text-foreground",
        className,
      )}
    >
      {icon}
      {label && <span>{label}</span>}
    </button>
  )
}

function Shell() {
  const { state } = useSettings()
  const [tab, setTab] = React.useState<TabId>(0)

  return (
    <div className="flex h-screen flex-col overflow-hidden rounded-xl border bg-background">
      <TopBar />
      {!state.adminConsent ? (
        <AdminConsent />
      ) : (
        <div className="flex min-h-0 flex-1 flex-col px-6">
          <div className="flex items-center gap-2 py-4">
            <TabButton
              active={tab === 0}
              onClick={() => setTab(0)}
              icon={<Gauge className="size-4" />}
              label="Stats"
            />
            <TabButton
              active={tab === 1}
              onClick={() => setTab(1)}
              icon={<Layers className="size-4" />}
              label="Style"
            />
            <TabButton
              active={tab === 2}
              onClick={() => setTab(2)}
              icon={<SettingsIcon className="size-4" />}
              label="Settings"
            />
            <div className="flex-1" />
            <TabButton
              active={tab === 3}
              onClick={() => setTab(3)}
              icon={<HelpCircle className="size-4" />}
            />
          </div>

          <div className="min-h-0 flex-1 overflow-y-auto pb-6">
            {tab === 0 && <StatsTab />}
            {tab === 1 && <StyleTab />}
            {tab === 2 && <AppSettingsTab />}
            {tab === 3 && <HelpTab />}
          </div>
        </div>
      )}
    </div>
  )
}
