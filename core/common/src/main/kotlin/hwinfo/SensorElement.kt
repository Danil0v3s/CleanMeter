package hwinfo

import kotlinx.serialization.Serializable

@Serializable
data class SensorElement(
    val dwSensorId: Long,
    val dwSensorInst: Long,
    val szSensorNameOrig: String,
    val szSensorNameUser: String,
)