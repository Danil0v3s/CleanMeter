package br.com.firstsoft.target.server.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.AlmostVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.MutedGray

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropdownSection(
    title: String,
    options: List<String>,
    onValueChanged: (Int) -> Unit,
    selectedIndex: Int,
) = Column(
    modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp)).padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[selectedIndex]) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = MutedGray,
            lineHeight = 0.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
    }

    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
}