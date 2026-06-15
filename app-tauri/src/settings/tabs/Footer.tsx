import { ChevronRight } from "lucide-react"

import { openExternal } from "@/lib/tauri"

const RELEASES = "https://github.com/Danil0v3s/CleanMeter/releases/latest"
const DISCORD = "https://discord.gg/phqwe89cvE"
const KOFI = "https://ko-fi.com/danil0v3s"

function LinkRow({
  href,
  icon,
  label,
  className,
}: {
  href: string
  icon: string
  label: string
  className?: string
}) {
  return (
    <button
      type="button"
      onClick={() => openExternal(href)}
      className={`flex items-center justify-between rounded-xl border bg-transparent p-3 text-left ${className ?? ""}`}
    >
      <span className="flex items-center gap-2">
        <img src={icon} alt="" className="size-8" />
        <span className="text-sm font-medium text-foreground">{label}</span>
      </span>
      <ChevronRight className="size-5 text-muted-foreground" />
    </button>
  )
}

export function Footer() {
  return (
    <div className="flex w-full flex-col gap-3">
      <LinkRow
        href={RELEASES}
        icon="/icons/github.png"
        label="Check the latest build"
      />
      <div className="grid grid-cols-2 gap-3">
        <LinkRow
          href={DISCORD}
          icon="/icons/discord.png"
          label="Join the discord server!"
        />
        <LinkRow
          href={KOFI}
          icon="/icons/ko-fi.png"
          label="Like the work? Support us!"
        />
      </div>
      <div className="flex h-8 items-center justify-between text-xs text-muted-foreground">
        <span>
          Built by{" "}
          <button
            type="button"
            className="underline"
            onClick={() => openExternal("https://github.com/Danil0v3s")}
          >
            Danil0v3s
          </button>{" "}
          & designed by{" "}
          <button
            type="button"
            className="underline"
            onClick={() => openExternal("https://www.instagram.com/mars.designs")}
          >
            Mars
          </button>
        </span>
        <button
          type="button"
          className="underline"
          onClick={() => openExternal(RELEASES)}
        >
          Version dev
        </button>
      </div>
    </div>
  )
}
