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
        val Identifier: String,
        val HardwareType: HardwareType
    )

    @Serializable
    data class Sensor(
        val Name: String,
        val Identifier: String,
        val HardwareIdentifier: String,
        val SensorType: SensorType,
        val Value: Float
    )

    enum class HardwareType {
        Motherboard,
        SuperIO,
        Cpu,
        Memory,
        GpuNvidia,
        GpuAmd,
        GpuIntel,
        Storage,
        Network,
        Cooler,
        EmbeddedController,
        Psu,
        Battery;
    }

    enum class SensorType {
        Voltage,
        Current,
        Power,
        Clock,
        Temperature,
        Load,
        Frequency,
        Fan,
        Flow,
        Control,
        Level,
        Factor,
        Data,
        SmallData,
        Throughput,
        TimeSpan,
        Energy,
        Noise;
    }
}

fun HardwareMonitorData.gpuReadings() = readings("GPU")
fun HardwareMonitorData.cpuReadings() = readings("CPU")
fun HardwareMonitorData.networkReadings() = readings("/nic/")
fun HardwareMonitorData.readings(namePart: String): List<HardwareMonitorData.Sensor> {
    return Sensors.filter { it.Identifier.contains(namePart, true) || it.Name.contains(namePart, true) }
        .sortedBy { it.SensorType }
}
fun HardwareMonitorData.getReading(identifier: String) = Sensors.firstOrNull { it.Identifier == identifier }

val HardwareMonitorData.FPS: Int
    get() = (getReading("/presentmon/presented")?.Value?.toInt() ?: 0).coerceAtMost(480)

val HardwareMonitorData.Frametime: Float
    get() = (getReading("/presentmon/frametime")?.Value ?: 0f).coerceAtLeast(0f).coerceAtMost(99f)

val HardwareMonitorData.RamUsage: Float
    get() = Sensors.firstOrNull { it.Name == "Memory Used" }?.Value?.coerceAtLeast(1f) ?: 1f

val HardwareMonitorData.RamUsagePercent: Float
    get() = RamUsage / (RamUsage + (Sensors.firstOrNull { it.Name == "Memory Available" }?.Value?.coerceAtLeast(1f) ?: 1f))