package app.cleanmeter.core.common.hardwaremonitor


data class PresentMonReading(
    val application: String,
    val processID: Int,
    val swapChainAddress: String,
    val presentRuntime: String,
    val syncInterval: Int,
    val presentFlags: Int,
    val allowsTearing: Boolean,
    val presentMode: String,
    val cpuStartTime: Float,
    val frameTime: Float,
    val cpuBusy: Float,
    val cpuWait: Float,
    val gpuLatency: Float,
    val gpuTime: Float,
    val gpuBusy: Float,
    val gpuWait: Float,
    val displayLatency: Float,
    val displayedTime: Float,
)