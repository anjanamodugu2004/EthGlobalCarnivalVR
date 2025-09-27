package com.eth.vrcarnival.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eth.vrcarnival.data.models.WalletBalance

@Composable
fun BalanceCard(
    balance: WalletBalance?,
    chainSymbol: String,
    chainName: String,
    isLoading: Boolean,
    onViewTokensClick: () -> Unit,
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
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    if (isLoading) {
                        ShimmerBox(
                            modifier = Modifier
                                .width(140.dp)
                                .height(32.dp)
                                .padding(top = 8.dp)
                        )
                    } else {
                        balance?.let { bal ->
                            Text(
                                text = "${bal.displayValue ?: "0"} ${bal.symbol ?: chainSymbol}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } ?: run {
                            Text(
                                text = "0 $chainSymbol",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Text(
                            text = chainName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                TextButton(
                    onClick = onViewTokensClick,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "View All",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}