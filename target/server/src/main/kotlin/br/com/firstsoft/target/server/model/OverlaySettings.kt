package br.com.firstsoft.target.server.model

import br.com.firstsoft.target.server.ui.settings.SensorType
import kotlinx.serialization.Serializable

@Serializable
data class OverlaySettings(
    val isHorizontal: Boolean = true,
    val positionIndex: Int = 0,
    val selectedDisplayIndex: Int = 0,
    val netGraph: Boolean = false,
    val progressType: ProgressType = ProgressType.Circular,
    val positionX: Int = 0,
    val positionY: Int = 0,
    val isPositionLocked: Boolean = true,
    val opacity: Float = 1f,
    val sensors: Sensors = Sensors(),
) {
    @Serializable
    enum class ProgressType {
        Circular, Bar, None
    }

    @Serializable
    data class Sensors(
        val framerate: Sensor.Framerate = Sensor.Framerate(),
        val frametime: Sensor.Frametime = Sensor.Frametime(),
        val cpuTemp: Sensor.CpuTemp = Sensor.CpuTemp(),
        val cpuUsage: Sensor.CpuUsage = Sensor.CpuUsage(),
        val gpuTemp: Sensor.GpuTemp = Sensor.GpuTemp(),
        val gpuUsage: Sensor.GpuUsage = Sensor.GpuUsage(),
        val vramUsage: Sensor.VramUsage = Sensor.VramUsage(),
        val totalVramUsed: Sensor.TotalVramUsed = Sensor.TotalVramUsed(),
        val ramUsage: Sensor.RamUsage = Sensor.RamUsage(),
        val upRate: Sensor.UpRate = Sensor.UpRate(),
        val downRate: Sensor.DownRate = Sensor.DownRate(),
    )

    @Serializable
    sealed class Sensor(val sensorType: SensorType) {
        abstract val isEnabled: Boolean

        @Serializable
        data class Framerate(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.Framerate)
        @Serializable
        data class Frametime(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.Frametime)
        @Serializable
        data class CpuTemp(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.CpuTemp)
        @Serializable
        data class CpuUsage(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.CpuUsage)
        @Serializable
        data class GpuTemp(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.GpuTemp)
        @Serializable
        data class GpuUsage(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.GpuUsage)
        @Serializable
        data class VramUsage(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.VramUsage)
        @Serializable
        data class TotalVramUsed(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.TotalVramUsed)
        @Serializable
        data class RamUsage(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.RamUsage)
        @Serializable
        data class UpRate(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.UpRate)
        @Serializable
        data class DownRate(override val isEnabled: Boolean = true, val customReadingId: String = "") : Sensor(SensorType.DownRate)
    }
}