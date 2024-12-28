package app.cleanmeter.target.desktop.ui.settings.tabs.stats

import androidx.compose.runtime.Composable
import app.cleanmeter.target.desktop.ui.components.section.CheckboxSection
import app.cleanmeter.target.desktop.ui.settings.CheckboxSectionOption
import app.cleanmeter.target.desktop.ui.settings.SectionType
import app.cleanmeter.target.desktop.ui.settings.SensorType

@Composable
internal fun RamStats(
    availableOptions: List<CheckboxSectionOption>,
    onSectionSwitchToggle: (SectionType, Boolean) -> Unit,
    onOptionsToggle: (CheckboxSectionOption) -> Unit,
) {
    CheckboxSection(
        title = "RAM",
        options = availableOptions.filterOptions(SensorType.RamUsage),
        onOptionToggle = onOptionsToggle,
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Ram, it) }
    )
}