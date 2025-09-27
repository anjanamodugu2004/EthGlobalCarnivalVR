package com.eth.vrcarnival.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object RefinedGradients {
    val elegantCard = Brush.linearGradient(
        colors = listOf(
            GameSurface.copy(alpha = 0.9f),
            GameSurfaceVariant.copy(alpha = 0.6f),
            GameSurface.copy(alpha = 0.9f)
        ),
        start = Offset(0f, 0f),
        end = Offset(300f, 300f)
    )

    val subtleBorder = Brush.linearGradient(
        colors = listOf(
            ElectricBlue.copy(alpha = 0.6f),
            NeonPurple.copy(alpha = 0.4f),
            CyberGreen.copy(alpha = 0.3f)
        )
    )

    // Achievement gradients using your rarity colors
    val legendaryGradient = Brush.linearGradient(
        colors = listOf(GoldTrophy, RarityLegendary)
    )

    val epicGradient = Brush.linearGradient(
        colors = listOf(RarityEpic, NeonPurple)
    )

    val rareGradient = Brush.linearGradient(
        colors = listOf(RarityRare, ElectricBlue)
    )

    val commonGradient = Brush.linearGradient(
        colors = listOf(RarityCommon, GameSurfaceVariant)
    )

    // Background gradient using your gaming colors
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            GameBackground,
            GameSurface.copy(alpha = 0.8f),
            GameBackground
        )
    )

    // Power meter gradient using your original colors
    val powerMeterGradient = Brush.linearGradient(
        colors = listOf(PowerMeterStart, PowerMeterEnd),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    // Holographic card using your colors
    val holographicCard = Brush.linearGradient(
        colors = listOf(
            HolographicBlue.copy(alpha = 0.3f),
            NeonPurple.copy(alpha = 0.2f),
            ElectricBlue.copy(alpha = 0.3f)
        )
    )

    // Subtle glow effect with your electric blue
    val glowEffect = Brush.radialGradient(
        colors = listOf(
            ElectricBlue.copy(alpha = 0.15f),
            Color.Transparent
        ),
        radius = 200f
    )

    // Success/Error gradients using your status colors
    val successGradient = Brush.linearGradient(
        colors = listOf(CyberGreen, SuccessGreen)
    )

    val errorGradient = Brush.linearGradient(
        colors = listOf(DangerRed, ElectricRed)
    )

    // Celebration effects
    val celebrationGradient = Brush.radialGradient(
        colors = listOf(
            ShandyYellow.copy(alpha = 0.8f),
            GoldTrophy.copy(alpha = 0.6f),
            Color.Transparent
        )
    )
}