package app.cleanmeter.core.os

import app.cleanmeter.core.os.hardwaremonitor.HardwareMonitorProcessManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.util.*

actual object StartupManager {
    
    actual fun isAppRegisteredToStartWithSystem(): Boolean {
        return when (getCurrentPlatform()) {
            Platform.WINDOWS -> isAppRegisteredToStartWithWindows()
            Platform.MACOS -> {
                // TODO: Implement macOS startup check (using launchctl)
                println("macOS startup management not yet implemented")
                false
            }
            Platform.LINUX -> {
                // TODO: Implement Linux startup check (using systemd or autostart files)
                println("Linux startup management not yet implemented")
                false
            }
        }
    }
    
    actual fun registerAppToStartWithSystem() {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> registerAppToStartWithWindows()
            Platform.MACOS -> {
                // TODO: Implement macOS startup registration
                println("macOS startup registration not yet implemented")
            }
            Platform.LINUX -> {
                // TODO: Implement Linux startup registration
                println("Linux startup registration not yet implemented")
            }
        }
    }
    
    actual fun removeAppFromStartWithSystem() {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> removeAppFromStartWithWindows()
            Platform.MACOS -> {
                // TODO: Implement macOS startup removal
                println("macOS startup removal not yet implemented")
            }
            Platform.LINUX -> {
                // TODO: Implement Linux startup removal
                println("Linux startup removal not yet implemented")
            }
        }
    }
    
    // Windows-specific implementation
    private const val STARTUP_ITEMS_LOCATION = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"
    private const val REGISTRY_APP_NAME = "cleanmeter"
    
    private fun read(location: String, key: String): List<String> {
        val proc = ProcessBuilder("reg", "query", location, "/v", key)
            .redirectErrorStream(true)
            .start()
        val input = BufferedReader(InputStreamReader(proc.inputStream))
        
        return input.lineSequence().toList()
    }
    
    private fun write(location: String, key: String, value: String, type: String = "REG_SZ") {
        val proc = ProcessBuilder("reg", "add", location, "/v", key, "/t", type, "/d", value)
            .redirectErrorStream(true)
            .start()
        
        val input = BufferedReader(InputStreamReader(proc.inputStream))
        
        println(input.lineSequence().toList())
    }
    
    private fun delete(location: String, key: String) {
        val proc = ProcessBuilder("reg", "delete", location, "/v", key, "/f")
            .redirectErrorStream(true)
            .start()
        
        val input = BufferedReader(InputStreamReader(proc.inputStream))
        
        println(input.lineSequence().toList())
    }
    
    private fun isAppRegisteredToStartWithWindows(): Boolean {
        val queryOutput = read(STARTUP_ITEMS_LOCATION, REGISTRY_APP_NAME)
        
        return queryOutput.map { it.indexOf(REGISTRY_APP_NAME) >= 0 }.any { it }
    }
    
    private fun registerAppToStartWithWindows() {
        if (PlatformService.isProcessElevated()) {
            write(STARTUP_ITEMS_LOCATION, REGISTRY_APP_NAME, "\\\"${Path.of("").toAbsolutePath()}\\$REGISTRY_APP_NAME.exe\\\" --autostart")
            HardwareMonitorProcessManager.createService()
        }
    }
    
    private fun removeAppFromStartWithWindows() {
        if (PlatformService.isProcessElevated()) {
            delete(STARTUP_ITEMS_LOCATION, REGISTRY_APP_NAME)
            HardwareMonitorProcessManager.stopService()
            HardwareMonitorProcessManager.deleteService()
        }
    }
}

private fun getCurrentPlatform(): Platform {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        "win" in osName -> Platform.WINDOWS
        "mac" in osName -> Platform.MACOS
        else -> Platform.LINUX
    }
}
