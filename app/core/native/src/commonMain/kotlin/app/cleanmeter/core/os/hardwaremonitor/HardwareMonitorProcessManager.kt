package app.cleanmeter.core.os.hardwaremonitor

expect object HardwareMonitorProcessManager {
    fun start()
    fun stop()
    fun isServiceCreated(): Boolean
    fun createService()
    fun stopService()
    fun deleteService()
}
