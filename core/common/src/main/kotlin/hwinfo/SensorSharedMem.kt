package hwinfo

import kotlinx.serialization.Serializable

@Serializable
data class SensorSharedMem(
    val dwSignature: Int,
    val dwVersion: Int,
    val dwRevision: Int,
    val pollTime: Int,
    val dwOffsetOfSensorSection: Int,
    val dwSizeOfSensorElement: Int,
    val dwNumSensorElements: Int,
    val dwOffsetOfReadingSection: Int,
    val dwdSizeOfReadingElement: Int,
    val dwNumReadingElements: Int,
)