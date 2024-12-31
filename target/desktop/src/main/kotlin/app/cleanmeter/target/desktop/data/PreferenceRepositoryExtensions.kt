package app.cleanmeter.target.desktop.data

import app.cleanmeter.core.os.OVERLAY_SETTINGS_PREFERENCE_KEY
import app.cleanmeter.core.os.PreferencesRepository
import app.cleanmeter.target.desktop.model.OverlaySettings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun PreferencesRepository.loadOverlaySettings(): OverlaySettings {
    val json = getPreferenceString(OVERLAY_SETTINGS_PREFERENCE_KEY)
    val settings = if (json != null) {
        try {
            Json.decodeFromString<OverlaySettings>(json)
        } catch (e: Exception) {
            OverlaySettings()
        }
    } else {
        OverlaySettings()
    }
    return settings
}