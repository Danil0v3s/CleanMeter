package app.cleanmeter.core.os

import app.cleanmeter.core.os.win32.WinRegistry

actual object StartupManager {
    
    actual fun isAppRegisteredToStartWithSystem(): Boolean {
        return when (getCurrentPlatform()) {
            Platform.WINDOWS -> WinRegistry.isAppRegisteredToStartWithWindows()
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
            Platform.WINDOWS -> WinRegistry.registerAppToStartWithWindows()
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
            Platform.WINDOWS -> WinRegistry.removeAppFromStartWithWindows()
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
}
