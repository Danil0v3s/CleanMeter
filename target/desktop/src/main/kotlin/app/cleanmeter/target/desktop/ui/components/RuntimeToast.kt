package app.cleanmeter.target.desktop.ui.components

import ClearButton
import FilledButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.ui.AppTheme
import kotlinx.coroutines.delay
import kotlin.system.exitProcess

@Composable
internal fun BoxScope.RuntimeToast() {
    val uriHandler = LocalUriHandler.current
    val density = LocalDensity.current
    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(1000)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically {
            with(density) { 40.dp.roundToPx() }
        },
        exit = slideOutVertically {
            with(density) { 40.dp.roundToPx() }
        },
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(0.95f)
                .background(LocalColorScheme.current.background.brand, RoundedCornerShape(100))
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .align(Alignment.BottomCenter)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = LocalColorScheme.current.icon.inverse,
                    modifier = Modifier.fillMaxSize().background(LocalColorScheme.current.background.brandSubtle, RoundedCornerShape(100)).padding(10.dp),
                )
            }
            BodyText()
            CallToAction(
                onCloseClick = { exitProcess(0) },
                onUpdateClick = {
                    uriHandler.openUri("https://dotnet.microsoft.com/en-us/download/dotnet/thank-you/runtime-desktop-8.0.11-windows-x64-installer")
                    exitProcess(0)
                },
            )
        }
    }
}

@Composable
private fun RowScope.BodyText() {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = ".NET Core Runtime not available",
            color = LocalColorScheme.current.text.inverse,
            style = LocalTypography.current.labelLMedium,
            modifier = Modifier.wrapContentHeight(),
        )
        Text(
            text = "Please re-open CleanMeter after the installation.",
            color = LocalColorScheme.current.text.disabled,
            style = LocalTypography.current.labelLMedium,
            modifier = Modifier.wrapContentHeight(),
        )
    }
}



@Composable
private fun RowScope.CallToAction(
    onCloseClick: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    ClearButton(label = "Later", onClick = onCloseClick)
    FilledButton(label = "Install now", onClick = onUpdateClick)
}

@Preview
@Composable
private fun RuntimeToastPreview() {
    AppTheme(false) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            RuntimeToast()
        }
    }
}