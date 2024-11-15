package br.com.firstsoft.core.common.hwinfo

import kotlinx.serialization.Serializable

@Serializable
data class SensorSharedMem(
    val dwSignature: Int,
    val dwVersion: Int,
    val dwRevision: Int,
    val pollTime: Long,
    val dwOffsetOfSensorSection: Int,
    val dwSizeOfSensorElement: Int,
    val dwNumSensorElements: Int,
    val dwOffsetOfReadingSection: Int,
    val dwSizeOfReadingElement: Int,
    val dwNumReadingElements: Int,
)