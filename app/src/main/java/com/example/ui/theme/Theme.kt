package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = WLTealSecondary,
    onPrimary = WLTextPrimaryDark,
    primaryContainer = WLTealDark,
    onPrimaryContainer = WLTealLight,
    secondary = WLOrangeAccent,
    onSecondary = WLSurfaceDark,
    tertiary = WLStarGold,
    background = WLBackgroundDark,
    onBackground = WLTextPrimaryDark,
    surface = WLSurfaceDark,
    onSurface = WLTextPrimaryDark,
    surfaceVariant = WLCardDark,
    onSurfaceVariant = WLTextSecondaryDark,
    outline = WLBorderDark,
    error = WLError
)

private val LightColorScheme = lightColorScheme(
    primary = WLTealPrimary,
    onPrimary = Color.White,
    primaryContainer = WLTealLight,
    onPrimaryContainer = WLTealDark,
    secondary = WLOrangeAccent,
    onSecondary = Color.White,
    tertiary = WLStarGold,
    background = Color.White,
    onBackground = WLTextPrimaryLight,
    surface = Color.White,
    onSurface = WLTextPrimaryLight,
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = WLTextSecondaryLight,
    outline = WLBorderLight,
    error = WLError
)

@Composable
fun WonderLightTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.White.toArgb()
                window.navigationBarColor = Color.White.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
