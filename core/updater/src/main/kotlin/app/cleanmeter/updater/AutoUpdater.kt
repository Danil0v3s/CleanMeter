package app.cleanmeter.updater

import app.cleanmeter.core.common.reporting.ApplicationParams
import app.cleanmeter.core.os.hardwaremonitor.HardwareMonitorProcessManager
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isNotEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File
import java.nio.file.Path
import kotlin.system.exitProcess

sealed class UpdateState {
    data object NotAvailable : UpdateState()
    data class Available(val version: String) : UpdateState()
    data class Downloading(val version: String, val progress: Float) : UpdateState()
    data class Downloaded(val version: String, val file: File) : UpdateState()
}

object AutoUpdater {

    private const val propertiesUrl =
        "https://raw.githubusercontent.com/Danil0v3s/CleanMeter/refs/heads/main/gradle.properties"

    private var _state = MutableStateFlow<UpdateState>(UpdateState.NotAvailable)
    val state: StateFlow<UpdateState> = _state

    private val client = HttpClient(OkHttp)
    private var _currentLiveVersion: Version? = null
    val currentLiveVersion: String
        get() = _currentLiveVersion.toString()

    private var downloadJob: Job? = null

    init {
        checkForUpdates()
    }

    fun downloadUpdate() {
        if (_state.value is UpdateState.Available && _currentLiveVersion != null) {
            downloadUpdatePackage(_currentLiveVersion!!)
        }
    }

    fun prepareForManualUpdate() {
        val state = _state.value
        if (state !is UpdateState.Downloaded) return

        if (ApplicationParams.isAutostart) {
            HardwareMonitorProcessManager.stopService()
        } else {
            HardwareMonitorProcessManager.stop()
        }
        Desktop.getDesktop().open(state.file?.absoluteFile)
        exitProcess(0)
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        checkForUpdates()
    }

    private suspend fun isUpdateAvailable(): Boolean {
        val map = client.getPropertiesMap()
        val liveVersion = map["projectVersion"]?.toVersion(strict = false)
//        val currentVersion = System.getProperty("jpackage.app-version")?.toVersion(strict = false)
        val currentVersion = "0.0.7"?.toVersion(strict = false)
        _currentLiveVersion = liveVersion
        return liveVersion != null && currentVersion != null && liveVersion > currentVersion
    }

    private fun checkForUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            if (isUpdateAvailable()) {
                _state.update { UpdateState.Available(currentLiveVersion) }
            }
        }
    }

    private fun downloadUpdatePackage(
        liveVersion: Version,
    ) {
        val file = File("cleanmeter.windows.$liveVersion.zip")
        if (file.exists()) {
            _state.update { UpdateState.Downloaded(currentLiveVersion, file) }
            return
        }

        val url = "https://github.com/Danil0v3s/CleanMeter/releases/download/$liveVersion/cleanmeter.windows.zip"
        _state.update { UpdateState.Downloading(currentLiveVersion, 0f) }

        downloadJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                client.prepareGet(url).execute { response ->
                    val contentLength = (response.contentLength() ?: 0).toFloat()
                    val channel: ByteReadChannel = response.body()
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (packet.isNotEmpty) {
                            val bytes = packet.readBytes()
                            file.appendBytes(bytes)
                            _state.update { UpdateState.Downloading(currentLiveVersion, file.length() / contentLength) }
                        }
                    }

                    _state.update { UpdateState.Downloaded(currentLiveVersion, file) }
                    println("A file saved to ${file.path}")
                }
            } catch (ex: CancellationException) {
                file.delete()
            }
        }
    }

    private fun invokeUpdater(packageFile: File) {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = "$currentDir\\app\\resources\\Updater.exe"
        ProcessBuilder().apply {
            command(
                "cmd.exe",
                "/c",
                file,
                "--package=${packageFile.path}",
                "--path=$currentDir",
                "--autostart=${ApplicationParams.isAutostart}"
            )
        }.start()
    }

    private suspend fun HttpClient.getPropertiesMap(): Map<String, String> {
        val response = get(propertiesUrl)
        val body = response.bodyAsText()
        return body.split("\n").map { line -> line.split("=").let { it[0] to it[1] } }.toMap()
    }
}


