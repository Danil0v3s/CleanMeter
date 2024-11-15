package br.com.firstsoft.target.server.ui.models

import kotlinx.serialization.Serializable

@Serializable
data class OverlaySettings(
    val isHorizontal: Boolean = true,
    val positionIndex: Int = 0,
    val selectedDisplayIndex: Int = 0,
    val fps: Boolean = true,
    val frametime: Boolean = false,
    val cpuTemp: Boolean = true,
    val cpuUsage: Boolean = true,
    val gpuTemp: Boolean = true,
    val gpuUsage: Boolean = true,
    val vramUsage: Boolean = false,
    val ramUsage: Boolean = false,
    val progressType: ProgressType = ProgressType.Circular,
    val positionX: Int = 0,
    val positionY: Int = 0,
    val isPositionLocked: Boolean = true,
    val upRate: Boolean = false,
    val downRate: Boolean = false,
    val netGraph: Boolean = false,
    val opacity: Float = 1f,
    val cpuTempReadingId: Int = 0,
    val cpuUsageReadingId: Int = 0,
    val gpuTempReadingId: Int = 0,
    val gpuUsageReadingId: Int = 0,
) {
    @Serializable
    enum class ProgressType {
        Circular, Bar, None
    }
}