package app.cleanmeter.target.desktop.ui.components.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.cleanmeter.target.desktop.ui.components.dropdown.DropdownMenu
import app.cleanmeter.target.desktop.ui.components.SectionTitle

@Composable
fun DropdownSection(
    title: String,
    options: List<String>,
    onValueChanged: (Int) -> Unit,
    selectedIndex: Int,
) = SectionBody {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionTitle(title = title)
    }

    DropdownMenu(
        options = options,
        selectedIndex = selectedIndex,
        onValueChanged = onValueChanged,
    )
}