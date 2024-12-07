package br.com.firstsoft.core.os

import br.com.firstsoft.core.os.hardwaremonitor.HardwareMonitorProcessManager

object ProcessManager {

    fun start() {
        HardwareMonitorProcessManager.start()
    }

    fun stop() {
        HardwareMonitorProcessManager.stop()
    }
}