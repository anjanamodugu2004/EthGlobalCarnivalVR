package com.eth.vrcarnival.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eth.vrcarnival.data.models.TokenBalance
import com.eth.vrcarnival.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TokenItem(
    token: TokenBalance,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    val rarity = when {
        token.balanceFormatted?.toFloatOrNull() ?: 0f > 1000f -> "legendary"
        token.balanceFormatted?.toFloatOrNull() ?: 0f > 100f -> "epic"
        token.balanceFormatted?.toFloatOrNull() ?: 0f > 10f -> "rare"
        else -> "common"
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400)) + slideInHorizontally(
            tween(400),
            initialOffsetX = { it / 3 }
        )
    ) {
        PremiumCard(
            rarity = rarity,
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Token icon with rarity-based glow
                    GentleGlow(
                        isActive = rarity != "common",
                        glowColor = when (rarity) {
                            "legendary" -> GoldTrophy
                            "epic" -> RarityEpic
                            "rare" -> RarityRare
                            else -> GameSurfaceVariant
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when (rarity) {
                                        "legendary" -> RefinedGradients.legendaryGradient
                                        "epic" -> RefinedGradients.epicGradient
                                        "rare" -> RefinedGradients.rareGradient
                                        else -> RefinedGradients.elegantCard
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (token.symbol?.firstOrNull() ?: "?").toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = token.name ?: "Unknown Token",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = token.symbol ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            // Rarity indicator
                            if (rarity != "common") {
                                Spacer(modifier = Modifier.width(8.dp))
                                ElegantPulse(
                                    color = when (rarity) {
                                        "legendary" -> GoldTrophy
                                        "epic" -> RarityEpic
                                        "rare" -> RarityRare
                                        else -> GameSurfaceVariant
                                    },
                                    size = 4f,
                                    intensity = 1.2f
                                )
                            }
                        }
                    }
                }

                // Balance display with gaming aesthetics
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = token.balanceFormatted ?: "0",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = when (rarity) {
                            "legendary" -> GoldTrophy
                            "epic" -> RarityEpic
                            "rare" -> RarityRare
                            else -> TextPrimary
                        }
                    )

                    // Stack count style indicator
                    Text(
                        text = "x${(token.balanceFormatted?.split(".")?.firstOrNull() ?: "0")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}