package hwinfo

import kotlinx.serialization.Serializable

@Serializable
data class SensorReadingElement(
    val tReading: SensorReadingType,
    val dwSensorIndex: Int,
    val dwReadingID: Int,
    val szLabelOrig: String,
    val szLabelUser: String,
    val szUnit: String,
    val value: Double,
    val valueMin: Double,
    val valueMax: Double,
    val valueAvg: Double,
)
