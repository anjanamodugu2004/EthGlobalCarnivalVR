package com.eth.vrcarnival.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object GameGradients {
    // Power meter gradients
    val powerMeterGradient = Brush.linearGradient(
        colors = listOf(PowerMeterStart, PowerMeterEnd),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    // Holographic card effects
    val holographicCard = Brush.linearGradient(
        colors = listOf(
            HolographicBlue.copy(alpha = 0.3f),
            NeonPurple.copy(alpha = 0.2f),
            ElectricBlue.copy(alpha = 0.3f)
        )
    )

    // Achievement gradients
    val legendaryGradient = Brush.linearGradient(
        colors = listOf(GoldTrophy, RarityLegendary)
    )

    val epicGradient = Brush.linearGradient(
        colors = listOf(RarityEpic, NeonPurple)
    )

    val rareGradient = Brush.linearGradient(
        colors = listOf(RarityRare, ElectricBlue)
    )

    // Background gradients
    val gameBackground = Brush.verticalGradient(
        colors = listOf(
            GameBackground,
            GameSurface.copy(alpha = 0.8f),
            GameBackground
        )
    )

    // Celebration effects
    val celebrationGradient = Brush.radialGradient(
        colors = listOf(
            ShandyYellow.copy(alpha = 0.8f),
            GoldTrophy.copy(alpha = 0.6f),
            Color.Transparent
        )
    )

    // Network world gradients
    val ethereumWorld = Brush.linearGradient(
        colors = listOf(Color(0xFF627EEA), Color(0xFF8B5CF6))
    )

    val polygonWorld = Brush.linearGradient(
        colors = listOf(Color(0xFF8247E5), Color(0xFFBB44E6))
    )

    val bscWorld = Brush.linearGradient(
        colors = listOf(Color(0xFFF3BA2F), Color(0xFFE0A82E))
    )
}