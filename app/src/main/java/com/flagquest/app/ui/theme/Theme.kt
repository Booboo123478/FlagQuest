package com.flagquest.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4FC3F7),
    onPrimary = Color(0xFF003546),
    primaryContainer = Color(0xFF004D64),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF3E2000),
    background = Color(0xFF0D1B2A),
    surface = Color(0xFF1A2C3D),
    onBackground = Color(0xFFE1F5FE),
    onSurface = Color(0xFFE1F5FE),
    error = Color(0xFFEF5350)
)

@Composable
fun FlagQuestTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}
