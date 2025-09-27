package com.eth.vrcarnival.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eth.vrcarnival.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AnimatedGradientCard(
    modifier: Modifier = Modifier,
    borderGlow: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500, easing = FastOutSlowInEasing)) +
                slideInVertically(tween(500, easing = FastOutSlowInEasing), initialOffsetY = { it / 6 }),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RefinedGradients.elegantCard)
                    .then(
                        if (borderGlow) {
                            Modifier.border(
                                1.dp,
                                RefinedGradients.subtleBorder,
                                RoundedCornerShape(16.dp)
                            )
                        } else {
                            Modifier.border(
                                0.5.dp,
                                TextSecondary.copy(alpha = 0.2f),
                                RoundedCornerShape(16.dp)
                            )
                        }
                    )
                    .padding(24.dp)
            ) {
                // Subtle background glow with your colors
                if (borderGlow) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(RefinedGradients.glowEffect)
                    )
                }

                Column(content = content)
            }
        }
    }
}

@Composable
fun ElegantPulse(
    color: Color = ElectricBlue,
    size: Float = 6f,
    intensity: Float = 1.3f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "elegant_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = intensity,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
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
fun SmoothShimmer(
    modifier: Modifier = Modifier,
    baseColor: Color = GameSurfaceVariant,
    highlightColor: Color = ElectricBlue
) {
    val infiniteTransition = rememberInfiniteTransition(label = "smooth_shimmer")

    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        baseColor.copy(alpha = 0.3f),
                        highlightColor.copy(alpha = 0.5f),
                        baseColor.copy(alpha = 0.3f)
                    ),
                    startX = shimmerProgress * 300f - 100f,
                    endX = shimmerProgress * 300f + 100f
                )
            )
    )
}

@Composable
fun GentleGlow(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    glowColor: Color = ElectricBlue,
    content: @Composable () -> Unit
) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.4f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "glow_alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "glow_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .background(
                glowColor.copy(alpha = glowAlpha),
                RoundedCornerShape(12.dp)
            )
    ) {
        content()
    }
}

@Composable
fun PremiumCard(
    rarity: String = "common",
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val cardGradient = when (rarity.lowercase()) {
        "legendary" -> RefinedGradients.legendaryGradient
        "epic" -> RefinedGradients.epicGradient
        "rare" -> RefinedGradients.rareGradient
        else -> RefinedGradients.elegantCard
    }

    val borderColor = when (rarity.lowercase()) {
        "legendary" -> GoldTrophy
        "epic" -> RarityEpic
        "rare" -> RarityRare
        else -> TextSecondary.copy(alpha = 0.2f)
    }

    val isInteractive = onClick != null
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        onClick = onClick ?: {},
        modifier = modifier.scale(scale),
        enabled = isInteractive,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (rarity != "common") 4.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardGradient)
                .border(
                    if (rarity != "common") 1.dp else 0.5.dp,
                    borderColor,
                    RoundedCornerShape(12.dp)
                ),
            content = content
        )
    }
}

@Composable
fun PowerMeterBalance(
    balance: String,
    symbol: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    var powerLevel by remember { mutableStateOf(0f) }

    LaunchedEffect(balance) {
        isVisible = true
        // Animate power level based on balance
        powerLevel = (balance.toFloatOrNull() ?: 0f) / 100f // Normalize to 0-1
        powerLevel = powerLevel.coerceIn(0f, 1f)
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(800)) + scaleIn(tween(800))
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RefinedGradients.holographicCard)
                    .border(
                        2.dp,
                        RefinedGradients.powerMeterGradient,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "POWER LEVEL",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextAccent,
                                letterSpacing = 2.sp
                            )

                            if (isLoading) {
                                SmoothShimmer(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(32.dp)
                                        .padding(top = 8.dp)
                                )
                            } else {
                                Text(
                                    text = balance,
                                    style = MaterialTheme.typography.displayLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = symbol,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextAccent
                                )
                            }
                        }

                        // Power level indicator
                        PowerLevelMeter(
                            level = powerLevel,
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated power bar
                    PowerBar(
                        progress = powerLevel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun PowerLevelMeter(
    level: Float,
    modifier: Modifier = Modifier
) {
    val animatedLevel by animateFloatAsState(
        targetValue = level,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "power_level"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer ring
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = GameSurfaceVariant,
            strokeWidth = 8.dp,
        )

        // Power level ring
        CircularProgressIndicator(
            progress = { animatedLevel },
            modifier = Modifier.fillMaxSize(),
            color = when {
                animatedLevel > 0.8f -> CyberGreen
                animatedLevel > 0.5f -> ShandyYellow
                animatedLevel > 0.2f -> WarningOrange
                else -> DangerRed
            },
            strokeWidth = 8.dp,
        )

        // Center power indicator
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when {
                        animatedLevel > 0.8f -> RefinedGradients.celebrationGradient
                        else -> RefinedGradients.powerMeterGradient
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Bolt,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PowerBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "power_bar"
    )

    Box(
        modifier = modifier
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(GameSurfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(6.dp))
                .background(RefinedGradients.powerMeterGradient)
        )

        // Animated sparkles along the bar
        if (animatedProgress > 0.1f) {
            repeat(3) { index ->
                SparkleEffect(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (animatedProgress * 200 * (index + 1) / 3).dp)
                )
            }
        }
    }
}

@Composable
fun SparkleEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_scale"
    )

    Box(
        modifier = modifier
            .size(4.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(ShandyYellow)
    )
}