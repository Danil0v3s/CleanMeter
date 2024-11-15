package br.com.firstsoft.core.common.hwinfo

import kotlinx.serialization.Serializable

@Serializable
data class SensorReadingElement(
    val readingType: SensorReadingType,
    val dwSensorIndex: Int,
    val dwReadingID: Int,
    val szLabelOrig: String,
    val szLabelUser: String,
    val szUnit: String,
    val value: Float,
    val valueMin: Float,
    val valueMax: Float,
    val valueAvg: Float,
)
