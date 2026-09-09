package com.digitalspeedometer.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = SpeedoAccent,
    onPrimary = SpeedoNavy,
    background = SpeedoNavy,
    onBackground = SpeedoOnDark,
    surface = SpeedoNavyLight,
    onSurface = SpeedoOnDark,
)

private val LightColors = lightColorScheme(
    primary = SpeedoNavy,
    onPrimary = SpeedoOnDark,
    background = SpeedoOnDark,
    onBackground = SpeedoNavy,
    surface = Color(0xFFFFFFFF),
    onSurface = SpeedoNavy,
)

@Composable
fun DigitalSpeedometerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
