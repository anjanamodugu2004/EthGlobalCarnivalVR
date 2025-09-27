package com.eth.vrcarnival.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.eth.vrcarnival.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AnimatedGradientCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(GameThemeConfig.SLOW_ANIMATION)) + slideInVertically(
            tween(GameThemeConfig.SLOW_ANIMATION),
            initialOffsetY = { it / 4 }
        ),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(GameThemeConfig.largeCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GameGradients.holographicCard)
                    .border(
                        1.dp,
                        GameGradients.powerMeterGradient,
                        RoundedCornerShape(GameThemeConfig.largeCornerRadius)
                    )
                    .padding(GameThemeConfig.xlSpace),
            ) {
                Column(content = content)
            }
        }
    }
}

@Composable
fun GamePulsingDot(
    color: Color = ElectricBlue,
    size: Float = 8f,
    pulseIntensity: Float = 1.4f,
    duration: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "game_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .size((size * scale).dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun PulsingDot(
    color: Color = MaterialTheme.gamingColors.electricBlue,
    size: Float = 8f
) {
    GamePulsingDot(color = color, size = size)
}

@Composable
fun GameShimmerBox(
    modifier: Modifier = Modifier,
    baseColor: Color = GameSurfaceVariant,
    highlightColor: Color = ElectricBlue
) {
    val infiniteTransition = rememberInfiniteTransition(label = "game_shimmer")

    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(GameThemeConfig.smallCornerRadius))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        baseColor,
                        highlightColor.copy(alpha = alpha),
                        baseColor
                    ),
                    startX = shimmerProgress * 300f,
                    endX = shimmerProgress * 300f + 100f
                )
            )
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier
) {
    GameShimmerBox(modifier = modifier)
}

@Composable
fun HolographicCard(
    modifier: Modifier = Modifier,
    borderWidth: Float = 2f,
    content: @Composable BoxScope.() -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(GameThemeConfig.POWER_UP_ANIMATION)) +
                scaleIn(tween(GameThemeConfig.POWER_UP_ANIMATION, easing = FastOutSlowInEasing))
    ) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(GameThemeConfig.largeCornerRadius),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = GameThemeConfig.elevatedCardElevation)
        ) {
            Box(
                modifier = Modifier
                    .background(GameGradients.holographicCard)
                    .border(
                        borderWidth.dp,
                        GameGradients.powerMeterGradient,
                        RoundedCornerShape(GameThemeConfig.largeCornerRadius)
                    ),
                content = content
            )
        }
    }
}

@Composable
fun PowerUpEffect(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "power_up_scale"
    )

    val glow by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(GameThemeConfig.NORMAL_ANIMATION),
        label = "power_up_glow"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (isActive) {
                    Modifier.border(
                        (2 * glow).dp,
                        GameGradients.celebrationGradient,
                        RoundedCornerShape(GameThemeConfig.mediumCornerRadius)
                    )
                } else Modifier
            )
    ) {
        content()

        if (isActive) {
            // Sparkle effects around the content
            repeat(4) { index ->
                SparkleParticle(
                    delay = index * 100L,
                    modifier = Modifier.align(
                        when (index) {
                            0 -> Alignment.TopStart
                            1 -> Alignment.TopEnd
                            2 -> Alignment.BottomStart
                            else -> Alignment.BottomEnd
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun SparkleParticle(
    delay: Long = 0L,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_rotation"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(300)) + scaleIn(tween(300))
    ) {
        Icon(
            Icons.Default.Star,
            contentDescription = null,
            tint = ShandyYellow,
            modifier = modifier
                .size((8 * scale).dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        )
    }
}

@Composable
fun LoadingOrb(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(tween(GameThemeConfig.NORMAL_ANIMATION)) + scaleIn(tween(GameThemeConfig.NORMAL_ANIMATION)),
        exit = fadeOut(tween(GameThemeConfig.FAST_ANIMATION)) + scaleOut(tween(GameThemeConfig.FAST_ANIMATION))
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "loading_orb")

        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "orb_rotation"
        )

        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orb_scale"
        )

        Box(
            modifier = modifier
                .size((48 * scale).dp)
                .graphicsLayer { rotationZ = rotation }
                .clip(CircleShape)
                .background(GameGradients.powerMeterGradient)
                .border(2.dp, ElectricBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(CyberGreen)
            )
        }
    }
}

@Composable
fun CelebrationBurst(
    isTriggered: Boolean,
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showBurst by remember { mutableStateOf(false) }

    LaunchedEffect(isTriggered) {
        if (isTriggered) {
            showBurst = true
            delay(GameThemeConfig.CELEBRATION_ANIMATION.toLong())
            showBurst = false
            onComplete()
        }
    }

    AnimatedVisibility(
        visible = showBurst,
        enter = fadeIn(tween(200)) + scaleIn(tween(200)),
        exit = fadeOut(tween(300)) + scaleOut(tween(300))
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Multiple sparkle bursts
            repeat(12) { index ->
                val angle = (index * 30f)
                SparkleParticle(
                    delay = (index * 50L),
                    modifier = Modifier
                        .offset(
                            x = (50 * kotlin.math.cos(Math.toRadians(angle.toDouble()))).dp,
                            y = (50 * kotlin.math.sin(Math.toRadians(angle.toDouble()))).dp
                        )
                )
            }
        }
    }
}