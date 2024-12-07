package br.com.firstsoft.target.server.ui.overlay

import androidx.lifecycle.ViewModel
import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.common.hwinfo.HwInfoData
import br.com.firstsoft.core.os.hwinfo.HwInfoReader
import br.com.firstsoft.target.server.data.ObserveHardwareReadings
import br.com.firstsoft.target.server.data.OverlaySettingsRepository
import br.com.firstsoft.target.server.model.OverlaySettings
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
            ObserveHardwareReadings.data.collectLatest { hwInfoData ->
                _state.update { it.copy(hardwareData = hwInfoData) }
            }
        }
    }
}