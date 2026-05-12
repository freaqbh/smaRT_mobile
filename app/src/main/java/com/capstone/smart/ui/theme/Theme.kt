package com.capstone.smart.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SmaRTColorScheme = lightColorScheme(
    primary = Teal600,
    onPrimary = Color.White,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal900,
    secondary = Teal500,
    onSecondary = Color.White,
    secondaryContainer = Teal50,
    onSecondaryContainer = Teal800,
    tertiary = AgendaPurple,
    onTertiary = Color.White,
    error = PanicRed,
    onError = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondary,
    outline = DividerGray
)

@Composable
fun SmaRTTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Teal900.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = SmaRTColorScheme,
        typography = Typography,
        content = content
    )
}