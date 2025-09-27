package com.eth.vrcarnival.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eth.vrcarnival.data.models.Chain
import kotlinx.coroutines.delay

@Composable
fun NetworkSelector(
    chains: List<Chain>,
    selectedChain: Chain,
    onChainSelected: (Chain) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Network",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(chains) { index, chain ->
                    var isVisible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * 30L)
                        isVisible = true
                    }

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(300)) + scaleIn(tween(300))
                    ) {
                        FilterChip(
                            onClick = { onChainSelected(chain) },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (chain.isTestnet) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.tertiary,
                                                    RoundedCornerShape(3.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = chain.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (selectedChain.chainId == chain.chainId)
                                            FontWeight.Medium else FontWeight.Normal
                                    )
                                }
                            },
                            selected = selectedChain.chainId == chain.chainId,
                            enabled = enabled,
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }
        }
    }
}