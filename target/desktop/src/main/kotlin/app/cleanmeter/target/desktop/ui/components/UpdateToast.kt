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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.target.desktop.ui.AppTheme
import app.cleanmeter.target.desktop.ui.ColorTokens.AlmostDarkGray
import app.cleanmeter.target.desktop.ui.ColorTokens.ClearGray
import app.cleanmeter.target.desktop.ui.ColorTokens.DarkGray
import app.cleanmeter.target.desktop.ui.ColorTokens.Green
import app.cleanmeter.target.desktop.ui.ColorTokens.LabelGray
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
                .background(DarkGray, RoundedCornerShape(100))
                .padding(16.dp)
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
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 0.sp,
            modifier = Modifier.padding(bottom = 2.5.dp),
        )
        Text(
            text = "v${AutoUpdater.currentLiveVersion}",
            color = LabelGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 0.sp,
            modifier = Modifier.padding(bottom = 2.5.dp),
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
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 2.5.dp),
        )
    }
}

@Composable
private fun FilledButton(onClick: () -> Unit, label: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.White,
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        shape = RoundedCornerShape(100),
    ) {
        Text(
            text = label,
            color = Color.DarkGray,
            fontSize = 14.sp,
            fontWeight = FontWeight(600),
            modifier = Modifier.padding(bottom = 2.5.dp),
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
                tint = Color.White,
                modifier = Modifier.fillMaxSize().background(AlmostDarkGray, RoundedCornerShape(100)).padding(10.dp),
            )

            is UpdateState.Downloaded -> Icon(
                painter = painterResource("icons/download_done.svg"),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.fillMaxSize().background(AlmostDarkGray, RoundedCornerShape(100)).padding(10.dp),
            )

            is UpdateState.Downloading -> CircularProgressIndicator(
                progress = state.progress,
                modifier = Modifier.fillMaxSize().align(Alignment.Center),
                color = Green,
                backgroundColor = ClearGray,
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