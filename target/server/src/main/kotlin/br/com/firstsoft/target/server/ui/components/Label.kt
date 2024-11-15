package br.com.firstsoft.target.server.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun Label(
    text: String,
    style: TextStyle = MaterialTheme.typography.subtitle2.copy(lineHeight = 0.sp),
) = Text(
    text = text,
    style = style
)
