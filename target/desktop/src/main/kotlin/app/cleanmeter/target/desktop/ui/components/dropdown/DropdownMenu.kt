package app.cleanmeter.target.desktop.ui.components.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropdownMenu(
    options: List<String>,
    selectedIndex: Int,
    onValueChanged: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[selectedIndex]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LocalColorScheme.current.border.bolder, RoundedCornerShape(8.dp))
                .background(LocalColorScheme.current.background.surfaceRaised).padding(12.dp)
        ) {
            Text(
                text = selectedOption,
                color = LocalColorScheme.current.text.heading,
                style = LocalTypography.current.labelL,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            IconButton(onClick = { }, modifier = Modifier.size(20.dp).clearAndSetSemantics { }) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Trailing icon for exposed dropdown menu",
                    tint = LocalColorScheme.current.icon.bolderActive,
                    modifier = Modifier.rotate(
                        if (expanded) 270f
                        else 90f
                    )
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(LocalColorScheme.current.background.surfaceRaised),
        ) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        selectedOption = item
                        onValueChanged(index)
                    },
                ) {
                    Text(
                        text = item,
                        style = LocalTypography.current.labelLMedium,
                        color = LocalColorScheme.current.text.heading,
                    )
                }
            }
        }
    }
}
