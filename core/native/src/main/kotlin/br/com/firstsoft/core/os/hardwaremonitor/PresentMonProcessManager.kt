package br.com.firstsoft.core.os.hardwaremonitor

import br.com.firstsoft.core.common.hardwaremonitor.PresentMonReading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Path

object PresentMonProcessManager {

    private var process: Process? = null
    private const val MAX_RETRIES = 1
    private var currentRetries = 0
    private var pollingJob: Job? = null

    init {
        currentRetries = 0
    }

    private val _state: MutableStateFlow<PresentMonReading?> = MutableStateFlow(null)
    val state: Flow<PresentMonReading> = _state.filterNotNull()

    fun start() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = "$currentDir\\cleanmeter\\app\\resources\\presentmon.exe"
//        val file = "D:\\Projetos\\Personal\\PCMonitoR\\presentmon\\presentmon.exe"

        process = ProcessBuilder()
            .apply {
                command(
                    "cmd.exe",
                    "/c",
                    file,
//                    "--exclude",
//                    "<unknown>",
//                    "--exclude",
//                    "WindowsTerminal.exe",
//                    "--exclude",
//                    "cmd.exe",
                    "--stop_existing_session",
                    "--no_console_stats",
                    "--output_stdout"
                )
            }
//            .inheritIO()
            .start()
        process?.inputStream?.run { observeOutput(this) }
        pollingJob?.cancel()
        pollingJob = observePollingTime()
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

    private fun observeOutput(inputStream: InputStream) {
        var isFirst = true
        val reader = BufferedReader(InputStreamReader(inputStream))

        CoroutineScope(Dispatchers.IO).launch {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                if (isFirst) {
                    isFirst = false
                    continue
                }

                val columns = line.split(",")
                val reading = PresentMonReading(
                    application = columns[0],
                    processID = columns[1].toInt(),
                    swapChainAddress = columns[2],
                    presentRuntime = columns[3],
                    syncInterval = columns[4].toInt(),
                    presentFlags = columns[5].toInt(),
                    allowsTearing = columns[6].toInt() != 0,
                    presentMode = columns[7],
                    cpuStartTime = columns[8].toFloat(),
                    frameTime = columns[9].toFloat(),
                    cpuBusy = columns[10].toFloat(),
                    cpuWait = columns[11].toFloat(),
                    gpuLatency = columns[12].toFloat(),
                    gpuTime = columns[13].toFloat(),
                    gpuBusy = columns[14].toFloat(),
                    gpuWait = columns[15].toFloat(),
                    displayLatency = columns[16].toFloatOrNull() ?: 0f,
                    displayedTime = columns[17].toFloatOrNull() ?: 0f,
                )

                _state.update { reading }
            }
        }
    }

    private fun observePollingTime() = CoroutineScope(Dispatchers.IO).launch {
        var lastPollTime = 0L
        var accumulator = 0L

//        HardwareMonitorReader
//            .currentData
//            .cancellable()
//            .map { it }
//            .collectLatest {
//                accumulator += 500
//                if (accumulator < TimeUnit.SECONDS.toMillis(5)) return@collectLatest
//
//                accumulator = 0
//                when {
//                    lastPollTime != it.pollTime -> {
//                        lastPollTime = it.pollTime
//                        currentRetries = 0
//                    }
//                    lastPollTime == it.pollTime && currentRetries < MAX_RETRIES -> {
//                        currentRetries++
//                        restart()
//                    }
//                }
//            }
    }
}