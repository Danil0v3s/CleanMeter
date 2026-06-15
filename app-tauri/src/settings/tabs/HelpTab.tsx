import * as React from "react"

import { useSettings } from "@/contexts/SettingsContext"
import { openExternal } from "@/lib/tauri"
import { Button } from "@/components/ui/button"
import {
  CollapsibleSection,
  ToggleSection,
} from "@/settings/components/Section"
import { HotKeySymbol } from "@/settings/components/primitives"

type FaqLink = { text: string; href?: string }

const FAQ: { question: string; answer: FaqLink[] }[] = [
  {
    question: "The sensors look wrong",
    answer: [{ text: "Try setting up each sensor via the Stats tab" }],
  },
  {
    question: "Neither sensors dropdown or the overlay are showing up",
    answer: [
      { text: "You need to have " },
      {
        text: ".NET Core Framework",
        href: "https://dotnet.microsoft.com/en-us/download/dotnet/thank-you/runtime-desktop-8.0.11-windows-x64-installer",
      },
      { text: " installed" },
    ],
  },
  {
    question: "Having problems like crashes or still nothing showing up?",
    answer: [
      { text: "Launch the app with --verbose params and ping us on our " },
      { text: "Discord Server", href: "https://discord.gg/phqwe89cvE" },
      { text: " or " },
      {
        text: "GitHub Issues",
        href: "https://github.com/Danil0v3s/CleanMeter/issues",
      },
    ],
  },
  {
    question: "Still has questions?",
    answer: [
      { text: "Join our " },
      { text: "Discord Server", href: "https://discord.gg/phqwe89cvE" },
    ],
  },
]

function NumberedList({ items }: { items: string[] }) {
  return (
    <div className="flex flex-col gap-3">
      {items.map((item, i) => (
        <div key={item} className="flex items-center gap-2">
          <span className="inline-flex size-6 items-center justify-center rounded-full bg-foreground text-xs font-semibold text-background">
            {i + 1}
          </span>
          <span className="text-sm font-medium text-foreground">{item}</span>
        </div>
      ))}
    </div>
  )
}

function Hotkey({ label, keys }: { label: string; keys: string[] }) {
  return (
    <div className="flex items-center justify-between">
      <span className="text-sm font-medium text-foreground">{label}</span>
      <HotKeySymbol keys={keys} />
    </div>
  )
}

export function HelpTab() {
  const { overlaySettings, onEvent, state } = useSettings()

  return (
    <div className="flex flex-col gap-4 pb-2 pt-5">
      <CollapsibleSection title="HOW TO SETUP">
        <NumberedList items={["Run it", "Setup the sensors", "Enjoy"]} />
      </CollapsibleSection>

      <CollapsibleSection title="CURRENT LIMITATIONS">
        <ul className="list-disc pl-5 text-sm font-medium text-foreground">
          <li>Doesn&apos;t work with exclusive fullscreen</li>
        </ul>
      </CollapsibleSection>

      <CollapsibleSection title="FREQUENTLY ASKED QUESTIONS">
        <div className="flex flex-col gap-4">
          {FAQ.map((item, i) => (
            <div key={item.question} className="flex flex-col gap-1.5">
              <p className="text-sm font-medium text-foreground">
                {i + 1}. {item.question}
              </p>
              <p className="pl-4 text-sm text-muted-foreground">
                {item.answer.map((part, j) =>
                  part.href ? (
                    <button
                      key={j}
                      type="button"
                      className="underline"
                      onClick={() => openExternal(part.href!)}
                    >
                      {part.text}
                    </button>
                  ) : (
                    <React.Fragment key={j}>{part.text}</React.Fragment>
                  ),
                )}
              </p>
            </div>
          ))}
        </div>
      </CollapsibleSection>

      <CollapsibleSection title="HOTKEYS">
        <div className="flex flex-col gap-3">
          <Hotkey label="Toggle the overlay" keys={["Ctrl", "Alt", "F10"]} />
          <Hotkey
            label="Toggle data recording"
            keys={["Ctrl", "Alt", "F11"]}
          />
        </div>
      </CollapsibleSection>

      <ToggleSection
        title="Application Logs"
        isEnabled={overlaySettings.isLoggingEnabled}
        onSwitchToggle={() => onEvent({ type: "ToggleLoggingEnabled" })}
      >
        <div className="flex flex-col gap-3">
          <Button className="self-start">Save logs to text</Button>
          <pre className="max-h-64 overflow-auto whitespace-pre-wrap rounded-md bg-muted/50 p-2 text-xs text-foreground">
            {state.logSink}
          </pre>
        </div>
      </ToggleSection>
    </div>
  )
}
