package app.cleanmeter.core.os.hardwaremonitor

expect object HardwareMonitorProcessManager {
    suspend fun checkRuntime(): Boolean
    fun start()
    fun stop()
    fun createService()
    fun stopService()
    fun deleteService()
}
