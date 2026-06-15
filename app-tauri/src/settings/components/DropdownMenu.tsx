import { cn } from "@/lib/utils"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Disclaimer } from "./primitives"

/** DropdownMenu.kt — label + selected value + chevron, options by index. */
export function DropdownMenu({
  label,
  disclaimer,
  options,
  selectedIndex,
  onValueChanged,
  className,
}: {
  label?: string
  disclaimer?: string
  options: string[]
  selectedIndex: number
  onValueChanged: (index: number) => void
  className?: string
}) {
  const value = selectedIndex >= 0 ? String(selectedIndex) : undefined
  return (
    <div className={cn("flex w-full flex-col gap-2", className)}>
      <Select value={value} onValueChange={(v) => onValueChanged(Number(v))}>
        <SelectTrigger className="w-full">
          {label && (
            <span className="text-muted-foreground">{label}&nbsp;</span>
          )}
          <SelectValue placeholder="Select…" />
        </SelectTrigger>
        <SelectContent>
          {options.map((opt, i) => (
            <SelectItem key={`${opt}-${i}`} value={String(i)}>
              {opt}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
      {disclaimer && <Disclaimer>{disclaimer}</Disclaimer>}
    </div>
  )
}
