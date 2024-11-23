package br.com.firstsoft.target.server.data

import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.os.hardwaremonitor.HardwareMonitorReader
import br.com.firstsoft.core.os.hardwaremonitor.PresentMonProcessManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

object ObserveHardwareReadings {
    val data = combine(PresentMonProcessManager.state, HardwareMonitorReader.currentData) { presentMon, hwMonitorData ->
        Pair(presentMon, hwMonitorData)
    }.map { (presentMon, hwMonitorData) ->
        val displayedFps = HardwareMonitorData.Sensor(
            Name = "FPS (Displayed)",
            Identifier = "/presentmon/displayed",
            HardwareIdentifier = "/presentmon",
            SensorType = HardwareMonitorData.SensorType.Noise,
            Value = 1000 / presentMon.displayedTime
        )
        val presentedFps = HardwareMonitorData.Sensor(
            Name = "FPS (Presented)",
            Identifier = "/presentmon/presented",
            HardwareIdentifier = "/presentmon",
            SensorType = HardwareMonitorData.SensorType.Noise,
            Value = 1000 / presentMon.gpuTime
        )
        val frametime = HardwareMonitorData.Sensor(
            Name = "Frametime",
            Identifier = "/presentmon/frametime",
            HardwareIdentifier = "/presentmon",
            SensorType = HardwareMonitorData.SensorType.Noise,
            Value = presentMon.frameTime
        )

        hwMonitorData.copy(Sensors = buildList {
            addAll(hwMonitorData.Sensors)
            add(presentedFps)
            add(displayedFps)
            add(frametime)
        })
    }
}