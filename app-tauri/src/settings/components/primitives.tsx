import * as React from "react"
import { Info, Plus } from "lucide-react"

import { cn } from "@/lib/utils"
import { Checkbox } from "@/components/ui/checkbox"

export function SectionTitle({ title }: { title: string }) {
  return (
    <span className="text-sm font-semibold tracking-wide text-foreground">
      {title}
    </span>
  )
}

export function Disclaimer({ children }: { children: React.ReactNode }) {
  return (
    <p className="text-xs leading-snug text-muted-foreground">{children}</p>
  )
}

export function CheckboxWithLabel({
  label,
  checked,
  enabled = true,
  onCheckedChange,
  trailingItem,
}: {
  label: string
  checked: boolean
  enabled?: boolean
  onCheckedChange: (checked: boolean) => void
  trailingItem?: React.ReactNode
}) {
  return (
    <label
      className={cn(
        "flex items-center gap-2",
        enabled ? "cursor-pointer" : "cursor-not-allowed opacity-60",
      )}
    >
      <Checkbox
        checked={checked}
        disabled={!enabled}
        onCheckedChange={(v) => onCheckedChange(v === true)}
      />
      <span className="text-sm font-medium text-foreground">{label}</span>
      {trailingItem}
    </label>
  )
}

export function HotKeySymbol({ keys }: { keys: string[] }) {
  return (
    <div className="flex items-center gap-1">
      {keys.map((key, index) => (
        <React.Fragment key={key}>
          <span className="inline-flex min-w-10 justify-center rounded-md bg-foreground px-2 py-1.5 text-xs text-background">
            {key}
          </span>
          {index !== keys.length - 1 && (
            <Plus className="size-2.5 text-muted-foreground" />
          )}
        </React.Fragment>
      ))}
    </div>
  )
}

export function KeyboardShortcutInfoLabel() {
  return (
    <div className="flex items-center justify-between rounded-xl border px-4 py-5">
      <div className="flex items-center gap-2">
        <Info className="size-5 text-muted-foreground" />
        <span className="text-sm font-medium text-foreground">
          Hot key for showing/hiding the overlay
        </span>
      </div>
      <HotKeySymbol keys={["Ctrl", "Alt", "F10"]} />
    </div>
  )
}
