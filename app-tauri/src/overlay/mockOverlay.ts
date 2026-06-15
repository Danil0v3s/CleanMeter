// Self-contained mock feed for the overlay so it renders with live-looking
// graphs. The native project replaces this with the real data later.

import {
  HwSensorType,
  type HardwareMonitorData,
  type HwSensor,
} from "@/lib/model/hardwareMonitorData"
import {
  defaultOverlaySettings,
  ProgressType,
  type OverlaySettings,
} from "@/lib/model/overlaySettings"

// Stable identifiers shared between the mock settings and the mock readings.
const ID = {
  cpuTemp: "/mock/cpu/temp",
  cpuLoad: "/mock/cpu/load",
  cpuPower: "/mock/cpu/power",
  gpuTemp: "/mock/gpu/temp",
  gpuLoad: "/mock/gpu/load",
  vram: "/mock/gpu/vram",
  vramTotal: "/mock/gpu/vramused",
  gpuPower: "/mock/gpu/power",
  up: "/mock/net/up",
  down: "/mock/net/down",
  frametime: "/presentmon/frametime",
}

export function mockOverlaySettings(): OverlaySettings {
  const base = defaultOverlaySettings()
  base.progressType = ProgressType.Circular
  base.netGraph = true
  const s = base.sensors
  s.cpuTemp.customReadingId = ID.cpuTemp
  s.cpuUsage.customReadingId = ID.cpuLoad
  s.cpuConsumption.customReadingId = ID.cpuPower
  s.gpuTemp.customReadingId = ID.gpuTemp
  s.gpuUsage.customReadingId = ID.gpuLoad
  s.vramUsage.customReadingId = ID.vram
  s.totalVramUsed.customReadingId = ID.vramTotal
  s.gpuConsumption.customReadingId = ID.gpuPower
  s.upRate.customReadingId = ID.up
  s.downRate.customReadingId = ID.down
  return base
}

const sensor = (
  Name: string,
  Identifier: string,
  SensorType: HwSensorType,
  Value: number,
): HwSensor => ({
  Name,
  Identifier,
  HardwareIdentifier: "/mock/0",
  SensorType,
  Value,
})

// tick drives small variations so the frametime/net graphs animate.
export function mockOverlayData(tick: number): HardwareMonitorData {
  const wave = (amp: number, phase = 0) =>
    Math.sin(tick / 4 + phase) * amp + Math.sin(tick / 1.7 + phase) * (amp / 3)
  const frametime = 6.9 + wave(1.6)
  const cpuLoad = 24 + wave(12)
  const gpuLoad = 42 + wave(16, 1)
  const up = 2.2 + wave(1.4, 2)
  const down = 5.1 + wave(2.6, 0.5)

  return {
    LastPollTime: tick,
    Hardwares: [],
    PresentMonApps: [],
    Sensors: [
      sensor("CPU Package", ID.cpuTemp, HwSensorType.Temperature, 52),
      sensor("CPU Total", ID.cpuLoad, HwSensorType.Load, Math.max(1, cpuLoad)),
      sensor("CPU Power", ID.cpuPower, HwSensorType.Power, 65),
      sensor("GPU Core", ID.gpuTemp, HwSensorType.Temperature, 48),
      sensor("GPU Core", ID.gpuLoad, HwSensorType.Load, Math.max(1, gpuLoad)),
      sensor("GPU Memory", ID.vram, HwSensorType.Load, 33),
      sensor("GPU Memory Used", ID.vramTotal, HwSensorType.SmallData, 4200),
      sensor("GPU Power", ID.gpuPower, HwSensorType.Power, 120),
      sensor("Upload", ID.up, HwSensorType.Throughput, Math.max(0, up)),
      sensor("Download", ID.down, HwSensorType.Throughput, Math.max(0, down)),
      sensor("Frametime", ID.frametime, HwSensorType.TimeSpan, frametime),
      sensor("Memory Used", "/mock/ram/used", HwSensorType.Data, 12.4),
      sensor("Memory Available", "/mock/ram/avail", HwSensorType.Data, 19.6),
    ],
  }
}
