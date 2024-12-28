package app.cleanmeter.target.desktop.ui.settings.tabs.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cleanmeter.target.desktop.ui.components.CheckboxWithLabel
import app.cleanmeter.target.desktop.ui.components.dropdown.DropdownMenu
import app.cleanmeter.target.desktop.ui.components.section.CustomBodyCheckboxSection
import app.cleanmeter.target.desktop.ui.settings.CheckboxSectionOption
import app.cleanmeter.target.desktop.ui.settings.SectionType
import app.cleanmeter.target.desktop.ui.settings.SensorType

@Composable
internal fun FpsStats(
    availableOptions: List<CheckboxSectionOption>,
    onSectionSwitchToggle: (SectionType, Boolean) -> Unit,
    onOptionsToggle: (CheckboxSectionOption) -> Unit,
    onFpsApplicationSelect: (String) -> Unit,
    getPresentMonApps: () -> List<String>,
) {
    CustomBodyCheckboxSection(
        title = "FPS",
        options = availableOptions.filterOptions(SensorType.Framerate, SensorType.Frametime),
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Fps, it) },
        body = { options ->
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    CheckboxWithLabel(
                        label = option.name,
                        enabled = option.useCheckbox,
                        onCheckedChange = { onOptionsToggle(option.copy(isSelected = !option.isSelected)) },
                        checked = option.isSelected,
                    )
                }
                val presentMonApps = getPresentMonApps()
                if (presentMonApps.isNotEmpty()) {
                    DropdownMenu(
                        label = "Monitored app:",
                        disclaimer = "Apps are auto updated every 10 seconds.",
                        options = presentMonApps,
                        selectedIndex = 0,
                        onValueChanged = { onFpsApplicationSelect(presentMonApps[it]) },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    )
}