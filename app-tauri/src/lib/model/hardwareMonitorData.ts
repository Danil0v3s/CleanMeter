// Mirror of app/core/common/.../hardwaremonitor/HardwareMonitorData.kt

export enum HardwareType {
  Motherboard = 0,
  SuperIO = 1,
  Cpu = 2,
  Memory = 3,
  GpuNvidia = 4,
  GpuAmd = 5,
  GpuIntel = 6,
  Storage = 7,
  Network = 8,
  Cooler = 9,
  EmbeddedController = 10,
  Psu = 11,
  Battery = 12,
  Unknown = 13,
}

// HardwareMonitorData.SensorType in Kotlin — renamed to avoid clashing with the
// UI-level SensorType enum used by the settings.
export enum HwSensorType {
  Voltage = 0,
  Current = 1,
  Power = 2,
  Clock = 3,
  Temperature = 4,
  Load = 5,
  Frequency = 6,
  Fan = 7,
  Flow = 8,
  Control = 9,
  Level = 10,
  Factor = 11,
  Data = 12,
  SmallData = 13,
  Throughput = 14,
  TimeSpan = 15,
  Energy = 16,
  Noise = 17,
  Unknown = 18,
}

export interface HwHardware {
  Name: string
  Identifier: string
  HardwareType: HardwareType
}

export interface HwSensor {
  Name: string
  Identifier: string
  HardwareIdentifier: string
  SensorType: HwSensorType
  Value: number
}

export interface HardwareMonitorData {
  LastPollTime: number
  Hardwares: HwHardware[]
  Sensors: HwSensor[]
  PresentMonApps: string[]
}

// --- helpers (mirror the Kotlin extension functions) ---

export function readings(data: HardwareMonitorData, namePart: string): HwSensor[] {
  const part = namePart.toLowerCase()
  return data.Sensors.filter(
    (s) =>
      s.Identifier.toLowerCase().includes(part) ||
      s.Name.toLowerCase().includes(part),
  ).sort((a, b) => a.SensorType - b.SensorType)
}

export const gpuReadings = (data: HardwareMonitorData) => readings(data, "GPU")
export const cpuReadings = (data: HardwareMonitorData) => readings(data, "CPU")
export const networkReadings = (data: HardwareMonitorData) => readings(data, "/nic/")

export function getReading(
  data: HardwareMonitorData,
  identifier: string,
): HwSensor | undefined {
  return data.Sensors.find((s) => s.Identifier === identifier)
}

export function fps(data: HardwareMonitorData): number {
  const frametime = getReading(data, "/presentmon/frametime")?.Value ?? 1
  return Math.trunc(1000 / frametime)
}

export function frametime(data: HardwareMonitorData): number {
  return Math.max(0, getReading(data, "/presentmon/frametime")?.Value ?? 0)
}

export function ramUsage(data: HardwareMonitorData): number {
  return Math.max(
    1,
    data.Sensors.find((s) => s.Name === "Memory Used")?.Value ?? 1,
  )
}

export function ramUsagePercent(data: HardwareMonitorData): number {
  const used = ramUsage(data)
  const available = Math.max(
    1,
    data.Sensors.find((s) => s.Name === "Memory Available")?.Value ?? 1,
  )
  return used / (used + available)
}
