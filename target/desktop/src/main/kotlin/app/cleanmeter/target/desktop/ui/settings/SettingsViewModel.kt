package app.cleanmeter.target.desktop.ui.settings

import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.os.hardwaremonitor.HardwareMonitorProcessManager
import app.cleanmeter.core.os.hardwaremonitor.HardwareMonitorReader
import app.cleanmeter.core.os.hardwaremonitor.Packet
import app.cleanmeter.core.os.hardwaremonitor.SocketClient
import app.cleanmeter.core.os.win32.WindowsService
import app.cleanmeter.target.desktop.KeyboardEvent
import app.cleanmeter.target.desktop.KeyboardManager
import app.cleanmeter.target.desktop.data.OverlaySettingsRepository
import app.cleanmeter.core.os.PREFERENCE_PERMISSION_CONSENT
import app.cleanmeter.core.os.PreferencesRepository
import app.cleanmeter.target.desktop.model.OverlaySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.util.Scanner

sealed interface Log {
    @JvmInline
    value class Info(val value: String)
    @JvmInline
    value class Error(val value: String)
}

data class SettingsState(
    val overlaySettings: OverlaySettings? = null,
    val hardwareData: HardwareMonitorData? = null,
    val isRecording: Boolean = false,
    val adminConsent: Boolean = false,
    val isRuntimeAvailable: Boolean = false,
    val logSink: String = ""
)

sealed class SettingsEvent {
    data class OptionsToggle(val data: CheckboxSectionOption) : SettingsEvent()
    data class SwitchToggle(val section: SectionType, val isEnabled: Boolean) : SettingsEvent()
    data class CustomSensorSelect(val sensor: SensorType, val sensorId: String) : SettingsEvent()
    data class DisplaySelect(val displayIndex: Int) : SettingsEvent()
    data class OverlayPositionIndexSelect(val index: Int) : SettingsEvent()
    data class OverlayCustomPositionSelect(val offset: IntOffset, val isPositionLocked: Boolean) : SettingsEvent()
    data class OverlayCustomPositionEnable(val isEnabled: Boolean) : SettingsEvent()
    data class OverlayOrientationSelect(val isHorizontal: Boolean) : SettingsEvent()
    data class OverlayOpacityChange(val opacity: Float) : SettingsEvent()
    data class OverlayGraphChange(val progressType: OverlaySettings.ProgressType) : SettingsEvent()
    data class DarkThemeToggle(val isEnabled: Boolean) : SettingsEvent()
    data class FpsApplicationSelect(val applicationName: String) : SettingsEvent()
    data class BoundarySet(val sensorType: SensorType, val boundaries: OverlaySettings.Sensor.GraphSensor.Boundaries) : SettingsEvent()
    data class PollingRateSelect(val pollingRate: Long) : SettingsEvent()
    data object ConsentGiven : SettingsEvent()
    data object ToggleLoggingEnabled : SettingsEvent()
}

class SettingsViewModel : ViewModel() {

    private val _oldOut = System.out;
    private val _oldErr = System.err;
    private var _loggerJob: Job? = null;

    private val _state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val state: Flow<SettingsState>
        get() = _state

    private val dataHistory = emptyList<HardwareMonitorData>().toMutableList()

    init {
        observeOverlaySettings()
        observeData()
        observeRecordingHotkey()
        observeRecordingState()
        sendInitialPollingRate()
        checkForNetCoreRuntime()
        checkIfLoggingIsEnabled()

        _state.update {
            it.copy(
                adminConsent = PreferencesRepository.getPreferenceBoolean(PREFERENCE_PERMISSION_CONSENT, false)
            )
        }
    }

    private fun checkIfLoggingIsEnabled() {
        CoroutineScope(Dispatchers.IO).launch {
            val settings = OverlaySettingsRepository.data.first()

            if (settings.isLoggingEnabled) {
                enableLogSink()
            }
        }
    }

    private fun enableLogSink() {
        _loggerJob?.cancel()
        _loggerJob = CoroutineScope(Dispatchers.IO).launch {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val printStream = PrintStream(byteArrayOutputStream)
            System.setOut(printStream)
            System.setErr(printStream)

            while (true) {
                val output = byteArrayOutputStream.toString()
                if (output.isNotEmpty()) {
                    _state.update { it.copy(logSink = output) }
                }
                delay(500)
            }
        }
    }

    private fun checkForNetCoreRuntime() {
        CoroutineScope(Dispatchers.IO).launch {
            val isRuntimeAvailable = HardwareMonitorProcessManager.checkRuntime()
            _state.update { it.copy(isRuntimeAvailable = isRuntimeAvailable) }
        }
    }

    private fun observeOverlaySettings() {
        CoroutineScope(Dispatchers.IO).launch {
            OverlaySettingsRepository
                .data
                .collectLatest { overlaySettings ->
                    _state.update { it.copy(overlaySettings = overlaySettings) }
                }
        }
    }

    private fun observeData() {
        CoroutineScope(Dispatchers.IO).launch {
            HardwareMonitorReader
                .currentData
                .collectLatest { hwInfoData ->
                    _state.update { it.copy(hardwareData = hwInfoData) }
                }
        }
    }

    private fun observeRecordingHotkey() {
        CoroutineScope(Dispatchers.Default).launch {
            KeyboardManager
                .filter(KeyboardEvent.ToggleRecording)
                .collectLatest {
                    println("Toggle recording ${_state.value.isRecording}")
                    _state.update { it.copy(isRecording = !it.isRecording) }
                }
        }
    }

    private fun observeRecordingState() {
        CoroutineScope(Dispatchers.Default).launch {
            _state
                .collectLatest { state ->
                    when {
                        state.isRecording && state.hardwareData != null -> {
                            dataHistory.add(state.hardwareData)
                            return@collectLatest
                        }

                        !state.isRecording && dataHistory.isNotEmpty() -> {
                            File("cleanmeter.recording.${System.currentTimeMillis()}.json").printWriter().use {
                                it.append(Json.encodeToString(dataHistory))
                                dataHistory.clear()
                            }
                        }

                        dataHistory.isNotEmpty() -> dataHistory.clear()
                        else -> Unit
                    }
                }
        }
    }

    private fun sendInitialPollingRate() {
        CoroutineScope(Dispatchers.IO).launch {
            // wait for first item
            HardwareMonitorReader.currentData.first()

            _state.value.overlaySettings?.let {
                SocketClient.setPollingRate(it.pollingRate)
                SocketClient.sendPacket(Packet.SelectPollingRate(it.pollingRate.toShort()))
            }
        }
    }

    fun onEvent(event: SettingsEvent) = with(_state.value) {
        when (event) {
            is SettingsEvent.OptionsToggle -> onOptionsToggle(event.data, this)
            is SettingsEvent.SwitchToggle -> onSwitchToggle(event.section, event.isEnabled, this)
            is SettingsEvent.CustomSensorSelect -> onCustomSensorSelect(event.sensor, event.sensorId, this)
            is SettingsEvent.DisplaySelect -> onDisplaySelect(event.displayIndex, this)
            is SettingsEvent.OverlayPositionIndexSelect -> onOverlayPositionIndexSelect(event.index, this)
            is SettingsEvent.OverlayCustomPositionSelect -> onOverlayCustomPositionSelect(event.offset, event.isPositionLocked, this)
            is SettingsEvent.OverlayCustomPositionEnable -> onOverlayCustomPositionEnable(event.isEnabled, this)
            is SettingsEvent.OverlayOrientationSelect -> onOverlayOrientationSelect(event.isHorizontal, this)
            is SettingsEvent.OverlayOpacityChange -> onOverlayOpacityChange(event.opacity, this)
            is SettingsEvent.OverlayGraphChange -> onOverlayGraphChange(event.progressType, this)
            is SettingsEvent.DarkThemeToggle -> onDarkModeToggle(event.isEnabled, this)
            is SettingsEvent.FpsApplicationSelect -> onFpsApplicationSelect(event.applicationName, this)
            is SettingsEvent.BoundarySet -> onBoundarySet(event.sensorType, event.boundaries, this)
            is SettingsEvent.ConsentGiven -> onConsentGiven()
            is SettingsEvent.PollingRateSelect -> onPollingRateSelect(event.pollingRate, this)
            is SettingsEvent.ToggleLoggingEnabled -> onToggleLoggingEnabled(this)
        }
    }

    private fun onToggleLoggingEnabled(settingsState: SettingsState) {
        if (settingsState.overlaySettings == null) return
        val newSettings = settingsState.overlaySettings.copy(
            isLoggingEnabled = !settingsState.overlaySettings.isLoggingEnabled
        )

        if (newSettings.isLoggingEnabled) {
            enableLogSink()
        } else {
            _loggerJob?.cancel()
            System.setOut(_oldOut)
            System.setErr(_oldErr)
        }

        OverlaySettingsRepository.setOverlaySettings(newSettings)
    }

    private fun onPollingRateSelect(pollingRate: Long, settingsState: SettingsState) {
        if (settingsState.overlaySettings == null) return

        val newSettings = settingsState.overlaySettings.copy(pollingRate = pollingRate)

        SocketClient.setPollingRate(pollingRate)
        SocketClient.sendPacket(Packet.SelectPollingRate(pollingRate.toShort()))

        OverlaySettingsRepository.setOverlaySettings(newSettings)
    }

    private fun onConsentGiven() {
        PreferencesRepository.setPreferenceBoolean(PREFERENCE_PERMISSION_CONSENT, true)
        _state.update { it.copy(adminConsent = true) }
        WindowsService.elevateProcess()
    }

    private fun onBoundarySet(
        sensorType: SensorType,
        boundaries: OverlaySettings.Sensor.GraphSensor.Boundaries,
        settingsState: SettingsState
    ) {
        if (settingsState.overlaySettings == null) return

        val newSettings = when (sensorType) {
            SensorType.CpuTemp -> {
                val sensor = settingsState.overlaySettings.sensors.cpuTemp
                settingsState.overlaySettings.copy(
                    sensors = settingsState.overlaySettings.sensors.copy(
                        cpuTemp = sensor.copy(boundaries = boundaries),
                    )
                )
            }

            SensorType.CpuUsage -> {
                val sensor = settingsState.overlaySettings.sensors.cpuUsage
                settingsState.overlaySettings.copy(
                    sensors = settingsState.overlaySettings.sensors.copy(
                        cpuUsage = sensor.copy(boundaries = boundaries),
                    )
                )
            }

            SensorType.GpuTemp -> {
                val sensor = settingsState.overlaySettings.sensors.gpuTemp
                settingsState.overlaySettings.copy(
                    sensors = settingsState.overlaySettings.sensors.copy(
                        gpuTemp = sensor.copy(boundaries = boundaries),
                    )
                )
            }

            SensorType.GpuUsage -> {
                val sensor = settingsState.overlaySettings.sensors.gpuUsage
                settingsState.overlaySettings.copy(
                    sensors = settingsState.overlaySettings.sensors.copy(
                        gpuUsage = sensor.copy(boundaries = boundaries),
                    )
                )
            }

            SensorType.VramUsage -> {
                val sensor = settingsState.overlaySettings.sensors.vramUsage
                settingsState.overlaySettings.copy(
                    sensors = settingsState.overlaySettings.sensors.copy(
                        vramUsage = sensor.copy(boundaries = boundaries),
                    )
                )
            }

            SensorType.RamUsage -> {
                val sensor = settingsState.overlaySettings.sensors.ramUsage
                settingsState.overlaySettings.copy(
                    sensors = settingsState.overlaySettings.sensors.copy(
                        ramUsage = sensor.copy(boundaries = boundaries),
                    )
                )
            }

            SensorType.Framerate -> settingsState.overlaySettings
            SensorType.Frametime -> settingsState.overlaySettings
            SensorType.TotalVramUsed -> settingsState.overlaySettings
            SensorType.UpRate -> settingsState.overlaySettings
            SensorType.DownRate -> settingsState.overlaySettings
            SensorType.NetGraph -> settingsState.overlaySettings
            SensorType.CpuConsumption -> settingsState.overlaySettings
            SensorType.GpuConsumption -> settingsState.overlaySettings
        }

        OverlaySettingsRepository.setOverlaySettings(newSettings)
    }

    private fun onFpsApplicationSelect(applicationName: String, settingsState: SettingsState) {
        SocketClient.sendPacket(Packet.SelectPresentMonApp(applicationName))
    }

    private fun onDarkModeToggle(enabled: Boolean, settingsState: SettingsState) {
        with(settingsState) {
            val newSettings = overlaySettings?.copy(isDarkTheme = enabled)
            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onOverlayGraphChange(progressType: OverlaySettings.ProgressType, settingsState: SettingsState) {
        with(settingsState) {
            val newSettings = overlaySettings?.copy(
                progressType = progressType,
            )

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onOverlayOpacityChange(opacity: Float, settingsState: SettingsState) {
        with(settingsState) {
            val newSettings = overlaySettings?.copy(
                opacity = opacity,
            )

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onOverlayOrientationSelect(isHorizontal: Boolean, settingsState: SettingsState) {
        with(settingsState) {
            val newSettings = overlaySettings?.copy(
                isHorizontal = isHorizontal,
            )

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onOverlayCustomPositionSelect(
        offset: IntOffset,
        positionLocked: Boolean,
        settingsState: SettingsState
    ) {
        with(settingsState) {
            val newSettings = overlaySettings?.copy(
                positionX = offset.x,
                positionY = offset.y,
                isPositionLocked = positionLocked
            )

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onOverlayCustomPositionEnable(enabled: Boolean, settingsState: SettingsState) {
        with(settingsState) {
            val newSettings = overlaySettings?.copy(
                positionIndex = if (enabled) 6 else 0,
                isPositionLocked = true
            )

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onOverlayPositionIndexSelect(index: Int, settingsState: SettingsState) {
        with(settingsState) {
            val newSettings = overlaySettings?.copy(
                positionIndex = index,
            )

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onDisplaySelect(displayIndex: Int, settingsState: SettingsState) {
        with(settingsState) {
            val newSettings = overlaySettings?.copy(
                selectedDisplayIndex = displayIndex,
            )

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onCustomSensorSelect(sensor: SensorType, sensorId: String, settingsState: SettingsState) {
        with(settingsState) {
            val newSettings = when (sensor) {
                SensorType.CpuTemp -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        cpuTemp = overlaySettings.sensors.cpuTemp.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.CpuUsage -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        cpuUsage = overlaySettings.sensors.cpuUsage.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.GpuTemp -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        gpuTemp = overlaySettings.sensors.gpuTemp.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.GpuUsage -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        gpuUsage = overlaySettings.sensors.gpuUsage.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.VramUsage -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        vramUsage = overlaySettings.sensors.vramUsage.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.TotalVramUsed -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        totalVramUsed = overlaySettings.sensors.totalVramUsed.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.UpRate -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        upRate = overlaySettings.sensors.upRate.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.DownRate -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        downRate = overlaySettings.sensors.downRate.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.CpuConsumption -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        cpuConsumption = overlaySettings.sensors.cpuConsumption.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.GpuConsumption -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        gpuConsumption = overlaySettings.sensors.gpuConsumption.copy(
                            customReadingId = sensorId,
                        )
                    )
                )

                SensorType.Framerate -> overlaySettings
                SensorType.Frametime -> overlaySettings
                SensorType.RamUsage -> overlaySettings
                SensorType.NetGraph -> overlaySettings
            }

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onSwitchToggle(section: SectionType, isEnabled: Boolean, settingsState: SettingsState) {
        with(settingsState) {
            val newSettings = when (section) {
                SectionType.Fps -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        framerate = overlaySettings.sensors.framerate.copy(
                            isEnabled = isEnabled
                        ),
                        frametime = overlaySettings.sensors.frametime.copy(
                            isEnabled = isEnabled
                        )
                    )
                )

                SectionType.Gpu -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        gpuTemp = overlaySettings.sensors.gpuTemp.copy(
                            isEnabled = isEnabled
                        ),
                        gpuUsage = overlaySettings.sensors.gpuUsage.copy(
                            isEnabled = isEnabled
                        ),
                        vramUsage = overlaySettings.sensors.vramUsage.copy(
                            isEnabled = isEnabled
                        )
                    )
                )

                SectionType.Cpu -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        cpuTemp = overlaySettings.sensors.cpuTemp.copy(
                            isEnabled = isEnabled
                        ),
                        cpuUsage = overlaySettings.sensors.cpuUsage.copy(
                            isEnabled = isEnabled
                        )
                    )
                )

                SectionType.Ram -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        ramUsage = overlaySettings.sensors.ramUsage.copy(
                            isEnabled = isEnabled
                        )
                    )
                )

                SectionType.Network -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        upRate = overlaySettings.sensors.upRate.copy(
                            isEnabled = isEnabled
                        ),
                        downRate = overlaySettings.sensors.downRate.copy(
                            isEnabled = isEnabled
                        )
                    ),
                    netGraph = isEnabled
                )
            }

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }

    private fun onOptionsToggle(option: CheckboxSectionOption, currentState: SettingsState) {
        with(currentState) {
            val newSettings = when (option.type) {
                SensorType.Framerate -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        framerate = overlaySettings.sensors.framerate.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.Frametime -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        frametime = overlaySettings.sensors.frametime.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.CpuTemp -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        cpuTemp = overlaySettings.sensors.cpuTemp.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.CpuUsage -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        cpuUsage = overlaySettings.sensors.cpuUsage.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.GpuTemp -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        gpuTemp = overlaySettings.sensors.gpuTemp.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.GpuUsage -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        gpuUsage = overlaySettings.sensors.gpuUsage.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.VramUsage -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        vramUsage = overlaySettings.sensors.vramUsage.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.RamUsage -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        ramUsage = overlaySettings.sensors.ramUsage.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.UpRate -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        upRate = overlaySettings.sensors.upRate.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.DownRate -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        downRate = overlaySettings.sensors.downRate.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.TotalVramUsed -> overlaySettings // cant disable total vram used

                SensorType.NetGraph -> overlaySettings?.copy(netGraph = option.isSelected)

                SensorType.CpuConsumption -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        cpuConsumption = overlaySettings.sensors.cpuConsumption.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )

                SensorType.GpuConsumption -> overlaySettings?.copy(
                    sensors = overlaySettings.sensors.copy(
                        gpuConsumption = overlaySettings.sensors.gpuConsumption.copy(
                            isEnabled = option.isSelected
                        )
                    )
                )
            }

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }
}
