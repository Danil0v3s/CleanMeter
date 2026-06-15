import * as React from "react"

import { useSettings } from "@/contexts/SettingsContext"
import { Button } from "@/components/ui/button"
import { Section } from "@/settings/components/Section"
import { StyleCard } from "@/settings/components/StyleCard"
import { CheckboxWithLabel } from "@/settings/components/primitives"
import { DropdownMenu } from "@/settings/components/DropdownMenu"
import { Footer } from "./Footer"

const POLLING_OPTIONS = ["33", "50", "100", "250", "300", "350", "400", "500"]

export function AppSettingsTab() {
  const { overlaySettings, onEvent } = useSettings()
  const [startWithWindows, setStartWithWindows] = React.useState(false)
  const [startMinimized, setStartMinimized] = React.useState(false)

  return (
    <div className="flex flex-col gap-4 pt-5">
      <Section title="GENERAL">
        <div className="flex flex-col items-start gap-3">
          <CheckboxWithLabel
            label="Start with Windows"
            checked={startWithWindows}
            onCheckedChange={setStartWithWindows}
          />
          <CheckboxWithLabel
            label="Start Minimized"
            checked={startMinimized}
            onCheckedChange={setStartMinimized}
          />
          <Button variant="ghost" className="px-0">
            Clear app preferences
          </Button>
        </div>
      </Section>

      <Section title="APPEARANCE">
        <div className="grid grid-cols-2 gap-4">
          <StyleCard
            label="Light"
            isSelected={!overlaySettings.isDarkTheme}
            onClick={() =>
              onEvent({ type: "DarkThemeToggle", isEnabled: false })
            }
            className="h-52"
          >
            <img src="/icons/light_mode.png" alt="Light" className="max-h-full" />
          </StyleCard>
          <StyleCard
            label="Dark"
            isSelected={overlaySettings.isDarkTheme}
            onClick={() => onEvent({ type: "DarkThemeToggle", isEnabled: true })}
            className="h-52"
          >
            <img src="/icons/dark_mode.png" alt="Dark" className="max-h-full" />
          </StyleCard>
        </div>
      </Section>

      <Section title="RECORDING">
        <DropdownMenu
          className="pt-2"
          label="Polling Rate:"
          disclaimer="The interval in milliseconds the app will update data. Be mindful, this can impact performance!"
          options={POLLING_OPTIONS}
          selectedIndex={POLLING_OPTIONS.indexOf(
            String(overlaySettings.pollingRate),
          )}
          onValueChanged={(i) =>
            onEvent({
              type: "PollingRateSelect",
              pollingRate: Number(POLLING_OPTIONS[i]),
            })
          }
        />
      </Section>

      <Footer />
    </div>
  )
}
