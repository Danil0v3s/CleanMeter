import * as React from "react"
import { ChevronRight } from "lucide-react"

import { cn } from "@/lib/utils"
import { Switch } from "@/components/ui/switch"
import {
  type CheckboxSectionOption,
} from "@/lib/model/settings"
import { CheckboxWithLabel, SectionTitle } from "./primitives"
import { DropdownMenu } from "./DropdownMenu"

/** The rounded card wrapper every settings section sits in (SectionBody.kt). */
export function SectionBody({
  className,
  children,
}: {
  className?: string
  children: React.ReactNode
}) {
  return (
    <div
      className={cn(
        "flex flex-col gap-5 rounded-xl border bg-card p-5",
        className,
      )}
    >
      {children}
    </div>
  )
}

/** Simple titled section (Section.kt). */
export function Section({
  title,
  children,
}: {
  title: string
  children: React.ReactNode
}) {
  return (
    <SectionBody>
      <div className="flex items-center justify-between">
        <SectionTitle title={title} />
      </div>
      {children}
    </SectionBody>
  )
}

/** CheckboxSection.kt — master toggle + checkbox list. */
export function CheckboxSection({
  title,
  options,
  onOptionToggle,
  onSwitchToggle,
}: {
  title: string
  options: CheckboxSectionOption[]
  onOptionToggle: (option: CheckboxSectionOption) => void
  onSwitchToggle: (enabled: boolean) => void
}) {
  const isAnySelected = options.some((o) => o.isSelected)
  return (
    <SectionBody>
      <div className="flex items-center justify-between">
        <SectionTitle title={title} />
        <Switch checked={isAnySelected} onCheckedChange={onSwitchToggle} />
      </div>
      {isAnySelected && (
        <div className="flex flex-col gap-3">
          {options.map((option) => (
            <CheckboxWithLabel
              key={option.type}
              label={option.name}
              checked={option.isSelected}
              onCheckedChange={() =>
                onOptionToggle({ ...option, isSelected: !option.isSelected })
              }
            />
          ))}
        </div>
      )}
    </SectionBody>
  )
}

/** CustomBodyCheckboxSection.kt — master toggle + custom body. */
export function CustomBodyCheckboxSection({
  title,
  options,
  onSwitchToggle,
  body,
}: {
  title: string
  options: CheckboxSectionOption[]
  onSwitchToggle: (enabled: boolean) => void
  body: (options: CheckboxSectionOption[]) => React.ReactNode
}) {
  const isAnySelected = options.some((o) => o.isSelected)
  return (
    <SectionBody>
      <div className="flex items-center justify-between">
        <SectionTitle title={title} />
        <Switch checked={isAnySelected} onCheckedChange={onSwitchToggle} />
      </div>
      {isAnySelected && body(options)}
    </SectionBody>
  )
}

/** ToggleSection.kt — master toggle gates content visibility. */
export function ToggleSection({
  title,
  isEnabled,
  onSwitchToggle,
  children,
}: {
  title: string
  isEnabled: boolean
  onSwitchToggle: (enabled: boolean) => void
  children: React.ReactNode
}) {
  return (
    <SectionBody>
      <div className="flex items-center justify-between">
        <SectionTitle title={title.toUpperCase()} />
        <Switch checked={isEnabled} onCheckedChange={onSwitchToggle} />
      </div>
      {isEnabled && children}
    </SectionBody>
  )
}

/** CollapsibleSection.kt — header row toggles content. */
export function CollapsibleSection({
  title,
  children,
}: {
  title: string
  children: React.ReactNode
}) {
  const [expanded, setExpanded] = React.useState(false)
  return (
    <SectionBody>
      <button
        type="button"
        className="flex items-center justify-between"
        onClick={() => setExpanded((e) => !e)}
      >
        <SectionTitle title={title} />
        <ChevronRight
          className="size-5 text-muted-foreground transition-transform"
          style={{ transform: expanded ? "rotate(270deg)" : "rotate(90deg)" }}
        />
      </button>
      {expanded && children}
    </SectionBody>
  )
}

/** DropdownSection.kt — title + dropdown. */
export function DropdownSection({
  title,
  options,
  selectedIndex,
  onValueChanged,
}: {
  title: string
  options: string[]
  selectedIndex: number
  onValueChanged: (index: number) => void
}) {
  return (
    <SectionBody>
      <div className="flex items-center justify-between">
        <SectionTitle title={title} />
      </div>
      <DropdownMenu
        options={options}
        selectedIndex={selectedIndex}
        onValueChanged={onValueChanged}
      />
    </SectionBody>
  )
}
