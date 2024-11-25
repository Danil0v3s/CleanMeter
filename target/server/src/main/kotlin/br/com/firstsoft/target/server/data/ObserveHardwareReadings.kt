package br.com.firstsoft.target.server.data

import br.com.firstsoft.core.os.hardwaremonitor.HardwareMonitorReader

object ObserveHardwareReadings {
    val data = HardwareMonitorReader.currentData
}