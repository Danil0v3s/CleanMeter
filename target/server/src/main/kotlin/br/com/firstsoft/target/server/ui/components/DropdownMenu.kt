package br.com.firstsoft.target.server.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.AlmostVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.LabelGray
import br.com.firstsoft.target.server.ui.ColorTokens.MutedGray

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
        TextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { }, modifier = Modifier.clearAndSetSemantics { }) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        "Trailing icon for exposed dropdown menu",
                        Modifier.rotate(
                            if (expanded)
                                270f
                            else
                                90f
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().border(1.dp, AlmostVisibleGray, RoundedCornerShape(16.dp)),
            textStyle = TextStyle(
                color = Color.DarkGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            shape = RoundedCornerShape(16.dp),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                trailingIconColor = MutedGray,
                focusedTrailingIconColor = MutedGray,
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
        ) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        selectedOption = item
                        onValueChanged(index)
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = item)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StealthDropdownMenu(
    options: List<String>,
    selectedIndex: Int,
    onValueChanged: (Int) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[selectedIndex]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth(),
    ) {
        TextField(
            leadingIcon = leadingIcon,
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = label,
            trailingIcon = {
                IconButton(onClick = { }, modifier = Modifier.clearAndSetSemantics { }) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        "Trailing icon for exposed dropdown menu",
                        Modifier.rotate(
                            if (expanded)
                                270f
                            else
                                90f
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = LabelGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 0.sp,
            ),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                trailingIconColor = MutedGray,
                focusedTrailingIconColor = MutedGray,
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
        ) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        selectedOption = item
                        onValueChanged(index)
                    }, modifier = Modifier.fillMaxWidth().height(24.dp)
                ) {
                    Text(
                        text = item,
                        color = DarkGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}