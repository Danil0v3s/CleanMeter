package app.cleanmeter.core.os

import app.cleanmeter.core.os.hardwaremonitor.HardwareMonitorProcessManager

object ProcessManager {

    fun start() {
        HardwareMonitorProcessManager.start()
    }

    fun stop() {
        HardwareMonitorProcessManager.stop()
    }
}