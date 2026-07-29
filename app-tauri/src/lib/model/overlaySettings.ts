// Mirror of app/target/desktop/.../model/OverlaySettings.kt

export enum ProgressType {
  Circular = "Circular",
  Bar = "Bar",
  None = "None",
}

export interface Boundaries {
  low: number
  medium: number
  high: number
}

export const defaultBoundaries = (): Boundaries => ({
  low: 60,
  medium: 80,
  high: 90,
})

export interface Sensor {
  isEnabled: boolean
  customReadingId: string
  // Present only on GraphSensors (cpuTemp, cpuUsage, gpuTemp, gpuUsage, vramUsage, ramUsage).
  boundaries?: Boundaries
}

export interface Sensors {
  framerate: Sensor
  frametime: Sensor
  cpuTemp: Sensor
  cpuUsage: Sensor
  cpuConsumption: Sensor
  gpuTemp: Sensor
  gpuUsage: Sensor
  vramUsage: Sensor
  gpuConsumption: Sensor
  totalVramUsed: Sensor
  ramUsage: Sensor
  upRate: Sensor
  downRate: Sensor
}

export interface OverlaySettings {
  isDarkTheme: boolean
  isHorizontal: boolean
  positionIndex: number
  selectedDisplayIndex: number
  netGraph: boolean
  progressType: ProgressType
  positionX: number
  positionY: number
  isPositionLocked: boolean
  opacity: number
  scale: number
  pollingRate: number
  isLoggingEnabled: boolean
  sensors: Sensors
  currentPresentMonApp: string
}

/** A sensor is "valid" (renderable in the overlay) when enabled with a chosen reading. */
export const sensorIsValid = (sensor: Sensor): boolean =>
  sensor.isEnabled && sensor.customReadingId.trim().length > 0

/** Whether this sensor type carries low/medium/high boundaries (GraphSensor). */
export const isGraphSensor = (sensor: Sensor): boolean =>
  sensor.boundaries !== undefined

const graphSensor = (): Sensor => ({
  isEnabled: true,
  customReadingId: "",
  boundaries: defaultBoundaries(),
})

const plainSensor = (): Sensor => ({ isEnabled: true, customReadingId: "" })

export const defaultSensors = (): Sensors => ({
  framerate: plainSensor(),
  frametime: plainSensor(),
  cpuTemp: graphSensor(),
  cpuUsage: graphSensor(),
  cpuConsumption: plainSensor(),
  gpuTemp: graphSensor(),
  gpuUsage: graphSensor(),
  vramUsage: graphSensor(),
  gpuConsumption: plainSensor(),
  totalVramUsed: plainSensor(),
  ramUsage: graphSensor(),
  upRate: plainSensor(),
  downRate: plainSensor(),
})

export const defaultOverlaySettings = (): OverlaySettings => ({
  isDarkTheme: false,
  isHorizontal: true,
  positionIndex: 0,
  selectedDisplayIndex: 0,
  netGraph: false,
  progressType: ProgressType.Circular,
  positionX: 0,
  positionY: 0,
  isPositionLocked: true,
  opacity: 1,
  scale: 1,
  pollingRate: 500,
  isLoggingEnabled: false,
  sensors: defaultSensors(),
  currentPresentMonApp: "",
})

/**
 * Merges persisted (possibly partial or older-schema) settings over the
 * defaults so missing keys fall back gracefully — the equivalent of Kotlin's
 * `ignoreUnknownKeys` + data-class defaults.
 */
export function mergeOverlaySettings(
  loaded: Partial<OverlaySettings> | null | undefined,
): OverlaySettings {
  const defaults = defaultOverlaySettings()
  if (!loaded || typeof loaded !== "object") return defaults

  const loadedSensors = (loaded.sensors ?? {}) as Partial<Sensors>
  const sensors = {} as Sensors
  for (const key of Object.keys(defaults.sensors) as (keyof Sensors)[]) {
    sensors[key] = { ...defaults.sensors[key], ...(loadedSensors[key] ?? {}) }
  }

  return { ...defaults, ...loaded, sensors }
}
