package app.cleanmeter.core.os

import java.util.prefs.Preferences

const val OVERLAY_SETTINGS_PREFERENCE_KEY = "OVERLAY_SETTINGS_PREFERENCE_KEY"
const val PREFERENCE_START_MINIMIZED = "PREFERENCE_START_MINIMIZED"
const val PREFERENCE_PERMISSION_CONSENT = "PREFERENCE_PERMISSION_CONSENT"

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
    fun clear() = prefs.clear()
}