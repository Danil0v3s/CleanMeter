package hwinfo

import kotlinx.serialization.Serializable

@Serializable
data class SensorElement(
    val dwSensorId: Int,
    val dwSensorInst: Int,
    val szSensorNameOrig: String,
    val szSensorNameUser: String,
)