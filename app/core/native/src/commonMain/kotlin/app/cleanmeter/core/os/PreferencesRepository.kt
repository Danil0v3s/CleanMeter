package app.cleanmeter.core.os

const val OVERLAY_SETTINGS_PREFERENCE_KEY = "OVERLAY_SETTINGS_PREFERENCE_KEY"
const val PREFERENCE_START_MINIMIZED = "PREFERENCE_START_MINIMIZED"
const val PREFERENCE_PERMISSION_CONSENT = "PREFERENCE_PERMISSION_CONSENT"

expect object PreferencesRepository {
    fun getPreferenceString(key: String): String?
    fun getPreferenceBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun getPreferenceBooleanNullable(key: String): Boolean?
    fun setPreference(key: String, value: String)
    fun setPreferenceBoolean(key: String, value: Boolean)
    fun clear()
}
