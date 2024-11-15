package br.com.firstsoft.core.os.win32

import java.io.BufferedReader
import java.io.InputStreamReader

object WinRegistry {
    const val STARTUP_ITEMS_LOCATION = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"
    const val REGISTRY_APP_NAME = "cleanmeter"

    fun read(location: String, key: String): List<String> {
        val proc = ProcessBuilder("reg", "query", location, "/v", key)
            .redirectErrorStream(true)
            .start()
        val input = BufferedReader(InputStreamReader(proc.inputStream))

        return input.lineSequence().toList()
    }

    fun write(location: String, key: String, value: String, type: String = "REG_SZ") {
        val proc = ProcessBuilder("reg", "add", location, "/v", key, "/t", type, "/d", value)
            .redirectErrorStream(true)
            .start()

        val input = BufferedReader(InputStreamReader(proc.inputStream))

        println(input.lineSequence().toList())
    }

    fun delete(location: String, key: String) {
        val proc = ProcessBuilder("reg", "delete", location, "/v", key, "/f")
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
        write(STARTUP_ITEMS_LOCATION, REGISTRY_APP_NAME, "\\\"${System.getProperty("user.dir")}\\$REGISTRY_APP_NAME.exe\\\"")
    }

    fun removeAppFromStartWithWindows() {
        delete(STARTUP_ITEMS_LOCATION, REGISTRY_APP_NAME)
    }
}
