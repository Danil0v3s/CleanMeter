import * as React from "react"

import { useSettings } from "@/contexts/SettingsContext"
import type { HwSensor } from "@/lib/model/hardwareMonitorData"
import {
  type CheckboxSectionOption,
  SectionType,
  SensorType,
} from "@/lib/model/settings"
import {
  CheckboxSection,
  CustomBodyCheckboxSection,
} from "@/settings/components/Section"
import { CheckboxWithLabel, KeyboardShortcutInfoLabel } from "@/settings/components/primitives"
import { DropdownMenu } from "@/settings/components/DropdownMenu"
import { SensorReadingDropdownMenu } from "@/settings/components/SensorReadingDropdownMenu"
import { SensorBoundaryInput } from "@/settings/components/SensorBoundaryInput"
import { checkboxSectionOptions, filterOptions } from "./statsOptions"

export function StatsTab() {
  const { overlaySettings } = useSettings()
  const availableOptions = React.useMemo(
    () => checkboxSectionOptions(overlaySettings),
    [overlaySettings],
  )

  return (
    <div className="flex flex-col gap-4 pb-2 pt-5">
      <KeyboardShortcutInfoLabel />
      <FpsStats options={availableOptions} />
      <GpuStats options={availableOptions} />
      <CpuStats options={availableOptions} />
      <RamStats options={availableOptions} />
      <NetworkStats options={availableOptions} />
      <p className="text-right text-xs font-medium text-muted-foreground">
        May your frames be high, and temps be low.
      </p>
    </div>
  )
}

function FpsStats({ options }: { options: CheckboxSectionOption[] }) {
  const { onEvent, overlaySettings, getPresentMonApps } = useSettings()
  const presentMonApps = getPresentMonApps()
  const fpsOptions = filterOptions(
    options,
    SensorType.Framerate,
    SensorType.Frametime,
  )

  return (
    <CustomBodyCheckboxSection
      title="FPS"
      options={fpsOptions}
      onSwitchToggle={(isEnabled) =>
        onEvent({ type: "SwitchToggle", section: SectionType.Fps, isEnabled })
      }
      body={(opts) => (
        <div className="flex flex-col gap-3">
          {opts.map((option) => (
            <CheckboxWithLabel
              key={option.type}
              label={option.name}
              enabled={option.useCheckbox}
              checked={option.isSelected}
              onCheckedChange={() =>
                onEvent({
                  type: "OptionsToggle",
                  data: { ...option, isSelected: !option.isSelected },
                })
              }
            />
          ))}
          {presentMonApps.length > 0 && (
            <DropdownMenu
              className="pt-2"
              label="Monitored app:"
              disclaimer="Apps are auto updated every 10 seconds."
              options={presentMonApps}
              selectedIndex={Math.max(
                0,
                presentMonApps.indexOf(overlaySettings.currentPresentMonApp),
              )}
              onValueChanged={(i) =>
                onEvent({
                  type: "FpsApplicationSelect",
                  applicationName: presentMonApps[i],
                })
              }
            />
          )}
        </div>
      )}
    />
  )
}

/** Shared body for sections with per-option custom sensor + boundaries. */
function SensorOptionList({
  options,
  getReadings,
  withBoundaries,
  dropdownLabel,
}: {
  options: CheckboxSectionOption[]
  getReadings: (option: CheckboxSectionOption) => HwSensor[]
  withBoundaries: boolean
  dropdownLabel?: (sensor: HwSensor) => string
}) {
  const { onEvent, getSensor } = useSettings()
  return (
    <div className="flex flex-col gap-3">
      {options.map((option) => {
        const readings = getReadings(option)
        const showCustom =
          readings.length > 0 && option.isSelected && option.useCustomSensor
        return (
          <div key={option.type} className="flex w-full flex-col">
            <CheckboxWithLabel
              label={option.name}
              enabled={option.useCheckbox}
              checked={option.isSelected}
              onCheckedChange={() =>
                onEvent({
                  type: "OptionsToggle",
                  data: { ...option, isSelected: !option.isSelected },
                })
              }
            />
            {showCustom && (
              <>
                <SensorReadingDropdownMenu
                  options={readings}
                  selectedIndex={readings.findIndex(
                    (r) => r.Identifier === option.optionReadingId,
                  )}
                  onValueChanged={(s) =>
                    onEvent({
                      type: "CustomSensorSelect",
                      sensor: option.type,
                      sensorId: s.Identifier,
                    })
                  }
                  label="Sensor:"
                  sensorName={option.name}
                  dropdownLabel={dropdownLabel}
                />
                {withBoundaries && (
                  <SensorBoundaryInput
                    sensor={getSensor(option.type)}
                    sensorType={option.type}
                    onBoundaryChange={(sensorType, boundaries) =>
                      onEvent({ type: "BoundarySet", sensorType, boundaries })
                    }
                  />
                )}
              </>
            )}
          </div>
        )
      })}
    </div>
  )
}

function GpuStats({ options }: { options: CheckboxSectionOption[] }) {
  const { onEvent, getGpuSensorReadings } = useSettings()
  return (
    <CustomBodyCheckboxSection
      title="GPU"
      options={filterOptions(
        options,
        SensorType.GpuUsage,
        SensorType.GpuTemp,
        SensorType.VramUsage,
        SensorType.TotalVramUsed,
        SensorType.GpuConsumption,
      )}
      onSwitchToggle={(isEnabled) =>
        onEvent({ type: "SwitchToggle", section: SectionType.Gpu, isEnabled })
      }
      body={(opts) => (
        <SensorOptionList
          options={opts}
          withBoundaries
          getReadings={(option) => {
            const all = getGpuSensorReadings()
            const filtered = all.filter((r) => r.SensorType === option.dataType)
            return filtered.length > 0 ? filtered : all
          }}
        />
      )}
    />
  )
}

function CpuStats({ options }: { options: CheckboxSectionOption[] }) {
  const { onEvent, getCpuSensorReadings } = useSettings()
  return (
    <CustomBodyCheckboxSection
      title="CPU"
      options={filterOptions(
        options,
        SensorType.CpuUsage,
        SensorType.CpuTemp,
        SensorType.CpuConsumption,
      )}
      onSwitchToggle={(isEnabled) =>
        onEvent({ type: "SwitchToggle", section: SectionType.Cpu, isEnabled })
      }
      body={(opts) => (
        <SensorOptionList
          options={opts}
          withBoundaries
          getReadings={(option) => {
            const all = getCpuSensorReadings()
            const filtered = all.filter((r) => r.SensorType === option.dataType)
            return filtered.length > 0 ? filtered : all
          }}
        />
      )}
    />
  )
}

function RamStats({ options }: { options: CheckboxSectionOption[] }) {
  const { onEvent } = useSettings()
  return (
    <CheckboxSection
      title="RAM"
      options={filterOptions(options, SensorType.RamUsage)}
      onOptionToggle={(data) => onEvent({ type: "OptionsToggle", data })}
      onSwitchToggle={(isEnabled) =>
        onEvent({ type: "SwitchToggle", section: SectionType.Ram, isEnabled })
      }
    />
  )
}

function NetworkStats({ options }: { options: CheckboxSectionOption[] }) {
  const { onEvent, getNetworkSensorReadings, getHardwareSensors } = useSettings()
  return (
    <CustomBodyCheckboxSection
      title="NETWORK"
      options={filterOptions(
        options,
        SensorType.DownRate,
        SensorType.UpRate,
        SensorType.NetGraph,
      )}
      onSwitchToggle={(isEnabled) =>
        onEvent({
          type: "SwitchToggle",
          section: SectionType.Network,
          isEnabled,
        })
      }
      body={(opts) => (
        <SensorOptionList
          options={opts}
          withBoundaries={false}
          dropdownLabel={(s) => {
            const hw = getHardwareSensors().find(
              (h) => h.Identifier === s.HardwareIdentifier,
            )
            return `${hw?.Name}: ${s.Name} (${s.Value})`
          }}
          getReadings={(option) => {
            const all = getNetworkSensorReadings()
              .slice()
              .sort((a, b) =>
                a.HardwareIdentifier.localeCompare(b.HardwareIdentifier),
              )
            const filtered = all.filter((r) => r.SensorType === option.dataType)
            return filtered.length > 0 ? filtered : all
          }}
        />
      )}
    />
  )
}
