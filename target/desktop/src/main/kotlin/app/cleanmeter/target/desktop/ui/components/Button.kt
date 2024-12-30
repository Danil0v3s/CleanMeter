import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography

@Composable
internal fun ClearButton(
    label: String,
    textColor: Color = LocalColorScheme.current.text.inverse,
    onClick: () -> Unit
) {
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
            color = textColor,
            modifier = Modifier.wrapContentHeight(),
        )
    }
}

@Composable
internal fun FilledButton(
    label: String,
    containerColor: Color = LocalColorScheme.current.background.surfaceRaised,
    textColor: Color = LocalColorScheme.current.text.heading,
    textStyle: TextStyle = LocalTypography.current.labelLMedium,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = containerColor,
        ),
        contentPadding = contentPadding,
        shape = RoundedCornerShape(100),
    ) {
        Text(
            text = label,
            color = textColor,
            style = textStyle,
            modifier = Modifier.wrapContentHeight(),
        )
    }
}