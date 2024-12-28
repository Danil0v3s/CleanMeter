package app.cleanmeter.core.common.hardwaremonitor

import kotlinx.serialization.Serializable

@Serializable
data class HardwareMonitorData(
    val LastPollTime: Long,
    val Hardwares: List<Hardware>,
    val Sensors: List<Sensor>,
    val PresentMonApps: List<String>,
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

    enum class HardwareType(val value: Int) {
        Motherboard(0),
        SuperIO(1),
        Cpu(2),
        Memory(3),
        GpuNvidia(4),
        GpuAmd(5),
        GpuIntel(6),
        Storage(7),
        Network(8),
        Cooler(9),
        EmbeddedController(10),
        Psu(11),
        Battery(12),
        Unknown(13);

        companion object {
            fun fromValue(value: Int): HardwareType = entries.firstOrNull { it.value == value } ?: Unknown
        }
    }

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
        Noise(17),
        Unknown(18)
        ;

        companion object {
            fun fromValue(value: Int): SensorType = entries.firstOrNull { it.value == value } ?: Unknown
        }
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
fun HardwareMonitorData.getReading(identifier: String, namePart: String) = Sensors.firstOrNull { it.Identifier == identifier && it.Name.contains(namePart, true) }

val HardwareMonitorData.FPS: Int
    get() = (1000f / (getReading("/presentmon/frametime")?.Value ?: 1f)).toInt().coerceAtMost(480)

val HardwareMonitorData.Frametime: Float
    get() = (getReading("/presentmon/frametime")?.Value ?: 0f).coerceAtLeast(0f).coerceAtMost(99f)

val HardwareMonitorData.RamUsage: Float
    get() = Sensors.firstOrNull { it.Name == "Memory Used" }?.Value?.coerceAtLeast(1f) ?: 1f

val HardwareMonitorData.RamUsagePercent: Float
    get() = RamUsage / (RamUsage + (Sensors.firstOrNull { it.Name == "Memory Available" }?.Value?.coerceAtLeast(1f) ?: 1f))