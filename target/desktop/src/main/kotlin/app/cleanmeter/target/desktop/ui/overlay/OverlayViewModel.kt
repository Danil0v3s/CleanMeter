package app.cleanmeter.target.desktop.ui.overlay

import androidx.lifecycle.ViewModel
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.os.hardwaremonitor.HardwareMonitorReader
import app.cleanmeter.target.desktop.data.OverlaySettingsRepository
import app.cleanmeter.target.desktop.model.OverlaySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OverlayState(
    val overlaySettings: OverlaySettings? = null,
    val hardwareData: HardwareMonitorData? = null,
)

class OverlayViewModel : ViewModel() {
    private val _state: MutableStateFlow<OverlayState> = MutableStateFlow(OverlayState())
    val state: Flow<OverlayState>
        get() = _state

    init {
        observeOverlaySettings()
        observeHwInfo()
    }

    private fun observeOverlaySettings() {
        CoroutineScope(Dispatchers.IO).launch {
            OverlaySettingsRepository
                .data
                .collectLatest { overlaySettings ->
                    _state.update { it.copy(overlaySettings = overlaySettings) }
                }
        }
    }

    private fun observeHwInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            HardwareMonitorReader
                .currentData
                .collectLatest { hwInfoData ->
                    _state.update { it.copy(hardwareData = hwInfoData) }
                }
        }
    }
}