package app.cleanmeter.target.desktop.ui.settings.tabs.style

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.StyleCard
import app.cleanmeter.target.desktop.ui.components.Toggle
import app.cleanmeter.target.desktop.ui.components.section.CollapsibleSection

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Position(
    overlaySettings: OverlaySettings,
    onOverlayPositionIndex: (Int) -> Unit,
    onOverlayCustomPosition: (IntOffset, Boolean) -> Unit,
    onOverlayCustomPositionEnable: (Boolean) -> Unit,
    getOverlayPosition: () -> IntOffset
) {
    CollapsibleSection(title = "POSITION") {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            FlowRow(
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StyleCard(
                    label = "Top left",
                    isSelected = overlaySettings.positionIndex == 0,
                    modifier = Modifier.weight(.3f).aspectRatio(1.15f),
                    onClick = { onOverlayPositionIndex(0) },
                    content = {
                        PositionMarker(
                            isSelected = overlaySettings.positionIndex == 0,
                            alignment = Alignment.TopStart,
                        )
                    }
                )

                StyleCard(
                    label = "Top middle",
                    isSelected = overlaySettings.positionIndex == 1,
                    modifier = Modifier.weight(.3f).aspectRatio(1.15f),
                    onClick = { onOverlayPositionIndex(1) },
                    content = {
                        PositionMarker(
                            isSelected = overlaySettings.positionIndex == 1,
                            alignment = Alignment.TopCenter,
                        )
                    }
                )

                StyleCard(
                    label = "Top right",
                    isSelected = overlaySettings.positionIndex == 2,
                    modifier = Modifier.weight(.3f).aspectRatio(1.15f),
                    onClick = { onOverlayPositionIndex(2) },
                    content = {
                        PositionMarker(
                            isSelected = overlaySettings.positionIndex == 2,
                            alignment = Alignment.TopEnd,
                        )
                    }
                )

                StyleCard(
                    label = "Bottom left",
                    isSelected = overlaySettings.positionIndex == 3,
                    modifier = Modifier.weight(.3f).aspectRatio(1.15f),
                    onClick = { onOverlayPositionIndex(3) },
                    content = {
                        PositionMarker(
                            isSelected = overlaySettings.positionIndex == 3,
                            alignment = Alignment.BottomStart,
                        )
                    }
                )

                StyleCard(
                    label = "Bottom middle",
                    isSelected = overlaySettings.positionIndex == 4,
                    modifier = Modifier.weight(.3f).aspectRatio(1.15f),
                    onClick = { onOverlayPositionIndex(4) },
                    content = {
                        PositionMarker(
                            isSelected = overlaySettings.positionIndex == 4,
                            alignment = Alignment.BottomCenter,
                        )
                    }
                )

                StyleCard(
                    label = "Bottom right",
                    isSelected = overlaySettings.positionIndex == 5,
                    modifier = Modifier.weight(.3f).aspectRatio(1.15f),
                    onClick = { onOverlayPositionIndex(5) },
                    content = {
                        PositionMarker(
                            isSelected = overlaySettings.positionIndex == 5,
                            alignment = Alignment.BottomEnd,
                        )
                    }
                )
            }

            Divider()

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(40.dp).border(1.dp, LocalColorScheme.current.border.bold, CircleShape).padding(10.dp),
                            painter = painterResource("icons/drag_pan.svg"),
                            tint = LocalColorScheme.current.icon.bolderActive,
                            contentDescription = "",
                        )
                        Column {
                            Text(
                                text = "Use custom position",
                                style = LocalTypography.current.labelM,
                                color = LocalColorScheme.current.text.heading,
                            )
                            Text(
                                text = "Unlock to move around the overlay, lock it again to fix it's position.",
                                style = LocalTypography.current.labelS,
                                color = LocalColorScheme.current.text.paragraph1,
                            )
                        }
                    }
                    Toggle(
                        checked = overlaySettings.positionIndex == 6,
                        onCheckedChange = {
                            onOverlayCustomPositionEnable(it)
                        },
                    )
                }

                if (overlaySettings.positionIndex == 6) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LocalColorScheme.current.background.surfaceSunkenSubtle, RoundedCornerShape(12.dp))
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Locked",
                                style = LocalTypography.current.labelLMedium,
                                color = if (!overlaySettings.isPositionLocked) LocalColorScheme.current.text.disabledLighter else LocalColorScheme.current.text.heading,
                            )
                            Toggle(
                                customSize = true,
                                checked = !overlaySettings.isPositionLocked,
                                checkedTrackColor = LocalColorScheme.current.background.brand,
                                onCheckedChange = {
                                    val position = getOverlayPosition()
                                    onOverlayCustomPosition(IntOffset(position.x, position.y), !it)
                                },
                                thumbContent = {
                                    val icon = if (!overlaySettings.isPositionLocked) {
                                        "icons/lock_open.svg"
                                    } else {
                                        "icons/lock_closed.svg"
                                    }
                                    Icon(
                                        painter = painterResource(icon),
                                        contentDescription = "",
                                        tint = if (overlaySettings.isPositionLocked) LocalColorScheme.current.icon.bolderActive else LocalColorScheme.current.border.brand,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            )
                            Text(
                                text = "Unlocked",
                                style = LocalTypography.current.labelLMedium,
                                color = if (!overlaySettings.isPositionLocked) LocalColorScheme.current.text.heading else LocalColorScheme.current.text.disabledLighter,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.PositionMarker(
    isSelected: Boolean,
    alignment: Alignment,
) {
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(20.dp)
            .background(
                if (isSelected) LocalColorScheme.current.background.brand else LocalColorScheme.current.background.surfaceSunken,
                RoundedCornerShape(50)
            )
            .align(alignment)
    )
}