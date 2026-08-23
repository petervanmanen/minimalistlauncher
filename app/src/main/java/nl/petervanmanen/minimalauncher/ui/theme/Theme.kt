package nl.petervanmanen.minimalauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PureBlack = Color(0xFF000000)
val PureWhite = Color(0xFFFFFFFF)
val DimWhite = Color(0xFF9A9A9A)

private val LauncherColorScheme = darkColorScheme(
    background = PureBlack,
    surface = PureBlack,
    onBackground = PureWhite,
    onSurface = PureWhite,
    primary = PureWhite,
    onPrimary = PureBlack,
)

private val LauncherTypography = Typography(
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        color = PureWhite,
    ),
)

@Composable
fun MinimalLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LauncherColorScheme,
        typography = LauncherTypography,
        content = content,
    )
}
