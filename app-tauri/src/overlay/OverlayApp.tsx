import { OverlayProvider } from "@/contexts/OverlayContext"
import { OverlayUi } from "./OverlayUi"

export function OverlayApp() {
  return (
    <OverlayProvider>
      <OverlayUi />
    </OverlayProvider>
  )
}
