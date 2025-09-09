package app.cleanmeter.core.os

import java.util.prefs.Preferences

actual object PreferencesRepository {

    private val prefs = Preferences.userNodeForPackage(PreferencesRepository::class.java)

    actual fun getPreferenceString(key: String): String? = prefs.get(key, null)
    actual fun getPreferenceBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    actual fun getPreferenceBooleanNullable(key: String): Boolean? {
        return if (prefs.keys().any { it == key }) {
            prefs.getBoolean(key, false)
        } else {
            null
        }
    }

    actual fun setPreference(key: String, value: String) = prefs.put(key, value)
    actual fun setPreferenceBoolean(key: String, value: Boolean) = prefs.putBoolean(key, value)
    actual fun clear() = prefs.clear()
}