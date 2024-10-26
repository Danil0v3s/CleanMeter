package hwinfo

import kotlinx.serialization.Serializable

@Serializable
data class HwInfoData(
    val header: SensorSharedMem,
    val sensors: List<SensorElement>,
    val readings: List<SensorReadingElement>
)

val HwInfoData.FPS: Int
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Framerate (Presented)" }?.value?.toInt()
        ?: 0).coerceAtMost(480)

val HwInfoData.Frametime: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Frame Time" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(0f).coerceAtMost(99f)

val HwInfoData.GpuTemp: Int
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Temp && it.szLabelOrig == "GPU Temperature" }?.value?.toInt()
        ?: 0).coerceAtLeast(1)

val HwInfoData.GpuTempUnit: String
    get() = readings.firstOrNull { it.readingType == SensorReadingType.Temp && it.szLabelOrig == "GPU Temperature" }?.szUnit.orEmpty()

val HwInfoData.GpuUsage: Int
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Usage && it.szLabelOrig == "GPU Core Load" }?.value?.toInt()
        ?: 0).coerceAtLeast(1)

val HwInfoData.VramUsage: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "GPU Memory Allocated" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(1f)

val HwInfoData.VramUsagePercent: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Usage && it.szLabelOrig == "GPU Memory Usage" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(0f).coerceAtMost(100f)

val HwInfoData.CpuTemp: Int
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Temp && it.szLabelOrig == "CPU" }?.value?.toInt()
        ?: 0).coerceAtLeast(1)

val HwInfoData.CpuTempUnit: String
    get() = readings.firstOrNull { it.readingType == SensorReadingType.Temp && it.szLabelOrig == "CPU" }?.szUnit.orEmpty()

val HwInfoData.CpuUsage: Int
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Usage && it.szLabelOrig == "Total CPU Usage" }?.value?.toInt()
        ?: 0).coerceAtLeast(1)

val HwInfoData.RamUsage: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Physical Memory Used" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(1f)

val HwInfoData.RamUsagePercent: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Physical Memory Load" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(0f).coerceAtMost(100f)

val HwInfoData.UpRate: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Current UP rate" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(0f)

val HwInfoData.UpRateUnit
    get() = readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Current UP rate" }?.szUnit.orEmpty()

val HwInfoData.DlRate: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Current DL rate" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(0f)

val HwInfoData.DlRateUnit
    get() = readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Current DL rate" }?.szUnit.orEmpty()

val HwInfoData.UpTotal: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Total UP" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(0f)

val HwInfoData.DlTotal: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Total DL" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(0f)

val HwInfoData.UpRateReading
    get() = readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Current UP rate" }

