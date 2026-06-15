import { cn } from "@/lib/utils"
import { useSettings } from "@/contexts/SettingsContext"
import { ProgressType } from "@/lib/model/overlaySettings"
import { Separator } from "@/components/ui/separator"
import { Slider } from "@/components/ui/slider"
import { Switch } from "@/components/ui/switch"
import {
  CollapsibleSection,
  DropdownSection,
  ToggleSection,
} from "@/settings/components/Section"
import { StyleCard } from "@/settings/components/StyleCard"

export function StyleTab() {
  const { overlaySettings, onEvent } = useSettings()

  return (
    <div className="flex flex-col gap-4 pb-2 pt-5">
      <Position />
      <Orientation />
      <Opacity />
      <Scale />
      <GraphType />
      <DropdownSection
        title="MONITOR"
        options={["Display 1"]}
        selectedIndex={overlaySettings.selectedDisplayIndex}
        onValueChanged={(displayIndex) =>
          onEvent({ type: "DisplaySelect", displayIndex })
        }
      />
    </div>
  )
}

const positionAlignments = [
  "items-start justify-start",
  "items-start justify-center",
  "items-start justify-end",
  "items-end justify-start",
  "items-end justify-center",
  "items-end justify-end",
] as const

const positionLabels = [
  "Top left",
  "Top middle",
  "Top right",
  "Bottom left",
  "Bottom middle",
  "Bottom right",
]

function PositionMarker({
  index,
  selected,
}: {
  index: number
  selected: boolean
}) {
  return (
    <div className={cn("flex size-full", positionAlignments[index])}>
      <div
        className={cn(
          "h-2 w-12 rounded-full",
          selected ? "bg-primary" : "bg-muted-foreground/40",
        )}
      />
    </div>
  )
}

function Position() {
  const { overlaySettings, onEvent, getOverlayPosition } = useSettings()
  const { positionIndex, isPositionLocked } = overlaySettings

  return (
    <CollapsibleSection title="POSITION">
      <div className="flex flex-col gap-5">
        <div className="grid grid-cols-3 gap-4">
          {positionLabels.map((label, i) => (
            <StyleCard
              key={label}
              label={label}
              isSelected={positionIndex === i}
              onClick={() =>
                onEvent({ type: "OverlayPositionIndexSelect", index: i })
              }
            >
              <PositionMarker index={i} selected={positionIndex === i} />
            </StyleCard>
          ))}
        </div>

        <Separator />

        <div className="flex flex-col gap-5">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <img
                src="/icons/drag_pan.svg"
                alt=""
                className="size-10 rounded-full border p-2.5"
              />
              <div>
                <p className="text-sm text-foreground">Use custom position</p>
                <p className="text-xs text-muted-foreground">
                  Unlock to move around the overlay, lock it again to fix its
                  position.
                </p>
              </div>
            </div>
            <Switch
              checked={positionIndex === 6}
              onCheckedChange={(isEnabled) =>
                onEvent({ type: "OverlayCustomPositionEnable", isEnabled })
              }
            />
          </div>

          {positionIndex === 6 && (
            <div className="flex items-center justify-center gap-3 rounded-xl bg-muted/50 p-5">
              <span
                className={cn(
                  "text-sm font-medium",
                  isPositionLocked
                    ? "text-foreground"
                    : "text-muted-foreground",
                )}
              >
                Locked
              </span>
              <Switch
                checked={!isPositionLocked}
                onCheckedChange={(unlocked) => {
                  const pos = getOverlayPosition()
                  onEvent({
                    type: "OverlayCustomPositionSelect",
                    offset: pos,
                    isPositionLocked: !unlocked,
                  })
                }}
              />
              <span
                className={cn(
                  "text-sm font-medium",
                  !isPositionLocked
                    ? "text-foreground"
                    : "text-muted-foreground",
                )}
              >
                Unlocked
              </span>
            </div>
          )}
        </div>
      </div>
    </CollapsibleSection>
  )
}

function Orientation() {
  const { overlaySettings, onEvent } = useSettings()
  return (
    <CollapsibleSection title="ORIENTATION">
      <div className="grid grid-cols-2 gap-4">
        <StyleCard
          label="Horizontal"
          isSelected={overlaySettings.isHorizontal}
          onClick={() =>
            onEvent({ type: "OverlayOrientationSelect", isHorizontal: true })
          }
          className="h-52"
        >
          <img src="/icons/horizontal.png" alt="Horizontal" className="max-h-full" />
        </StyleCard>
        <StyleCard
          label="Vertical"
          isSelected={!overlaySettings.isHorizontal}
          onClick={() =>
            onEvent({ type: "OverlayOrientationSelect", isHorizontal: false })
          }
          className="h-52"
        >
          <img src="/icons/vertical.png" alt="Vertical" className="max-h-full" />
        </StyleCard>
      </div>
    </CollapsibleSection>
  )
}

function BrightnessIcons() {
  return (
    <div className="flex justify-between">
      <img src="/icons/no_brightness.svg" alt="" className="size-5 opacity-70" />
      <img src="/icons/mid_brightness.svg" alt="" className="size-5 opacity-70" />
      <img src="/icons/full_brightness.svg" alt="" className="size-5 opacity-70" />
    </div>
  )
}

function Opacity() {
  const { overlaySettings, onEvent } = useSettings()
  return (
    <CollapsibleSection title="OPACITY">
      <div className="flex flex-col gap-3">
        <Slider
          min={0}
          max={1}
          step={0.1}
          value={[overlaySettings.opacity]}
          onValueChange={([opacity]) =>
            onEvent({ type: "OverlayOpacityChange", opacity })
          }
        />
        <BrightnessIcons />
      </div>
    </CollapsibleSection>
  )
}

function Scale() {
  const { overlaySettings, onEvent } = useSettings()
  return (
    <CollapsibleSection title="SCALE">
      <div className="flex flex-col gap-3">
        <Slider
          min={0}
          max={1}
          step={1 / 6}
          value={[overlaySettings.scale]}
          onValueChange={([scale]) =>
            onEvent({ type: "OverlayScaleChange", scale })
          }
        />
        <BrightnessIcons />
      </div>
    </CollapsibleSection>
  )
}

function GraphType() {
  const { overlaySettings, onEvent } = useSettings()
  const { progressType } = overlaySettings
  return (
    <ToggleSection
      title="GRAPH"
      isEnabled={progressType !== ProgressType.None}
      onSwitchToggle={(enabled) =>
        onEvent({
          type: "OverlayGraphChange",
          progressType: enabled ? ProgressType.Circular : ProgressType.None,
        })
      }
    >
      <div className="grid grid-cols-2 gap-4">
        <StyleCard
          label="Ring graph"
          isSelected={progressType === ProgressType.Circular}
          onClick={() =>
            onEvent({
              type: "OverlayGraphChange",
              progressType: ProgressType.Circular,
            })
          }
          className="h-52"
        >
          <img src="/icons/rings.png" alt="Ring graph" className="max-h-full" />
        </StyleCard>
        <StyleCard
          label="Bar graph"
          isSelected={progressType === ProgressType.Bar}
          onClick={() =>
            onEvent({
              type: "OverlayGraphChange",
              progressType: ProgressType.Bar,
            })
          }
          className="h-52"
        >
          <img src="/icons/bars.png" alt="Bar graph" className="max-h-full" />
        </StyleCard>
      </div>
    </ToggleSection>
  )
}
