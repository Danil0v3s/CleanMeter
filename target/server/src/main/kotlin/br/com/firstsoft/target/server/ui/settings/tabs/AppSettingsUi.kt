package br.com.firstsoft.target.server.ui.settings.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.firstsoft.target.server.data.PREFERENCE_START_MINIMIZED
import br.com.firstsoft.target.server.data.PreferencesRepository
import br.com.firstsoft.target.server.ui.components.CheckboxWithLabel
import br.com.firstsoft.target.server.ui.components.Label
import br.com.firstsoft.target.server.ui.components.Section
import br.com.firstsoft.target.server.ui.settings.FooterUi
import br.com.firstsoft.core.os.win32.WinRegistry

@Composable
fun AppSettingsUi() = Box(modifier = Modifier.fillMaxSize().padding(top = 20.dp)) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Section(
            title = "GENERAL",
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                startWithWindowsCheckbox()
                startMinimizedCheckbox()
            }
        }
    }

    FooterUi(modifier = Modifier.align(Alignment.BottomStart))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun startWithWindowsCheckbox() {
    var state by remember { mutableStateOf(WinRegistry.isAppRegisteredToStartWithWindows()) }

    CheckboxWithLabel(
        label = "Start with Windows",
        checked = state,
        onCheckedChange = { value ->
            state = value
            if (value) {
                WinRegistry.registerAppToStartWithWindows()
            } else {
                WinRegistry.removeAppFromStartWithWindows()
            }
        }
    ) {
        TooltipArea({
            Label(text = "Admin rights needed")
        }) {
            Icon(imageVector = Icons.Filled.AdminPanelSettings, null)
        }
    }
}

@Composable
private fun startMinimizedCheckbox() {
    var state by remember { mutableStateOf(PreferencesRepository.getPreferenceBoolean(PREFERENCE_START_MINIMIZED)) }
    CheckboxWithLabel(
        label = "Start Minimized",
        checked = state,
        onCheckedChange = { value ->
            state = value
            PreferencesRepository.setPreferenceBoolean(PREFERENCE_START_MINIMIZED, value)
        },
    )
}
