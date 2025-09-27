package com.eth.vrcarnival.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    secondary = SecondaryPurpleLight,
    tertiary = AccentGreen,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextOnDark,
    onSecondary = TextOnDark,
    onTertiary = BackgroundDark,
    onBackground = TextOnDark,
    onSurface = TextOnDark,
    surfaceVariant = SurfaceDark.copy(alpha = 0.8f),
    onSurfaceVariant = TextSecondary,
    primaryContainer = PrimaryBlue.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryBlueLight,
    errorContainer = AccentRed.copy(alpha = 0.2f),
    onErrorContainer = AccentRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryPurple,
    tertiary = AccentGreen,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = TextOnDark,
    onSecondary = TextOnDark,
    onTertiary = BackgroundLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight.copy(alpha = 0.5f),
    onSurfaceVariant = TextSecondary,
    primaryContainer = PrimaryBlue.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryBlue,
    errorContainer = AccentRed.copy(alpha = 0.1f),
    onErrorContainer = AccentRed
)

@Composable
fun VrCarnivalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}