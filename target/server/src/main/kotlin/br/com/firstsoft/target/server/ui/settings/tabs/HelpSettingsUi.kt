package br.com.firstsoft.target.server.ui.settings.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.firstsoft.target.server.ui.components.CollapsibleSection

@Composable
internal fun HelpSettingsUi() {
    Column(
        modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CollapsibleSection(
            title = "SHORCUTS",
        ) {
            Text("fasdadas")
        }

        CollapsibleSection(
            title = "HOW TO SETUP",
        ) {
            Text("fasdadas")
        }

        CollapsibleSection(
            title = "CURRENT LIMITATIONS",
        ) {
            Text("fasdadas")
        }

        CollapsibleSection(
            title = "FREQUENTLY ASKED QUESTIONS",
        ) {
            Text("fasdadas")
        }
    }
}