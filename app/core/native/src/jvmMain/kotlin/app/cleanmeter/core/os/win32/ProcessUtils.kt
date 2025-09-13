package app.cleanmeter.core.os.win32

import java.io.BufferedReader
import java.io.InputStreamReader

object ProcessUtils {
    
    /**
     * Fallback method to get foreground process name using Windows tasklist command
     * This doesn't require JNA and should work in distributable environments
     */
    fun getForegroundProcessNameFallback(): String? {
        return try {
            // Use PowerShell to get the foreground window process
            val command = arrayOf(
                "powershell.exe", 
                "-Command",
                """
                Add-Type @"
                    using System;
                    using System.Diagnostics;
                    using System.Runtime.InteropServices;
                    public class Win32 {
                        [DllImport("user32.dll")]
                        public static extern IntPtr GetForegroundWindow();
                        [DllImport("user32.dll")]
                        public static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);
                    }
"@
                ${'$'}hwnd = [Win32]::GetForegroundWindow()
                ${'$'}processId = 0
                [Win32]::GetWindowThreadProcessId(${'$'}hwnd, [ref]${'$'}processId)
                ${'$'}process = Get-Process -Id ${'$'}processId -ErrorAction SilentlyContinue
                if (${'$'}process) { ${'$'}process.ProcessName } else { "" }
                """.trimIndent()
            )
            
            val processBuilder = ProcessBuilder(*command)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readText().trim()
            
            val exitCode = process.waitFor()
            reader.close()
            
            if (exitCode == 0 && result.isNotBlank()) {
                result
            } else {
                null
            }
        } catch (e: Exception) {
            println("Exception in fallback method: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
