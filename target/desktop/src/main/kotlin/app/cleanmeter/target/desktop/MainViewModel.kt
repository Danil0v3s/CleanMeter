package app.cleanmeter.target.desktop

import androidx.lifecycle.ViewModel
import app.cleanmeter.target.desktop.data.OverlaySettingsRepository
import app.cleanmeter.target.desktop.model.OverlaySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainState(
    val overlaySettings: OverlaySettings = OverlaySettings(),
)


class MainViewModel : ViewModel() {
    private val _state: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val state: Flow<MainState>
        get() = _state

    init {
        observeOverlaySettings()
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
}
