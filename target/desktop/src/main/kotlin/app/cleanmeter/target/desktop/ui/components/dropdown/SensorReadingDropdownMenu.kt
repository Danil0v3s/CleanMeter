package app.cleanmeter.target.desktop.ui.components.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.ui.ColorTokens.AlmostVisibleGray
import app.cleanmeter.target.desktop.ui.ColorTokens.BarelyVisibleGray
import app.cleanmeter.target.desktop.ui.ColorTokens.DarkGray
import app.cleanmeter.target.desktop.ui.ColorTokens.Gray200
import app.cleanmeter.target.desktop.ui.ColorTokens.MutedGray
import app.cleanmeter.target.desktop.ui.overlay.conditional

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SensorReadingDropdownMenu(
    options: List<HardwareMonitorData.Sensor>,
    selectedIndex: Int,
    onValueChanged: (HardwareMonitorData.Sensor) -> Unit,
    label: String? = null,
    sensorName: String,
    dropdownLabel: (HardwareMonitorData.Sensor) -> String = { "${it.Name} (${it.Value} - ${it.SensorType})" },
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[selectedIndex]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
    ) {
        DropdownContent(
            label = label,
            selectedOption = selectedOption,
            expanded = expanded,
            options = options,
            sensorName = sensorName,
            onValueChanged = {
                expanded = false
                selectedOption = it
                onValueChanged(it)
            },
            selectedIndex = selectedIndex,
            dropdownLabel = dropdownLabel,
            onDismissRequest = { expanded = false },
        )
    }
}

@Composable
private fun DropdownContent(
    label: String?,
    expanded: Boolean,
    sensorName: String,
    selectedIndex: Int,
    selectedOption: HardwareMonitorData.Sensor,
    options: List<HardwareMonitorData.Sensor>,
    onValueChanged: (HardwareMonitorData.Sensor) -> Unit,
    dropdownLabel: (HardwareMonitorData.Sensor) -> String,
    onDismissRequest: () -> Unit,
) {
    val colorScheme = LocalColorScheme.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = 12.dp)
            .drawWithCache {
                onDrawWithContent {
                    drawRoundRect(
                        color = colorScheme.border.subtle,
                        cornerRadius = CornerRadius(100f, 100f),
                        size = Size(2f, (size.height - 16f).coerceAtLeast(0f)),
                        topLeft = Offset(0f, 16f)
                    )
                    drawContent()
                }
            }
            .padding(start = 20.dp, top = 16.dp)
            .fillMaxWidth()
            .background(LocalColorScheme.current.background.surfaceSunkenSubtle, RoundedCornerShape(8.dp)).padding(16.dp)
            .border(1.dp, LocalColorScheme.current.border.bolder, RoundedCornerShape(8.dp))
            .background(LocalColorScheme.current.background.surfaceRaised).padding(12.dp)

    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (label != null) {
                Text(
                    text = label,
                    color = LocalColorScheme.current.text.paragraph1,
                    style = LocalTypography.current.labelL,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Text(
                text = "${selectedOption.Name} - ${selectedOption.SensorType}",
                color = LocalColorScheme.current.text.heading,
                style = LocalTypography.current.labelL,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

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

    if (expanded) {
        Popup(
            onDismissRequest = onDismissRequest, popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize
                ): IntOffset {
                    return IntOffset(0, 56)
                }

            }, properties = PopupProperties(
                clippingEnabled = true,
                dismissOnBackPress = true,
                focusable = true,
            )
        ) {
            var filteredItems by remember { mutableStateOf(options) }
            Surface(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                            .border(1.dp, LocalColorScheme.current.border.bolder, RoundedCornerShape(12.dp))
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                            .background(LocalColorScheme.current.background.surfaceRaised, RoundedCornerShape(12.dp))
                            .size(650.dp, 400.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Header(
                            label = sensorName,
                            onCloseRequest = onDismissRequest,
                            onFilterChange = { filter ->
                                filteredItems = if (filter.isNotEmpty()) {
                                    options.filter {
                                        it.SensorType.name.contains(filter, true) || it.Name.contains(filter, true)
                                    }
                                } else {
                                    options
                                }
                            })

                        LazyColumn(
                            modifier = Modifier.size(650.dp, 400.dp).padding(horizontal = 24.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            filteredItems.forEachIndexed { index, item ->
                                item {
                                    Row(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .fillMaxWidth()
                                            .clickable {
                                                onValueChanged(item)
                                            }
                                            .conditional(
                                                predicate = index == selectedIndex,
                                                ifTrue = {
                                                    background(
                                                        LocalColorScheme.current.background.surfaceSunkenSubtle,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                })
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = dropdownLabel(item),
                                            style = LocalTypography.current.labelLMedium,
                                            color = LocalColorScheme.current.text.heading,
                                        )

                                        if (index == selectedIndex) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = "Trailing icon for exposed dropdown menu",
                                                tint = LocalColorScheme.current.icon.bolderActive,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    label: String, onCloseRequest: () -> Unit, onFilterChange: (String) -> Unit
) {
    var filter by remember { mutableStateOf("") }
    val colorScheme = LocalColorScheme.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val y = size.height - 4
                drawLine(
                    color = colorScheme.border.subtle,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )
            }
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Select $label sensor",
                style = LocalTypography.current.titleM,
                color = LocalColorScheme.current.text.heading,
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = onCloseRequest, modifier = Modifier.size(20.dp).clearAndSetSemantics { }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Trailing icon for exposed dropdown menu",
                    tint = LocalColorScheme.current.icon.bolderActive,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(LocalColorScheme.current.background.surfaceRaised, RoundedCornerShape(8.dp))
                .border(1.dp, LocalColorScheme.current.border.bolder, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            IconButton(onClick = {}, modifier = Modifier.size(20.dp).clearAndSetSemantics { }) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Leading icon for search box",
                    tint = LocalColorScheme.current.icon.bolderActive,
                )
            }

            BasicTextField(
                value = filter,
                textStyle = LocalTypography.current.labelS.copy(
                    lineHeight = 2.sp,
                    textAlign = TextAlign.Start,
                ),
                singleLine = true,
                onValueChange = {
                    filter = it
                    onFilterChange(it)
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}