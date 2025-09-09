package app.cleanmeter.core.os

expect object StartupManager {
    fun isAppRegisteredToStartWithSystem(): Boolean
    fun registerAppToStartWithSystem()
    fun removeAppFromStartWithSystem()
}
