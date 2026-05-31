package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    onPrimary = Color.White,
    secondary = NeonRed,
    onSecondary = Color.White,
    tertiary = DarkTertiary,
    background = CosmicSlate,
    onBackground = TextPrimary,
    surface = DarkGreySurface,
    onSurface = TextPrimary,
    surfaceVariant = BorderSlate,
    onSurfaceVariant = TextSecondary,
    outline = BorderSlate
)

private val LightColorScheme = lightColorScheme(
    primary = NeonBlue,
    onPrimary = Color.White,
    secondary = NeonRed,
    onSecondary = Color.White,
    tertiary = DarkTertiary,
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF16181F),
    surface = Color.White,
    onSurface = Color(0xFF16181F),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = Color(0xFFCBD5E0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Force dark theme for the perfect console visual ambiance!

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
