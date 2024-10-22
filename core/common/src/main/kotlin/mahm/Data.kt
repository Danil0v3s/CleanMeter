package mahm

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val header: Header,
    val entries: List<Entry>,
    val gpuEntries: List<GPUEntry>
)

val Data.FPS: Int
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_FRAMERATE }?.data?.toInt()
        ?: 0).coerceAtMost(480)

val Data.Frametime: Float
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_FRAMETIME }?.data
        ?: 0f).coerceAtLeast(0f).coerceAtMost(99f)

val Data.GpuTemp: Int
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_GPU_TEMPERATURE }?.data?.toInt()
        ?: 0).coerceAtLeast(1)

val Data.GpuTempUnit: String
    get() = entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_GPU_TEMPERATURE }?.szLocalisedSrcUnits ?: "c"

val Data.GpuUsage: Int
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_GPU_USAGE }?.data?.toInt()
        ?: 0).coerceAtLeast(1)

val Data.VramUsagePercent: Float
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_PLUGIN_MISC && it.szSrcName == "GPU Memory Usage" }?.data
        ?: 0f).coerceAtLeast(1f)

val Data.VramUsage: Float
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_MEMORY_USAGE }?.data
        ?: 0f).coerceAtLeast(1f)

val Data.CpuTemp: Int
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_CPU_TEMPERATURE }?.data?.toInt()
        ?: 0).coerceAtLeast(1)

val Data.CpuTempUnit: String
    get() = entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_CPU_TEMPERATURE }?.szLocalisedSrcUnits ?: "c"

val Data.CpuUsage: Int
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_CPU_USAGE }?.data?.toInt()
        ?: 0).coerceAtLeast(1)

val Data.RamUsagePercent: Float
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_PLUGIN_MISC && it.szSrcName == "Physical Memory Load" }?.data
        ?: 0f).coerceAtLeast(1f)

val Data.RamUsage: Float
    get() = (entries.firstOrNull { it.dwSrcId == SourceID.MONITORING_SOURCE_ID_RAM_USAGE }?.data
        ?: 0f).coerceAtLeast(1f)