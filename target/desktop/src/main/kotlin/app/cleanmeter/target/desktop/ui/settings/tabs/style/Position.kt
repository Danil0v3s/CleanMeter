package app.cleanmeter.target.desktop.ui.settings.tabs.style

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.ColorTokens.AlmostVisibleGray
import app.cleanmeter.target.desktop.ui.ColorTokens.BarelyVisibleGray
import app.cleanmeter.target.desktop.ui.ColorTokens.DarkGray
import app.cleanmeter.target.desktop.ui.ColorTokens.LabelGray
import app.cleanmeter.target.desktop.ui.ColorTokens.MutedGray
import app.cleanmeter.target.desktop.ui.components.CollapsibleSection
import app.cleanmeter.target.desktop.ui.components.StyleCard
import app.cleanmeter.target.desktop.ui.components.Toggle

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
                    modifier = Modifier.weight(.3f),
                    onClick = { onOverlayPositionIndex(0) },
                    content = {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                                .background(
                                    if (overlaySettings.positionIndex == 0) DarkGray else MutedGray,
                                    RoundedCornerShape(50)
                                )
                                .align(Alignment.TopStart)
                        )
                    }
                )

                StyleCard(
                    label = "Top middle",
                    isSelected = overlaySettings.positionIndex == 1,
                    modifier = Modifier.weight(.3f),
                    onClick = { onOverlayPositionIndex(1) },
                    content = {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                                .background(
                                    if (overlaySettings.positionIndex == 1) DarkGray else MutedGray,
                                    RoundedCornerShape(50)
                                )
                                .align(Alignment.TopCenter)
                        )
                    }
                )

                StyleCard(
                    label = "Top right",
                    isSelected = overlaySettings.positionIndex == 2,
                    modifier = Modifier.weight(.3f),
                    onClick = { onOverlayPositionIndex(2) },
                    content = {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                                .background(
                                    if (overlaySettings.positionIndex == 2) DarkGray else MutedGray,
                                    RoundedCornerShape(50)
                                )
                                .align(Alignment.TopEnd)
                        )
                    }
                )

                StyleCard(
                    label = "Bottom left",
                    isSelected = overlaySettings.positionIndex == 3,
                    modifier = Modifier.weight(.3f),
                    onClick = { onOverlayPositionIndex(3) },
                    content = {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                                .background(
                                    if (overlaySettings.positionIndex == 3) DarkGray else MutedGray,
                                    RoundedCornerShape(50)
                                )
                                .align(Alignment.BottomStart)
                        )
                    }
                )

                StyleCard(
                    label = "Bottom middle",
                    isSelected = overlaySettings.positionIndex == 4,
                    modifier = Modifier.weight(.3f),
                    onClick = { onOverlayPositionIndex(4) },
                    content = {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                                .background(
                                    if (overlaySettings.positionIndex == 4) DarkGray else MutedGray,
                                    RoundedCornerShape(50)
                                )
                                .align(Alignment.BottomCenter)
                        )
                    }
                )

                StyleCard(
                    label = "Bottom right",
                    isSelected = overlaySettings.positionIndex == 5,
                    modifier = Modifier.weight(.3f),
                    onClick = { onOverlayPositionIndex(5) },
                    content = {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                                .background(
                                    if (overlaySettings.positionIndex == 5) DarkGray else MutedGray,
                                    RoundedCornerShape(50)
                                )
                                .align(Alignment.BottomEnd)
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
                            modifier = Modifier.size(40.dp).border(1.dp, LabelGray, CircleShape).padding(10.dp),
                            painter = painterResource("icons/drag_pan.svg"),
                            tint = MutedGray,
                            contentDescription = "",
                        )
                        Column {
                            Text(
                                text = "Use custom position",
                                fontSize = 13.sp,
                                color = DarkGray,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                            )
                            Text(
                                text = "Unlock to move around the overlay, lock it again to fix it's position.",
                                fontSize = 12.sp,
                                color = LabelGray,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
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
                            .background(BarelyVisibleGray, RoundedCornerShape(12.dp))
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Locked",
                                fontSize = 13.sp,
                                color = if (!overlaySettings.isPositionLocked) AlmostVisibleGray else DarkGray,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                            )
                            Toggle(
                                customSize = true,
                                checked = !overlaySettings.isPositionLocked,
                                checkedTrackColor = DarkGray,
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
                                    Icon(painterResource(icon), "")
                                }
                            )
                            Text(
                                text = "Unlocked",
                                fontSize = 13.sp,
                                color = if (!overlaySettings.isPositionLocked) DarkGray else AlmostVisibleGray,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }
    }
}