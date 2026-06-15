import { useOverlay } from "@/contexts/OverlayContext"
import { mapScale } from "./tokens"
import { FpsSection } from "./sections/FpsSection"
import { CpuSection } from "./sections/CpuSection"
import { GpuSection } from "./sections/GpuSection"
import { RamSection } from "./sections/RamSection"
import { NetSection } from "./sections/NetSection"

export function OverlayUi() {
  const { settings, data } = useOverlay()
  if (!data) return null

  const scale = mapScale(settings.scale)
  const sections = (
    <>
      <FpsSection settings={settings} data={data} />
      <CpuSection settings={settings} data={data} />
      <GpuSection settings={settings} data={data} />
      <RamSection settings={settings} data={data} />
      <NetSection settings={settings} data={data} />
    </>
  )

  if (settings.isHorizontal) {
    return (
      <div
        style={{
          height: "100vh",
          display: "flex",
          alignItems: "center",
          overflow: "hidden",
        }}
      >
        <div
          style={{
            transform: `scale(${scale})`,
            transformOrigin: "center",
            margin: 16,
            background: "rgba(0, 0, 0, 0.36)",
            borderRadius: 9999,
            padding: 4,
            display: "flex",
            alignItems: "center",
          }}
        >
          {/* Content row — fillMaxHeight + spacedBy(8). 40px = window(80) - 16*2 - 4*2. */}
          <div
            style={{
              display: "flex",
              height: 40,
              alignItems: "stretch",
              gap: 8,
            }}
          >
            {sections}
          </div>
        </div>
      </div>
    )
  }

  return (
    <div style={{ display: "flex", overflow: "hidden" }}>
      <div
        style={{
          transform: `scale(${scale})`,
          transformOrigin: "top left",
          margin: 16,
          background: "rgba(0, 0, 0, 0.36)",
          borderRadius: 12,
          padding: 4,
          display: "flex",
          flexDirection: "column",
          alignItems: "stretch",
        }}
      >
        {/* Vertical content — equal-width columns (align-items: stretch) + spacedBy(4). */}
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            gap: 4,
            alignItems: "stretch",
          }}
        >
          {sections}
        </div>
      </div>
    </div>
  )
}
