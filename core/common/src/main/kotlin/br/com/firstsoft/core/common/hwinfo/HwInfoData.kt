package br.com.firstsoft.core.common.hwinfo

import kotlinx.serialization.Serializable

@Serializable
data class HwInfoData(
    val header: SensorSharedMem,
    val sensors: List<SensorElement>,
    val readings: List<SensorReadingElement>
)

private fun HwInfoData.readings(namePart: String): List<SensorReadingElement> {
    val indexes = sensors.mapIndexed { index, sensorElement -> if (sensorElement.szSensorNameOrig.contains(namePart)) index else null }.filterNotNull()
    return readings.filter { indexes.contains(it.dwSensorIndex) }
}

fun HwInfoData.cpuReadings() = readings("CPU")
fun HwInfoData.gpuReadings() = readings("")
fun HwInfoData.presentMonReadings() = readings("PresentMon")
fun HwInfoData.getReading(readingId: Int, sensorIndex: Int) = readings.firstOrNull { it.dwReadingID == readingId && it.dwSensorIndex == sensorIndex }

val HwInfoData.Frametime: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "Frame Time" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(0f).coerceAtMost(99f)

val HwInfoData.VramUsage: Float
    get() = (readings.firstOrNull { it.readingType == SensorReadingType.Other && it.szLabelOrig == "GPU Memory Allocated" }?.value?.toFloat()
        ?: 0f).coerceAtLeast(1f)

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

