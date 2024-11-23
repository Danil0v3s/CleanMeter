package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.runtime.Composable
import br.com.firstsoft.core.common.hardwaremonitor.GpuTemp
import br.com.firstsoft.core.common.hardwaremonitor.GpuTempUnit
import br.com.firstsoft.core.common.hardwaremonitor.GpuUsage
import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.common.hardwaremonitor.VramUsage
import br.com.firstsoft.core.common.hardwaremonitor.VramUsagePercent
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.Progress
import java.util.*

@Composable
internal fun GpuSection(overlaySettings: OverlaySettings, data: HardwareMonitorData) {
    if (overlaySettings.sensors.gpuTemp.isEnabled || overlaySettings.sensors.gpuUsage.isEnabled || overlaySettings.sensors.vramUsage.isEnabled) {
        Pill(
            title = "GPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.sensors.gpuTemp.isEnabled) {
                Progress(
                    value = data.GpuTemp / 100f,
                    label = "${data.GpuTemp}",
                    unit = data.GpuTempUnit,
                    progressType = overlaySettings.progressType
                )
            }
            if (overlaySettings.sensors.gpuUsage.isEnabled) {
                Progress(
                    value = data.GpuUsage / 100f,
                    label = String.format("%02d", data.GpuUsage, Locale.US),
                    unit = "%",
                    progressType = overlaySettings.progressType
                )
            }
            if (overlaySettings.sensors.vramUsage.isEnabled) {
                Progress(
                    value = data.VramUsagePercent / 100f,
                    label = String.format("%02.1f", data.VramUsage / 1000, Locale.US),
                    unit = "GB",
                    progressType = overlaySettings.progressType
                )
            }
        }
    }
}