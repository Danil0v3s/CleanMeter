package br.com.firstsoft.target.server.ui.settings.tabs.overlaysettings.sections

import br.com.firstsoft.target.server.ui.settings.CheckboxSectionOption
import br.com.firstsoft.target.server.ui.settings.SettingsOptionType

internal fun List<CheckboxSectionOption>.filterOptions(vararg optionType: SettingsOptionType) =
    this.filter { source -> optionType.any { it == source.type } }