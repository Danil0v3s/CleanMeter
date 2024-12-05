package br.com.firstsoft.updater

import br.com.firstsoft.core.common.reporting.ApplicationParams
import br.com.firstsoft.core.os.hardwaremonitor.HardwareMonitorProcessManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File
import java.nio.file.Path
import kotlin.system.exitProcess


object AutoUpdater {

    private const val propertiesUrl =
        "https://raw.githubusercontent.com/Danil0v3s/CleanMeter/refs/heads/main/gradle.properties"

    private var _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private var _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    private var _isUpdateAvailable = MutableStateFlow(false)
    val isUpdateAvailable: StateFlow<Boolean> = _isUpdateAvailable

    private val client = HttpClient(OkHttp)
    private var currentLiveVersion: Version? = null

    init {
        checkForUpdates()
    }

    fun withAutoUpdate(block: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val isUpdateAvailable = isUpdateAvailable()
            _isUpdateAvailable.update { isUpdateAvailable }

            downloadUpdate { file ->
                invokeUpdater(file)
                exitProcess(0)
            }
        }

        block()
    }

    fun downloadUpdate(onDone: (File) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            if (_isUpdateAvailable.value && currentLiveVersion != null) {
                downloadUpdatePackage(currentLiveVersion!!) { file ->
                    onDone(file)
                }
            }
        }
    }

    fun prepareForManualUpdate(file: File) {
        if (ApplicationParams.isAutostart) {
            HardwareMonitorProcessManager.stopService()
        } else {
            HardwareMonitorProcessManager.stop()
        }
        Desktop.getDesktop().open(file.absoluteFile)
        exitProcess(0)
    }

    private suspend fun isUpdateAvailable(): Boolean {
        val map = client.getPropertiesMap()
        val liveVersion = map["projectVersion"]?.toVersion(strict = false)
//        val currentVersion = System.getProperty("jpackage.app-version")?.toVersion(strict = false)
        val currentVersion = "0.0.1".toVersion(strict = false)
        currentLiveVersion = liveVersion
        return liveVersion != null && currentVersion != null && liveVersion > currentVersion
    }

    private fun checkForUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            val isUpdateAvailable = isUpdateAvailable()
            _isUpdateAvailable.update { isUpdateAvailable }
        }
    }

    private suspend fun downloadUpdatePackage(
        liveVersion: Version,
        onDone: (File) -> Unit
    ) {
        val file = File("cleanmeter.windows.$liveVersion.zip")
        if (file.exists()) {
            onDone(file)
            return
        }
        val url = "https://github.com/Danil0v3s/CleanMeter/releases/download/$liveVersion/cleanmeter.windows.zip"
        _isUpdating.update { true }

        client.prepareGet(url).execute { response ->
            val contentLength = (response.contentLength() ?: 0).toFloat()
            val channel: ByteReadChannel = response.body()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (packet.isNotEmpty) {
                    val bytes = packet.readBytes()
                    file.appendBytes(bytes)
                    _progress.update { file.length() / contentLength }
                }
            }
            onDone(file)
            _isUpdating.update { false }
            println("A file saved to ${file.path}")
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


