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
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Gaming Dark Color Scheme (Primary)
private val GameDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = GameBackground,
    primaryContainer = HolographicBlue.copy(alpha = 0.3f),
    onPrimaryContainer = ElectricBlue,

    secondary = NeonPurple,
    onSecondary = TextPrimary,
    secondaryContainer = NeonPurple.copy(alpha = 0.2f),
    onSecondaryContainer = NeonPurple,

    tertiary = CyberGreen,
    onTertiary = GameBackground,
    tertiaryContainer = CyberGreen.copy(alpha = 0.2f),
    onTertiaryContainer = CyberGreen,

    background = GameBackground,
    onBackground = TextPrimary,

    surface = GameSurface,
    onSurface = TextPrimary,
    surfaceVariant = GameSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    surfaceTint = ElectricBlue,
    inverseSurface = TextPrimary,
    inverseOnSurface = GameBackground,

    error = DangerRed,
    onError = TextPrimary,
    errorContainer = DangerRed.copy(alpha = 0.2f),
    onErrorContainer = DangerRed,

    outline = GameSurfaceVariant,
    outlineVariant = GameSurfaceVariant.copy(alpha = 0.5f),

    scrim = GameBackground.copy(alpha = 0.8f),

    // Custom gaming surface containers
    surfaceContainer = GameSurface,
    surfaceContainerHigh = GameSurfaceVariant,
    surfaceContainerHighest = GameSurfaceVariant.copy(alpha = 0.8f),
    surfaceContainerLow = GameSurface.copy(alpha = 0.8f),
    surfaceContainerLowest = GameBackground.copy(alpha = 0.9f),

    inversePrimary = ElectricBlue.copy(alpha = 0.8f)
)

// Gaming Light Color Scheme (for users who prefer light mode)
private val GameLightColorScheme = lightColorScheme(
    primary = HolographicBlue,
    onPrimary = TextPrimary,
    primaryContainer = ElectricBlue.copy(alpha = 0.1f),
    onPrimaryContainer = HolographicBlue,

    secondary = RarityEpic,
    onSecondary = TextPrimary,
    secondaryContainer = NeonPurple.copy(alpha = 0.1f),
    onSecondaryContainer = RarityEpic,

    tertiary = CyberGreen,
    onTertiary = TextPrimary,
    tertiaryContainer = CyberGreen.copy(alpha = 0.1f),
    onTertiaryContainer = CyberGreen.copy(alpha = 0.8f),

    background = androidx.compose.ui.graphics.Color.White,
    onBackground = GameBackground,

    surface = androidx.compose.ui.graphics.Color(0xFFF8FAFC),
    onSurface = GameBackground,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE2E8F0),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF64748B),

    surfaceTint = ElectricBlue,
    inverseSurface = GameSurface,
    inverseOnSurface = TextPrimary,

    error = DangerRed,
    onError = TextPrimary,
    errorContainer = DangerRed.copy(alpha = 0.1f),
    onErrorContainer = DangerRed.copy(alpha = 0.8f),

    outline = androidx.compose.ui.graphics.Color(0xFFCBD5E1),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFFE2E8F0),

    scrim = GameBackground.copy(alpha = 0.6f),

    surfaceContainer = androidx.compose.ui.graphics.Color(0xFFF1F5F9),
    surfaceContainerHigh = androidx.compose.ui.graphics.Color(0xFFE2E8F0),
    surfaceContainerHighest = androidx.compose.ui.graphics.Color(0xFFCBD5E1),
    surfaceContainerLow = androidx.compose.ui.graphics.Color(0xFFF8FAFC),
    surfaceContainerLowest = androidx.compose.ui.graphics.Color.White,

    inversePrimary = ElectricBlue
)

@Composable
fun VrCarnivalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to maintain gaming aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Even with dynamic colors, prefer our gaming scheme
            if (darkTheme) GameDarkColorScheme else GameLightColorScheme
        }
        // Gaming theme always prioritizes dark mode for better visual effects
        darkTheme -> GameDarkColorScheme
        else -> GameLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar to gaming dark background for immersion
            window.statusBarColor = GameBackground.toArgb()
            // Always use light status bar content for dark gaming theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false

            // Set navigation bar to match
            window.navigationBarColor = GameBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Extension functions for easy access to gaming colors
val MaterialTheme.gamingColors: GameColors
    @Composable
    get() = GameColors

object GameColors {
    val electricBlue @Composable get() = ElectricBlue
    val electricRed @Composable get() = ElectricRed
    val neonPurple @Composable get() = NeonPurple
    val cyberGreen @Composable get() = CyberGreen
    val shandyYellow @Composable get() = ShandyYellow
    val mellowApricot @Composable get() = MellowApricot

    val goldTrophy @Composable get() = GoldTrophy
    val silverMedal @Composable get() = SilverMedal
    val bronzeMedal @Composable get() = BronzeMedal

    val rarityLegendary @Composable get() = RarityLegendary
    val rarityEpic @Composable get() = RarityEpic
    val rarityRare @Composable get() = RarityRare
    val rarityCommon @Composable get() = RarityCommon

    val gameBackground @Composable get() = GameBackground
    val gameSurface @Composable get() = GameSurface
    val gameSurfaceVariant @Composable get() = GameSurfaceVariant
    val holographicBlue @Composable get() = HolographicBlue
    val neonAccent @Composable get() = NeonAccent

    val successGreen @Composable get() = SuccessGreen
    val warningOrange @Composable get() = WarningOrange
    val dangerRed @Composable get() = DangerRed
    val infoBlue @Composable get() = InfoBlue

    val textPrimary @Composable get() = TextPrimary
    val textSecondary @Composable get() = TextSecondary
    val textAccent @Composable get() = TextAccent
    val textOnDark @Composable get() = TextOnDark
}

// Extension functions for easy access to gaming gradients
val MaterialTheme.gamingGradients: GameGradientColors
    @Composable
    get() = GameGradientColors

object GameGradientColors {
    val powerMeter @Composable get() = RefinedGradients.powerMeterGradient
    val holographicCard @Composable get() = RefinedGradients.holographicCard
    val legendary @Composable get() = RefinedGradients.legendaryGradient
    val epic @Composable get() = RefinedGradients.epicGradient
    val rare @Composable get() = RefinedGradients.rareGradient
//    val gameBackground @Composable get() = RefinedGradients.gameBackground
    val celebration @Composable get() = RefinedGradients.celebrationGradient
//    val ethereumWorld @Composable get() = RefinedGradients.ethereumWorld
//    val polygonWorld @Composable get() = RefinedGradients.polygonWorld
//    val bscWorld @Composable get() = RefinedGradients.bscWorld
}

// Gaming theme configuration object
object GameThemeConfig {
    // Animation durations in milliseconds
    const val FAST_ANIMATION = 200
    const val NORMAL_ANIMATION = 300
    const val SLOW_ANIMATION = 500
    const val POWER_UP_ANIMATION = 800
    const val CELEBRATION_ANIMATION = 1200

    // Common corner radius values
    val smallCornerRadius = 8.dp
    val mediumCornerRadius = 12.dp
    val largeCornerRadius = 16.dp
    val xlCornerRadius = 20.dp

    // Gaming spacing system
    val tinySpace = 4.dp
    val smallSpace = 8.dp
    val mediumSpace = 12.dp
    val largeSpace = 16.dp
    val xlSpace = 20.dp
    val xxlSpace = 24.dp
    val xxxlSpace = 32.dp

    // Gaming elevation levels
    val cardElevation = 4.dp
    val elevatedCardElevation = 8.dp
    val modalElevation = 16.dp
    val maxElevation = 24.dp
}