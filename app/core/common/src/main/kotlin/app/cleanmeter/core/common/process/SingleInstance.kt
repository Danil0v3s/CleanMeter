package app.cleanmeter.core.common.process

import app.cleanmeter.core.common.reporting.ApplicationParams
import app.cleanmeter.core.common.reporting.setDefaultUncaughtExceptionHandler
import kotlin.system.exitProcess

fun singleInstance(args: Array<out String>, block: () -> Unit) {
    if(isAppAlreadyRunning()) {
        exitProcess(0)
    }

    ApplicationParams.parse(args)

    setDefaultUncaughtExceptionHandler()

    block()
}

private fun isAppAlreadyRunning(): Boolean {
    val os = System.getProperty("os.name").lowercase()
    val processName = "Clean Meter" // or jar name

    return try {
        val process = when {
            os.contains("win") -> {
                ProcessBuilder("tasklist", "/FI", "IMAGENAME eq $processName.exe").start()
            }
            os.contains("mac") || os.contains("nix") || os.contains("nux") -> {
                ProcessBuilder("pgrep", "-f", processName).start()
            }
            else -> return false
        }

        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()

        // Parse output to check if process exists (beyond current instance)
        output.lines().filterNot { it.isEmpty() }.size > 1
    } catch (e: Exception) {
        false
    }
}
