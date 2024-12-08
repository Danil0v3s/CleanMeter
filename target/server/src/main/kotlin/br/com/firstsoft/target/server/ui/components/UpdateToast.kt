package br.com.firstsoft.target.server.ui.components

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
import br.com.firstsoft.target.server.ui.AppTheme
import br.com.firstsoft.target.server.ui.ColorTokens.AlmostDarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.ClearGray
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.Green
import br.com.firstsoft.target.server.ui.ColorTokens.LabelGray
import br.com.firstsoft.updater.AutoUpdater
import kotlinx.coroutines.delay
import java.io.File

@Composable
internal fun BoxScope.UpdateToast() {
    val updateProgress by AutoUpdater.progress.collectAsState()
    val isUpdating by AutoUpdater.isUpdating.collectAsState()
    val isUpdateAvailable by AutoUpdater.isUpdateAvailable.collectAsState()
    var updateArchive by remember { mutableStateOf<File?>(null) }
    var hasFinishedDownloading by remember { mutableStateOf(false) }
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
                .fillMaxWidth(0.8f)
                .background(DarkGray, RoundedCornerShape(100))
                .padding(16.dp)
                .align(Alignment.BottomCenter)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconProgress(
                hasFinishedDownloading = hasFinishedDownloading,
                isUpdating = isUpdating,
                isUpdateAvailable = isUpdateAvailable,
                updateProgress = updateProgress
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "New update available",
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

            CallToAction(
                hasFinishedDownloading = hasFinishedDownloading,
                isUpdating = isUpdating,
                isUpdateAvailable = isUpdateAvailable,
                updateProgress = updateProgress,
                onCloseClick = { visible = false },
                onInstallClick = {
                    AutoUpdater.prepareForManualUpdate(updateArchive)
                },
                onUpdateClick = {
                    AutoUpdater.downloadUpdate {
                        updateArchive = it
                        hasFinishedDownloading = true
                    }
                },
                onCancelDownloadClick = {
                    AutoUpdater.cancelDownload()
                },
            )
        }
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
private fun CallToAction(
    onCloseClick: () -> Unit,
    onInstallClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onCancelDownloadClick: () -> Unit,
    hasFinishedDownloading: Boolean,
    isUpdating: Boolean,
    isUpdateAvailable: Boolean,
    updateProgress: Float
) {

    when {
        !isUpdating && isUpdateAvailable && hasFinishedDownloading -> {
            ClearButton(label = "Later", onClick = onCloseClick)
            FilledButton(label = "Install now", onClick = onInstallClick)
        }
        !isUpdating && isUpdateAvailable && !hasFinishedDownloading -> {
            ClearButton(label = "Later", onClick = onCloseClick)
            FilledButton(label = "Update now", onClick = onUpdateClick)
        }
        isUpdating && updateProgress in 0f..<1f -> {
            ClearButton(label = "Cancel", onClick = onCancelDownloadClick)
        }
    }
}

@Composable
private fun IconProgress(
    hasFinishedDownloading: Boolean,
    isUpdating: Boolean,
    isUpdateAvailable: Boolean,
    updateProgress: Float
) {
    Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        when {
            !isUpdating && isUpdateAvailable && hasFinishedDownloading -> {
                Icon(
                    painter = painterResource("icons/download_done.svg"),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize().background(AlmostDarkGray, RoundedCornerShape(100)).padding(10.dp),
                )
            }
            !isUpdating && isUpdateAvailable && !hasFinishedDownloading -> {
                Icon(
                    painter = painterResource("icons/cloud_download.svg"),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize().background(AlmostDarkGray, RoundedCornerShape(100)).padding(10.dp),
                )
            }
            isUpdating && updateProgress in 0f..<1f -> {
                CircularProgressIndicator(
                    progress = updateProgress,
                    modifier = Modifier.fillMaxSize().align(Alignment.Center),
                    color = Green,
                    backgroundColor = ClearGray,
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Preview
@Composable
private fun UpdateToastPreview() {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            UpdateToast()
        }
    }
}