package app.cleanmeter.core.os.win32

import app.cleanmeter.core.os.util.isDev
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path

object WinRegistry {
    const val STARTUP_ITEMS_LOCATION = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"
    const val REGISTRY_APP_NAME = "cleanmeter"

    val appDir: String
        get() {
            val currentDir = Path.of("").toAbsolutePath().toString()
            return if (isDev()) {
                "$currentDir\\bin"
            } else {
                "$currentDir\\app\\resources"
            }
        }

    fun read(location: String, key: String): List<String> {
        val proc = ProcessBuilder("reg", "query", location, "/v", key)
            .redirectErrorStream(true)
            .start()
        val input = BufferedReader(InputStreamReader(proc.inputStream))

        return input.lineSequence().toList()
    }

    fun write(key: String, value: String) {
        val proc = ProcessBuilder("cmd", "/c", "$appDir\\win-x64\\registry-write.bat", key, value)
            .redirectErrorStream(true)
            .start()

        val input = BufferedReader(InputStreamReader(proc.inputStream))

        println(input.lineSequence().toList())
    }

    fun delete(key: String) {
        val proc = ProcessBuilder("cmd", "/c", "$appDir\\win-x64\\registry-delete.bat", key)
            .redirectErrorStream(true)
            .start()

        val input = BufferedReader(InputStreamReader(proc.inputStream))

        println(input.lineSequence().toList())
    }

    fun isAppRegisteredToStartWithWindows(): Boolean {
        val queryOutput = read(STARTUP_ITEMS_LOCATION, REGISTRY_APP_NAME)

        return queryOutput.map { it.indexOf(REGISTRY_APP_NAME) >= 0 }.any { it }
    }

    fun registerAppToStartWithWindows() {
        write(REGISTRY_APP_NAME, "${Path.of("").toAbsolutePath()}\\$REGISTRY_APP_NAME.exe")
    }

    fun removeAppFromStartWithWindows() {
        delete(REGISTRY_APP_NAME)
    }
}
