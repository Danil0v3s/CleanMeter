// Placeholder hardware data so the settings UI has something to render.
// The real feed comes from the native project later (same shape).

import {
  HardwareType,
  HwSensorType,
  type HardwareMonitorData,
  type HwHardware,
  type HwSensor,
} from "./hardwareMonitorData"

const hardwares: HwHardware[] = [
  { Name: "AMD Ryzen 7 5800X", Identifier: "/amdcpu/0", HardwareType: HardwareType.Cpu },
  { Name: "NVIDIA GeForce RTX 4070", Identifier: "/gpu-nvidia/0", HardwareType: HardwareType.GpuNvidia },
  { Name: "Realtek Gaming 2.5GbE", Identifier: "/nic/0", HardwareType: HardwareType.Network },
  { Name: "Generic Memory", Identifier: "/ram/0", HardwareType: HardwareType.Memory },
]

const sensor = (
  Name: string,
  Identifier: string,
  HardwareIdentifier: string,
  SensorType: HwSensorType,
  Value: number,
): HwSensor => ({ Name, Identifier, HardwareIdentifier, SensorType, Value })

const sensors: HwSensor[] = [
  sensor("CPU Package", "/amdcpu/0/temperature/2", "/amdcpu/0", HwSensorType.Temperature, 52),
  sensor("CPU Total", "/amdcpu/0/load/0", "/amdcpu/0", HwSensorType.Load, 23),
  sensor("Package Power", "/amdcpu/0/power/0", "/amdcpu/0", HwSensorType.Power, 65),
  sensor("GPU Core", "/gpu-nvidia/0/temperature/0", "/gpu-nvidia/0", HwSensorType.Temperature, 48),
  sensor("GPU Core", "/gpu-nvidia/0/load/0", "/gpu-nvidia/0", HwSensorType.Load, 41),
  sensor("GPU Memory", "/gpu-nvidia/0/load/3", "/gpu-nvidia/0", HwSensorType.Load, 33),
  sensor("GPU Power", "/gpu-nvidia/0/power/0", "/gpu-nvidia/0", HwSensorType.Power, 120),
  sensor("GPU Memory Used", "/gpu-nvidia/0/smalldata/2", "/gpu-nvidia/0", HwSensorType.SmallData, 4200),
  sensor("Upload Speed", "/nic/0/throughput/7", "/nic/0", HwSensorType.Throughput, 125000),
  sensor("Download Speed", "/nic/0/throughput/8", "/nic/0", HwSensorType.Throughput, 980000),
  sensor("Memory Used", "/ram/0/data/0", "/ram/0", HwSensorType.Data, 12.4),
  sensor("Memory Available", "/ram/0/data/1", "/ram/0", HwSensorType.Data, 19.6),
  sensor("Frametime", "/presentmon/frametime", "/presentmon/0", HwSensorType.TimeSpan, 6.94),
]

export const mockHardwareData = (): HardwareMonitorData => ({
  LastPollTime: 0,
  Hardwares: hardwares,
  Sensors: sensors,
  PresentMonApps: ["Cyberpunk2077.exe", "eldenring.exe", "cs2.exe"],
})
