package app.cleanmeter.core.os.win32

import com.sun.jna.Native

object JnaConfig {
    
    fun initialize() {
        try {
            println("Configuring JNA for distributable...")
            
            // Force JNA to extract native libraries to a temp directory
            System.setProperty("jna.tmpdir", System.getProperty("java.io.tmpdir") + "/jna-cleanmeter")
            
            // Enable debug mode for JNA
            System.setProperty("jna.debug_load", "true")
            System.setProperty("jna.debug_load.jna", "true")
            
            // Force library loading
            System.setProperty("jna.nosys", "false")
            
            println("JNA temp directory: ${System.getProperty("jna.tmpdir")}")
            println("JNA library path: ${System.getProperty("jna.library.path")}")
            println("Java library path: ${System.getProperty("java.library.path")}")
            
            // Test JNA functionality early
            val version = Native.VERSION
            println("JNA version loaded successfully: $version")
            
        } catch (e: Exception) {
            println("Error configuring JNA: ${e.message}")
            e.printStackTrace()
        }
    }
}
