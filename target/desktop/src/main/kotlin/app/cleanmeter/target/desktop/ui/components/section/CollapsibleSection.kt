package app.cleanmeter.target.desktop.ui.components.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import app.cleanmeter.target.desktop.ui.components.SectionTitle

@Composable
fun CollapsibleSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    SectionBody {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { expanded = !expanded }
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(title = title)
            IconButton(onClick = { expanded = !expanded }, modifier = Modifier.clearAndSetSemantics { }.height(20.dp)) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Trailing icon for exposed dropdown menu",
                    modifier = Modifier
                        .rotate(if (expanded) 270f else 90f)
                        .height(20.dp)
                )
            }
        }

        if (expanded) {
            content()
        }
    }
}