package com.eth.vrcarnival.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eth.vrcarnival.data.models.GameNFT
import com.eth.vrcarnival.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun NFTCollectionGrid(
    nfts: List<GameNFT>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GameSurface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NFT Collection",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                val mintedCount = nfts.count { it.isMinted }
                Text(
                    text = "$mintedCount/${nfts.size} Minted",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (mintedCount == nfts.size) CyberGreen else TextAccent,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(400.dp)
            ) {
                itemsIndexed(nfts) { index, nft ->
                    var isVisible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * 100L)
                        isVisible = true
                    }

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(500)) + scaleIn(tween(500))
                    ) {
                        NFTCard(nft = nft)
                    }
                }
            }
        }
    }
}

@Composable
fun NFTCard(
    nft: GameNFT,
    modifier: Modifier = Modifier
) {
    PremiumCard(
        rarity = if (nft.isMinted) "rare" else "common",
        modifier = modifier.aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // NFT Image with color tinting for differentiation
            Image(
                painter = painterResource(id = nft.drawableRes),
                contentDescription = nft.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                colorFilter = if (!nft.isMinted) {
                    ColorFilter.colorMatrix(
                        ColorMatrix().apply {
                            setToSaturation(0.2f) // Fixed: use setToSaturation
                        }
                    )
                } else {
                    null
                }
            )

            // Overlay for unminted NFTs
            if (!nft.isMinted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )
            }

            // NFT Name and Status
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = nft.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (nft.isMinted) TextPrimary else Color.White,
                    textAlign = TextAlign.Start
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (nft.isMinted) CyberGreen else WarningOrange
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (nft.isMinted) "Minted" else "Locked",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (nft.isMinted) CyberGreen else WarningOrange,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Rarity indicator for minted NFTs
            if (nft.isMinted) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(RarityRare)
                )
            }
        }
    }
}