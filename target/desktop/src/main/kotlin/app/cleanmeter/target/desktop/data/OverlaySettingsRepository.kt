package app.cleanmeter.target.desktop.data

import app.cleanmeter.core.os.OVERLAY_SETTINGS_PREFERENCE_KEY
import app.cleanmeter.core.os.PreferencesRepository
import app.cleanmeter.target.desktop.model.OverlaySettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object OverlaySettingsRepository {

    private val _data = MutableStateFlow<OverlaySettings?>(null)
    val data: Flow<OverlaySettings>
        get() = _data.filterNotNull()

    init {
        _data.value = PreferencesRepository.loadOverlaySettings()
    }

    fun setOverlaySettings(settings: OverlaySettings?) {
        if (settings == null) return
        _data.value = settings
        PreferencesRepository.setPreference(OVERLAY_SETTINGS_PREFERENCE_KEY, Json.encodeToString(settings))
    }
}