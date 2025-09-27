package com.eth.vrcarnival.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
        powerLevel = (balance.toFloatOrNull() ?: 0f) / 100f
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
                    .background(GameGradients.holographicCard)
                    .border(
                        2.dp,
                        GameGradients.powerMeterGradient,
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
                                PulsingText(
                                    text = "CHARGING...",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = ElectricBlue
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
                        animatedLevel > 0.8f -> GameGradients.celebrationGradient
                        else -> GameGradients.powerMeterGradient
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
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            PowerMeterStart,
                            PowerMeterEnd
                        )
                    )
                )
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

@Composable
fun PulsingText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Text(
        text = text,
        style = style,
        color = color.copy(alpha = alpha),
        modifier = modifier
    )
}

@Composable
fun InventoryTokenItem(
    tokenName: String,
    tokenSymbol: String,
    balance: String,
    rarity: String = "common",
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val rarityGradient = when (rarity.lowercase()) {
        "legendary" -> GameGradients.legendaryGradient
        "epic" -> GameGradients.epicGradient
        "rare" -> GameGradients.rareGradient
        else -> Brush.linearGradient(colors = listOf(GameSurface, GameSurfaceVariant))
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400)) + slideInHorizontally(
            tween(400),
            initialOffsetX = { it / 3 }
        )
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(rarityGradient)
                    .border(
                        2.dp,
                        when (rarity.lowercase()) {
                            "legendary" -> GoldTrophy
                            "epic" -> RarityEpic
                            "rare" -> RarityRare
                            else -> GameSurfaceVariant
                        },
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Token icon with rarity effect
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    when (rarity.lowercase()) {
                                        "legendary" -> GameGradients.legendaryGradient
                                        "epic" -> GameGradients.epicGradient
                                        "rare" -> GameGradients.rareGradient
                                        else -> GameGradients.powerMeterGradient
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (tokenSymbol.firstOrNull() ?: "?").toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = tokenName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tokenSymbol,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Rarity indicator
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (rarity.lowercase()) {
                                                "legendary" -> GoldTrophy
                                                "epic" -> RarityEpic
                                                "rare" -> RarityRare
                                                else -> TextSecondary
                                            }
                                        )
                                )
                            }
                        }
                    }

                    // Balance with stack count style
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = balance,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "x${balance.split(".")[0]}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkWorldSelector(
    chainName: String,
    isSelected: Boolean,
    isTestnet: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val worldGradient = when (chainName.lowercase()) {
        "ethereum" -> GameGradients.ethereumWorld
        "polygon" -> GameGradients.polygonWorld
        "bsc", "binance" -> GameGradients.bscWorld
        else -> GameGradients.powerMeterGradient
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "world_scale"
    )

    Card(
        onClick = onClick,
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .background(worldGradient)
                .border(
                    if (isSelected) 3.dp else 1.dp,
                    if (isSelected) ElectricBlue else GameSurfaceVariant,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isTestnet) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CyberGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = chainName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = TextPrimary
                )

                if (isSelected) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}