package app.cleanmeter.core.os

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import java.io.File
import java.nio.file.Path

actual object PreferencesRepository {

    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    private val preferencesFile: File by lazy {
        val currentDir = Path.of("").toAbsolutePath().toString()
        File(currentDir, "preferences.json")
    }
    
    private fun loadPreferences(): Map<String, JsonElement> {
        return if (preferencesFile.exists()) {
            try {
                val jsonContent = preferencesFile.readText()
                json.decodeFromString<Map<String, JsonElement>>(jsonContent)
            } catch (e: Exception) {
                emptyMap()
            }
        } else {
            emptyMap()
        }
    }
    
    private fun savePreferences(data: Map<String, JsonElement>) {
        try {
            val jsonContent = json.encodeToString(data)
            preferencesFile.writeText(jsonContent)
        } catch (e: Exception) {
            // Silently fail - matches original behavior
        }
    }

    actual fun getPreferenceString(key: String): String? {
        val data = loadPreferences()
        return (data[key] as? JsonPrimitive)?.contentOrNull
    }
    
    actual fun getPreferenceBoolean(key: String, defaultValue: Boolean): Boolean {
        val data = loadPreferences()
        return (data[key] as? JsonPrimitive)?.booleanOrNull ?: defaultValue
    }
    
    actual fun getPreferenceBooleanNullable(key: String): Boolean? {
        val data = loadPreferences()
        return (data[key] as? JsonPrimitive)?.booleanOrNull
    }

    actual fun setPreference(key: String, value: String) {
        val data = loadPreferences().toMutableMap()
        data[key] = JsonPrimitive(value)
        savePreferences(data)
    }
    
    actual fun setPreferenceBoolean(key: String, value: Boolean) {
        val data = loadPreferences().toMutableMap()
        data[key] = JsonPrimitive(value)
        savePreferences(data)
    }
    
    actual fun clear() {
        savePreferences(emptyMap())
    }
}