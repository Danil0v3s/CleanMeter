package br.com.firstsoft.core.common.hardwaremonitor

import br.com.firstsoft.core.common.hwinfo.SensorReadingType
import kotlinx.serialization.Serializable

@Serializable
data class HardwareMonitorData(
    val LastPollTime: Long,
    val Hardwares: List<Hardware>,
    val Sensors: List<Sensor>
) {
    @Serializable
    data class Hardware(
        val Name: String,
        val Identifier: String
    )

    @Serializable
    data class Sensor(
        val Name: String,
        val Identifier: String,
        val SensorType: Int,
        val Value: Float
    )

    enum class SensorType(val value: Int) {
        Voltage(0),
        Current(1),
        Power(2),
        Clock(3),
        Temperature(4),
        Load(5),
        Frequency(6),
        Fan(7),
        Flow(8),
        Control(9),
        Level(10),
        Factor(11),
        Data(12),
        SmallData(13),
        Throughput(14),
        TimeSpan(15),
        Energy(16),
        Noise(17);

        companion object {
            fun fromValue(value: Int): SensorType = entries.find { it.value == value } ?: Noise
        }
    }
}

fun HardwareMonitorData.gpuReadings() = readings("GPU")
fun HardwareMonitorData.cpuReadings() = readings("CPU")
fun HardwareMonitorData.readings(namePart: String): List<HardwareMonitorData.Sensor> {
    return Sensors.filter { it.Identifier.contains(namePart, true) || it.Name.contains(namePart, true) }
        .sortedBy { it.SensorType }
}
fun HardwareMonitorData.getReading(identifier: String) = Sensors.firstOrNull { it.Identifier == identifier }

val HardwareMonitorData.FPS: Int
    get() = (getReading("/presentmon/presented")?.Value?.toInt() ?: 0).coerceAtMost(480)

val HardwareMonitorData.Frametime: Float
    get() = (getReading("/presentmon/frametime")?.Value ?: 0f).coerceAtLeast(0f).coerceAtMost(99f)

val HardwareMonitorData.GpuTemp: Int
    get() = 1

val HardwareMonitorData.GpuTempUnit: String
    get() = ""

val HardwareMonitorData.GpuUsage: Int
    get() = 1

val HardwareMonitorData.VramUsage: Float
    get() = 1f

val HardwareMonitorData.VramUsagePercent: Float
    get() = 1f

val HardwareMonitorData.CpuUsage: Int
    get() = 1

val HardwareMonitorData.RamUsage: Float
    get() = 1f

val HardwareMonitorData.RamUsagePercent: Float
    get() = 1f

val HardwareMonitorData.UpRate: Float
    get() = 1f

val HardwareMonitorData.UpRateUnit: String
    get() = ""

val HardwareMonitorData.DlRate: Float
    get() = 1f

val HardwareMonitorData.DlRateUnit
    get() = ""
