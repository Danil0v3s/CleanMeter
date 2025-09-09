package app.cleanmeter.core.os.hardwaremonitor

expect object HardwareMonitorProcessManager {
    fun start()
    fun stop()
    fun createService()
    fun stopService()
    fun deleteService()
}
