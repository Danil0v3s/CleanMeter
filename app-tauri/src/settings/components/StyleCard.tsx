import * as React from "react"

import { cn } from "@/lib/utils"

/** StyleCard.kt — selectable card with a preview area and a label. */
export function StyleCard({
  label,
  isSelected,
  onClick,
  className,
  customLabel,
  children,
}: {
  label: string
  isSelected: boolean
  onClick: () => void
  className?: string
  customLabel?: React.ReactNode
  children?: React.ReactNode
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        "flex flex-col rounded-lg border bg-card p-1 text-left",
        isSelected ? "border-2 border-primary" : "border",
        className,
      )}
    >
      <div className="flex aspect-video w-full items-center justify-center overflow-hidden rounded-md bg-muted/50 p-2">
        {children}
      </div>
      <div className="p-3">
        {customLabel ?? (
          <span className="text-sm font-medium text-foreground">{label}</span>
        )}
      </div>
    </button>
  )
}
