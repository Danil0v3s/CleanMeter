package br.com.firstsoft.target.server.ui.settings

import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import br.com.firstsoft.core.common.hwinfo.HwInfoData
import br.com.firstsoft.core.os.hwinfo.HwInfoReader
import br.com.firstsoft.target.server.data.OverlaySettingsRepository
import br.com.firstsoft.target.server.model.OverlaySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val overlaySettings: OverlaySettings? = null,
    val hwInfoData: HwInfoData? = null
)

sealed class SettingsEvent {
    data class OptionsToggle(val data: CheckboxSectionOption) : SettingsEvent()
    data class SwitchToggle(val section: SectionType, val isEnabled: Boolean) : SettingsEvent()
    data class CustomSensorSelect(val sensor: SensorType, val sensorId: Int) : SettingsEvent()
    data class DisplaySelect(val displayIndex: Int) : SettingsEvent()
    data class OverlayPositionIndexSelect(val index: Int) : SettingsEvent()
    data class OverlayCustomPositionSelect(val offset: IntOffset, val isPositionLocked: Boolean) : SettingsEvent()
    data class OverlayCustomPositionEnable(val isEnabled: Boolean) : SettingsEvent()
    data class OverlayOrientationSelect(val isHorizontal: Boolean) : SettingsEvent()
    data class OverlayOpacityChange(val opacity: Float) : SettingsEvent()
    data class OverlayGraphChange(val progressType: OverlaySettings.ProgressType) : SettingsEvent()
}

class SettingsViewModel : ViewModel() {

    private val _state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val state: Flow<SettingsState>
        get() = _state

    init {
        observeOverlaySettings()
        observeHwInfo()
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

    private fun observeHwInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            HwInfoReader.currentData.collectLatest { hwInfoData ->
                _state.update { it.copy(hwInfoData = hwInfoData) }
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

    private fun onCustomSensorSelect(sensor: SensorType, sensorId: Int, settingsState: SettingsState) {
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

                SensorType.Framerate -> overlaySettings
                SensorType.Frametime -> overlaySettings
                SensorType.VramUsage -> overlaySettings
                SensorType.RamUsage -> overlaySettings
                SensorType.UpRate -> overlaySettings
                SensorType.DownRate -> overlaySettings
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

                SensorType.NetGraph -> overlaySettings?.copy(netGraph = option.isSelected)
            }

            OverlaySettingsRepository.setOverlaySettings(newSettings)
        }
    }
}
