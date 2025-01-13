package app.cleanmeter.core.os.hardwaremonitor

import app.cleanmeter.core.os.util.isDev
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.Path
import java.util.*

object HardwareMonitorProcessManager {
    private var process: Process? = null

    suspend fun checkRuntime(): Boolean {
        return try {
            val process = ProcessBuilder().apply {
                command("cmd.exe", "/c", "dotnet", "--list-runtimes")
            }.start()

            val scannerIn = Scanner(process.inputStream)
            val scannerErr = Scanner(process.errorStream)

            val stdOutput = emptyList<String>().toMutableList()
            val errOutput = emptyList<String>().toMutableList()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    while (scannerIn.hasNextLine()) {
                        stdOutput.add(scannerIn.nextLine())
                    }
                } catch (e: Exception) {
                    return@launch
                }

            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    while (scannerErr.hasNextLine()) {
                        errOutput.add(scannerIn.nextLine())
                    }
                } catch (e: Exception) {
                    return@launch
                }
            }

            return withContext(Dispatchers.IO) {
                val exitCode = process.waitFor()
                if (exitCode != 0) {
                    return@withContext false
                }

                val hasAtLeastEight = stdOutput
                    .map { it.split(" ").take(2).let { Pair(it[0], it[1].toVersion()) } } // transform into pairs of [name, version]
                    .filter { it.first.contains(".NETCore", true) }
                    .any { it.second >= Version(8,0,0) }

                hasAtLeastEight
            }
        } catch (ex: Exception) {
            return false
        }
    }

    fun start() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = if (isDev()) {
            "$currentDir\\HardwareMonitor\\HardwareMonitor\\bin\\Release\\net8.0\\HardwareMonitor.exe"
        } else {
            "$currentDir\\app\\resources\\HardwareMonitor.exe"
        }

        process = ProcessBuilder().apply {
            command("cmd.exe", "/c", file)
        }.start()

        val scannerIn = Scanner(process!!.inputStream)
        val scannerErr = Scanner(process!!.errorStream)

        CoroutineScope(Dispatchers.IO).launch {
            while (scannerIn.hasNextLine()) {
                System.out.println(scannerIn.nextLine())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            while (scannerErr.hasNextLine()) {
                System.err.println(scannerErr.nextLine())
            }
        }
    }

    fun stop() {
        process?.apply {
            descendants().forEach(ProcessHandle::destroy)
            destroy()
        }
        process = null
    }

    fun createService() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = "$currentDir\\app\\resources\\HardwareMonitor.exe"
        val command = listOf(
            "cmd.exe",
            "/c",
            "sc create svcleanmeter displayname= \"CleanMeter Service\" binPath= $file start= auto group= LocalServiceNoNetworkFirewall"
        )
        ProcessBuilder().apply {
            command(command)
        }.start()
    }

    fun stopService() {
        ProcessBuilder().apply {
            command(
                "cmd.exe",
                "/c",
                "sc stop svcleanmeter"
            )
        }.start()
    }

    fun deleteService() {
        ProcessBuilder().apply {
            command(
                "cmd.exe",
                "/c",
                "sc delete svcleanmeter"
            )
        }.start()
    }

}