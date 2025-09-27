package com.eth.vrcarnival.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eth.vrcarnival.ui.theme.*

@Composable
fun WalletHeader(
    onSendClick: () -> Unit,
    onLogoutClick: () -> Unit,
    isSendEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    PremiumCard(
        rarity = "rare",
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ElegantPulse(
                    color = ElectricBlue,
                    size = 8f,
                    intensity = 1.2f
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Portfolio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GentleGlow(
                    isActive = isSendEnabled,
                    glowColor = ElectricBlue,
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = onSendClick,
                        enabled = isSendEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue
                        )
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send", fontWeight = FontWeight.Medium)
                    }
                }

                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DangerRed
                    )
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}