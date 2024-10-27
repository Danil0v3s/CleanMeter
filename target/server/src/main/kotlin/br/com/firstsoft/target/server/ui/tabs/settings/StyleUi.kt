package br.com.firstsoft.target.server.ui.tabs.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.AlmostVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.BackgroundOffWhite
import br.com.firstsoft.target.server.ui.ColorTokens.BarelyVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.LabelGray
import br.com.firstsoft.target.server.ui.ColorTokens.MutedGray
import br.com.firstsoft.target.server.ui.components.CollapsibleSection
import br.com.firstsoft.target.server.ui.components.SliderThumb
import br.com.firstsoft.target.server.ui.components.Toggle
import br.com.firstsoft.target.server.ui.components.ToggleSection
import br.com.firstsoft.target.server.ui.components.coercedValueAsFraction
import br.com.firstsoft.target.server.ui.components.drawTrack
import ui.app.OverlaySettings
import ui.conditional

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StyleUi(
    overlaySettings: OverlaySettings,
    onOverlaySettings: (OverlaySettings) -> Unit,
    getOverlayPosition: () -> IntOffset,
) = Column(
    modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    onClick = { onOverlaySettings(overlaySettings.copy(positionIndex = 0)) },
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
                    onClick = { onOverlaySettings(overlaySettings.copy(positionIndex = 1)) },
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
                    onClick = { onOverlaySettings(overlaySettings.copy(positionIndex = 2)) },
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
                    onClick = { onOverlaySettings(overlaySettings.copy(positionIndex = 3)) },
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
                    onClick = { onOverlaySettings(overlaySettings.copy(positionIndex = 4)) },
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
                    onClick = { onOverlaySettings(overlaySettings.copy(positionIndex = 5)) },
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
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        onCheckedChange = { onOverlaySettings(overlaySettings.copy(positionIndex = if (it) 6 else 0, isPositionLocked = true)) },
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
                                    onOverlaySettings(
                                        overlaySettings.copy(
                                            isPositionLocked = !it,
                                            positionX = position.x,
                                            positionY = position.y
                                        )
                                    )
                                },
                                thumbContent = {
                                    val icon = if (!overlaySettings.isPositionLocked) {
                                        "icons/lock_open.svg"
                                    } else {
                                        "icons/drag_pan.svg"
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

    CollapsibleSection(title = "ORIENTATION") {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StyleCard(
                label = "Horizontal",
                isSelected = overlaySettings.isHorizontal,
                modifier = Modifier.weight(.5f),
                onClick = { onOverlaySettings(overlaySettings.copy(isHorizontal = true)) },
                content = {
                    Image(
                        painter = painterResource("icons/horizontal.png"),
                        contentDescription = "Horizontal image"
                    )
                }
            )

            StyleCard(
                label = "Vertical",
                isSelected = !overlaySettings.isHorizontal,
                modifier = Modifier.weight(.5f),
                onClick = { onOverlaySettings(overlaySettings.copy(isHorizontal = false)) },
                content = {
                    Image(
                        painter = painterResource("icons/vertical.png"),
                        contentDescription = "Vertical image",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            )
        }
    }

    CollapsibleSection(title = "OPACITY") {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Slider(
                value = overlaySettings.opacity,
                onValueChange = {
                    println(it)
                    onOverlaySettings(overlaySettings.copy(opacity = it.coerceIn(0f, 1f)))
                },
                steps = 9,
                track = { sliderState ->
                    val textMeasurer = rememberTextMeasurer()

                    Canvas(
                        Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        drawTrack(
                            FloatArray(sliderState.steps + 2) { it.toFloat() / (sliderState.steps + 1) },
                            0f,
                            sliderState.coercedValueAsFraction,
                            BackgroundOffWhite,
                            DarkGray,
                            AlmostVisibleGray,
                            Color.White,
                            textMeasurer
                        )
                    }
                },
                thumb = {
                    SliderThumb()
                }
            )
        }
    }

    ToggleSection(
        title = "GRAPH",
        isEnabled = overlaySettings.progressType != OverlaySettings.ProgressType.None,
        onSwitchToggle = {
            if (!it) {
                onOverlaySettings(overlaySettings.copy(progressType = OverlaySettings.ProgressType.None))
            } else {
                onOverlaySettings(overlaySettings.copy(progressType = OverlaySettings.ProgressType.Circular))
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StyleCard(
                label = "Ring graph",
                isSelected = overlaySettings.progressType == OverlaySettings.ProgressType.Circular,
                modifier = Modifier.weight(.5f),
                onClick = { onOverlaySettings(overlaySettings.copy(progressType = OverlaySettings.ProgressType.Circular)) },
                content = {
                    Image(
                        painter = painterResource("icons/rings.png"),
                        contentDescription = "Horizontal image"
                    )
                }
            )

            StyleCard(
                label = "Bar graph",
                isSelected = overlaySettings.progressType == OverlaySettings.ProgressType.Bar,
                modifier = Modifier.weight(.5f),
                onClick = { onOverlaySettings(overlaySettings.copy(progressType = OverlaySettings.ProgressType.Bar)) },
                content = {
                    Image(
                        painter = painterResource("icons/bars.png"),
                        contentDescription = "Vertical image",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            )
        }
    }
}

@Composable
private fun StyleCard(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    customLabel: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) = Column(
    modifier = modifier
        .aspectRatio(1.15f)
        .background(Color.White, RoundedCornerShape(8.dp))
        .conditional(
            predicate = isSelected,
            ifTrue = { border(2.dp, DarkGray, RoundedCornerShape(8.dp)) },
            ifFalse = { border(1.dp, AlmostVisibleGray, RoundedCornerShape(8.dp)) }
        )
        .padding(4.dp)
        .clickable { onClick() }
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .background(BarelyVisibleGray, RoundedCornerShape(4.dp))
            .padding(8.dp),
        content = content,
        contentAlignment = Alignment.Center
    )

    Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp)) {
        if (customLabel != null) {
            customLabel()
        } else {
            Text(
                text = label,
                fontSize = 14.sp,
                color = DarkGray,
                lineHeight = 0.sp,
                fontWeight = FontWeight(550),
                letterSpacing = 0.14.sp,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
    }
}

