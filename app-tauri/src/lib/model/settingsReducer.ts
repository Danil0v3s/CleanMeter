// Pure state transitions mirroring SettingsViewModel.on* handlers.

import type { Boundaries, OverlaySettings, Sensors } from "./overlaySettings"
import {
  type CheckboxSectionOption,
  type SettingsEvent,
  type SettingsState,
  SectionType,
  SensorType,
} from "./settings"

type SensorKey = keyof Sensors

function patchSensor(
  settings: OverlaySettings,
  key: SensorKey,
  patch: Partial<OverlaySettings["sensors"][SensorKey]>,
): OverlaySettings {
  return {
    ...settings,
    sensors: {
      ...settings.sensors,
      [key]: { ...settings.sensors[key], ...patch },
    },
  }
}

function patchSensors(
  settings: OverlaySettings,
  patches: Partial<Record<SensorKey, Partial<Sensors[SensorKey]>>>,
): OverlaySettings {
  const sensors = { ...settings.sensors }
  for (const k of Object.keys(patches) as SensorKey[]) {
    sensors[k] = { ...sensors[k], ...patches[k] }
  }
  return { ...settings, sensors }
}

// Maps a UI SensorType to the matching settings sensor key (where 1:1).
const sensorKeyByType: Partial<Record<SensorType, SensorKey>> = {
  [SensorType.Framerate]: "framerate",
  [SensorType.Frametime]: "frametime",
  [SensorType.CpuTemp]: "cpuTemp",
  [SensorType.CpuUsage]: "cpuUsage",
  [SensorType.CpuConsumption]: "cpuConsumption",
  [SensorType.GpuTemp]: "gpuTemp",
  [SensorType.GpuUsage]: "gpuUsage",
  [SensorType.VramUsage]: "vramUsage",
  [SensorType.TotalVramUsed]: "totalVramUsed",
  [SensorType.GpuConsumption]: "gpuConsumption",
  [SensorType.RamUsage]: "ramUsage",
  [SensorType.UpRate]: "upRate",
  [SensorType.DownRate]: "downRate",
}

function onOptionsToggle(
  settings: OverlaySettings,
  option: CheckboxSectionOption,
): OverlaySettings {
  if (option.type === SensorType.NetGraph) {
    return { ...settings, netGraph: option.isSelected }
  }
  if (option.type === SensorType.TotalVramUsed) {
    return settings // can't disable total vram used
  }
  const key = sensorKeyByType[option.type]
  if (!key) return settings
  return patchSensor(settings, key, { isEnabled: option.isSelected })
}

function onSwitchToggle(
  settings: OverlaySettings,
  section: SectionType,
  isEnabled: boolean,
): OverlaySettings {
  switch (section) {
    case SectionType.Fps:
      return patchSensors(settings, {
        framerate: { isEnabled },
        frametime: { isEnabled },
      })
    case SectionType.Gpu:
      return patchSensors(settings, {
        gpuTemp: { isEnabled },
        gpuUsage: { isEnabled },
        vramUsage: { isEnabled },
        gpuConsumption: { isEnabled },
      })
    case SectionType.Cpu:
      return patchSensors(settings, {
        cpuTemp: { isEnabled },
        cpuUsage: { isEnabled },
        cpuConsumption: { isEnabled },
      })
    case SectionType.Ram:
      return patchSensor(settings, "ramUsage", { isEnabled })
    case SectionType.Network:
      return {
        ...patchSensors(settings, {
          upRate: { isEnabled },
          downRate: { isEnabled },
        }),
        netGraph: isEnabled,
      }
  }
}

function onCustomSensorSelect(
  settings: OverlaySettings,
  sensor: SensorType,
  sensorId: string,
): OverlaySettings {
  // Framerate / Frametime / RamUsage / NetGraph have no custom reading.
  const key = sensorKeyByType[sensor]
  if (
    !key ||
    sensor === SensorType.Framerate ||
    sensor === SensorType.Frametime ||
    sensor === SensorType.RamUsage ||
    sensor === SensorType.NetGraph
  ) {
    return settings
  }
  return patchSensor(settings, key, { customReadingId: sensorId })
}

function onBoundarySet(
  settings: OverlaySettings,
  sensorType: SensorType,
  boundaries: Boundaries,
): OverlaySettings {
  const key = sensorKeyByType[sensorType]
  if (!key) return settings
  // Only GraphSensors carry boundaries.
  if (settings.sensors[key].boundaries === undefined) return settings
  return patchSensor(settings, key, { boundaries })
}

/** Reduce overlay settings for events that only touch overlaySettings. */
function reduceOverlay(
  settings: OverlaySettings,
  event: SettingsEvent,
): OverlaySettings {
  switch (event.type) {
    case "OptionsToggle":
      return onOptionsToggle(settings, event.data)
    case "SwitchToggle":
      return onSwitchToggle(settings, event.section, event.isEnabled)
    case "CustomSensorSelect":
      return onCustomSensorSelect(settings, event.sensor, event.sensorId)
    case "BoundarySet":
      return onBoundarySet(settings, event.sensorType, event.boundaries)
    case "DisplaySelect":
      return { ...settings, selectedDisplayIndex: event.displayIndex }
    case "OverlayPositionIndexSelect":
      return { ...settings, positionIndex: event.index }
    case "OverlayCustomPositionSelect":
      return {
        ...settings,
        positionX: event.offset.x,
        positionY: event.offset.y,
        isPositionLocked: event.isPositionLocked,
      }
    case "OverlayCustomPositionEnable":
      return {
        ...settings,
        positionIndex: event.isEnabled ? 6 : 0,
        isPositionLocked: true,
      }
    case "OverlayOrientationSelect":
      return { ...settings, isHorizontal: event.isHorizontal }
    case "OverlayOpacityChange":
      return { ...settings, opacity: event.opacity }
    case "OverlayScaleChange":
      return { ...settings, scale: event.scale }
    case "OverlayGraphChange":
      return { ...settings, progressType: event.progressType }
    case "DarkThemeToggle":
      return { ...settings, isDarkTheme: event.isEnabled }
    case "FpsApplicationSelect":
      return { ...settings, currentPresentMonApp: event.applicationName }
    case "PollingRateSelect":
      return { ...settings, pollingRate: event.pollingRate }
    case "ToggleLoggingEnabled":
      return { ...settings, isLoggingEnabled: !settings.isLoggingEnabled }
    default:
      return settings
  }
}

/** Top-level reducer mirroring SettingsViewModel.onEvent. */
export function reduceSettings(
  state: SettingsState,
  event: SettingsEvent,
): SettingsState {
  if (event.type === "ConsentGiven") {
    return { ...state, adminConsent: true }
  }
  if (state.overlaySettings == null) return state
  return {
    ...state,
    overlaySettings: reduceOverlay(state.overlaySettings, event),
  }
}
