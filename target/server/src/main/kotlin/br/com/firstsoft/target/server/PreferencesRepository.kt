package br.com.firstsoft.target.server

import br.com.firstsoft.target.server.ui.models.OverlaySettings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.prefs.Preferences

const val OVERLAY_SETTINGS_PREFERENCE_KEY = "OVERLAY_SETTINGS_PREFERENCE_KEY"
const val PREFERENCE_START_MINIMIZED = "PREFERENCE_START_MINIMIZED"

object PreferencesRepository {

    private val prefs = Preferences.userNodeForPackage(PreferencesRepository::class.java)

    fun getPreferenceString(key: String): String? = prefs.get(key, null)
    fun getPreferenceBoolean(key: String, defaultValue: Boolean = false): Boolean = prefs.getBoolean(key, defaultValue)
    fun getPreferenceBooleanNullable(key: String): Boolean? {
        return if (prefs.keys().any { it == key }) {
            prefs.getBoolean(key, false)
        } else {
            null
        }
    }

    fun setPreference(key: String, value: String) = prefs.put(key, value)
    fun setPreferenceBoolean(key: String, value: Boolean) = prefs.putBoolean(key, value)
}

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