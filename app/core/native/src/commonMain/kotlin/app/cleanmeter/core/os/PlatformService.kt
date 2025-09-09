package app.cleanmeter.core.os

import java.awt.Component

/**
 * Platform-specific service interface for OS-level operations
 */
expect class PlatformService() {
    companion object {
        fun changeWindowTransparency(w: Component, isTransparent: Boolean)
        fun isProcessElevated(): Boolean
        fun tryElevateProcess(isAutostart: Boolean)
        fun elevateProcess()
    }
}
