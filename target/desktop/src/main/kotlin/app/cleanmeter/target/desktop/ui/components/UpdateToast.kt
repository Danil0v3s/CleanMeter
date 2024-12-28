package app.cleanmeter.target.desktop.ui.components

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.ui.AppTheme
import app.cleanmeter.updater.AutoUpdater
import app.cleanmeter.updater.UpdateState
import kotlinx.coroutines.delay

@Composable
internal fun BoxScope.UpdateToast() {
    val updaterState by AutoUpdater.state.collectAsState()

    val density = LocalDensity.current
    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(1000)
        visible = true
    }

    LaunchedEffect(updaterState) {
        if (updaterState is UpdateState.NotAvailable) {
            visible = false
        }
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
                .fillMaxWidth(0.8f)
                .background(LocalColorScheme.current.background.brand, RoundedCornerShape(100))
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .align(Alignment.BottomCenter)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconProgress(state = updaterState)
            BodyText(state = updaterState)
            CallToAction(
                state = updaterState,
                onCloseClick = { visible = false },
                onInstallClick = {
                    AutoUpdater.prepareForManualUpdate()
                },
                onUpdateClick = {
                    AutoUpdater.downloadUpdate()
                },
                onCancelDownloadClick = {
                    AutoUpdater.cancelDownload()
                },
            )
        }
    }
}

@Composable
private fun RowScope.BodyText(state: UpdateState) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = when (state) {
                is UpdateState.Available -> "New update available"
                is UpdateState.Downloaded -> "Update ready to install"
                is UpdateState.Downloading -> "Downloading update..."
                UpdateState.NotAvailable -> ""
            },
            color = LocalColorScheme.current.text.inverse,
            style = LocalTypography.current.labelLMedium,
            modifier = Modifier.wrapContentHeight(),
        )
        Text(
            text = "v${AutoUpdater.currentLiveVersion}",
            color = LocalColorScheme.current.text.disabled,
            style = LocalTypography.current.labelLMedium,
            modifier = Modifier.wrapContentHeight(),
        )
    }
}

@Composable
private fun ClearButton(onClick: () -> Unit, label: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
    ) {
        Text(
            text = label,
            style = LocalTypography.current.labelLMedium,
            color = LocalColorScheme.current.text.inverse,
            modifier = Modifier.wrapContentHeight(),
        )
    }
}

@Composable
private fun FilledButton(onClick: () -> Unit, label: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = LocalColorScheme.current.background.surfaceRaised,
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        shape = RoundedCornerShape(100),
    ) {
        Text(
            text = label,
            color = LocalColorScheme.current.text.heading,
            style = LocalTypography.current.labelLMedium,
            modifier = Modifier.wrapContentHeight(),
        )
    }
}

@Composable
private fun RowScope.CallToAction(
    onCloseClick: () -> Unit,
    onInstallClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onCancelDownloadClick: () -> Unit,
    state: UpdateState
) {
    when (state) {
        is UpdateState.Available -> {
            ClearButton(label = "Later", onClick = onCloseClick)
            FilledButton(label = "Update now", onClick = onUpdateClick)
        }

        is UpdateState.Downloaded -> {
            ClearButton(label = "Later", onClick = onCloseClick)
            FilledButton(label = "Install now", onClick = onInstallClick)
        }

        is UpdateState.Downloading -> {
            ClearButton(label = "Cancel", onClick = onCancelDownloadClick)
        }

        is UpdateState.NotAvailable -> Unit
    }
}

@Composable
private fun IconProgress(state: UpdateState) {
    Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        when (state) {
            is UpdateState.Available -> Icon(
                painter = painterResource("icons/cloud_download.svg"),
                contentDescription = null,
                tint = LocalColorScheme.current.border.inverse,
                modifier = Modifier.fillMaxSize().background(LocalColorScheme.current.background.brandSubtle, RoundedCornerShape(100)).padding(10.dp),
            )

            is UpdateState.Downloaded -> Icon(
                painter = painterResource("icons/download_done.svg"),
                contentDescription = null,
                tint = LocalColorScheme.current.border.inverse,
                modifier = Modifier.fillMaxSize().background(LocalColorScheme.current.background.brandSubtle, RoundedCornerShape(100)).padding(10.dp),
            )

            is UpdateState.Downloading -> CircularProgressIndicator(
                progress = state.progress,
                modifier = Modifier.fillMaxSize().align(Alignment.Center),
                color = LocalColorScheme.current.background.successHover,
                backgroundColor = LocalColorScheme.current.background.surfaceRaised.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round,
                strokeWidth = 3.dp
            )

            is UpdateState.NotAvailable -> Unit
        }
    }
}

@Preview
@Composable
private fun UpdateToastPreview() {
    AppTheme(false) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            UpdateToast()
        }
    }
}