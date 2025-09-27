package com.eth.vrcarnival.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

object Gradients {
    val primaryGradient = Brush.linearGradient(
        colors = listOf(GradientStart, GradientEnd),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            PrimaryBlueLight.copy(alpha = 0.1f),
            SecondaryPurpleLight.copy(alpha = 0.1f)
        )
    )

    val successGradient = Brush.linearGradient(
        colors = listOf(AccentGreen, AccentGreen.copy(alpha = 0.7f))
    )
}