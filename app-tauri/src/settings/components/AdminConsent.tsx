import { useSettings } from "@/contexts/SettingsContext"
import { useTheme } from "@/components/theme-provider"
import { useWindow } from "@/contexts/WindowContext"
import { Button } from "@/components/ui/button"

export function AdminConsent() {
  const { onEvent } = useSettings()
  const { resolvedTheme } = useTheme()
  const { exit } = useWindow()

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
        To function properly, CleanMeter requires administrative permissions and
        access to your local network. This is necessary for our processes to
        communicate with each other using sockets.
        <br />
        <br />
        If you&apos;re okay with this, please grant the permissions below.
      </p>
      <div className="flex gap-3">
        <Button variant="secondary" onClick={exit}>
          Close app
        </Button>
        <Button onClick={() => onEvent({ type: "ConsentGiven" })}>Allow</Button>
      </div>
    </div>
  )
}
