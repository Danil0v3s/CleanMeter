package br.com.firstsoft.target.server.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Title(text: String) = Text(
    modifier = Modifier.padding(bottom = 8.dp),
    text = text,
    style = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    )
)
