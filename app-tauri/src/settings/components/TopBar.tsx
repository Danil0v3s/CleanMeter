import { Minus, X } from "lucide-react"

import { useWindow } from "@/contexts/WindowContext"
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip"

export function TopBar() {
  const { close, minimize } = useWindow()
  return (
    <div
      data-tauri-drag-region
      className="flex h-14 items-center justify-between border-b px-6"
    >
      <div className="flex items-center gap-2" data-tauri-drag-region>
        <img src="/logo.png" alt="logo" className="size-6" />
        <span className="font-medium text-foreground">Clean Meter</span>
      </div>
      <div className="flex items-center gap-4">
        <button
          type="button"
          aria-label="Minimize window"
          onClick={minimize}
          className="text-muted-foreground hover:text-foreground"
        >
          <Minus className="size-5" />
        </button>
        <Tooltip>
          <TooltipTrigger asChild>
            <button
              type="button"
              aria-label="Close window to tray"
              onClick={close}
              className="text-muted-foreground hover:text-foreground"
            >
              <X className="size-5" />
            </button>
          </TooltipTrigger>
          <TooltipContent>Closing will minimize to the Tray</TooltipContent>
        </Tooltip>
      </div>
    </div>
  )
}
