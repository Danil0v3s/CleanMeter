package br.com.firstsoft.core.os.hwinfo

import br.com.firstsoft.core.os.resource.NativeResourceLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Path
import java.util.concurrent.TimeUnit

object HwInfoProcessManager {
    private var process: Process? = null
    private const val MAX_RETRIES = 1
    private var currentRetries = 0
    private var pollingJob: Job? = null

    init {
        currentRetries = 0
    }

    fun start() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = "$currentDir\\cleanmeter\\app\\resources\\HWiNFO64.exe"

        overwriteSettings()

        process = ProcessBuilder().apply {
            command("cmd.exe", "/c", file)
        }.start()

        pollingJob?.cancel()
        pollingJob = observeHwInfoPollingTime()
    }

    fun stop() {
        process?.apply {
            descendants().forEach(ProcessHandle::destroy)
            destroy()
        }
        process = null
    }

    private fun restart() {
        stop()
        start()
    }

    private fun overwriteSettings() {
        try {
            val sourceSettings = NativeResourceLoader.load("/hwinfo/HWiNFO64.INI.src")
            File("cleanmeter/app/resources/HWiNFO64.INI").printWriter().use { it.print(sourceSettings) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observeHwInfoPollingTime() = CoroutineScope(Dispatchers.IO).launch {
        var lastPollTime = 0L
        var accumulator = 0L

        HwInfoReader
            .currentData
            .cancellable()
            .map { it.header }
            .collectLatest {
                accumulator += 500
                if (accumulator < TimeUnit.SECONDS.toMillis(5)) return@collectLatest

                accumulator = 0
                when {
                    lastPollTime != it.pollTime -> {
                        lastPollTime = it.pollTime
                        currentRetries = 0
                    }
                    lastPollTime == it.pollTime && currentRetries < MAX_RETRIES -> {
                        currentRetries++
                        restart()
                    }
                }
            }
    }
}