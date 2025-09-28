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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eth.vrcarnival.data.models.GameNFT
import com.eth.vrcarnival.ui.theme.*
import kotlinx.coroutines.delay
import com.eth.vrcarnival.R

@Composable
fun NFTCollectionGrid(
    nfts: List<GameNFT>,
    onNFTClick: (GameNFT) -> Unit,
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

                val ownedCount = nfts.count { it.isOwned }
                Text(
                    text = "$ownedCount/${nfts.size} Owned",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (ownedCount == nfts.size) CyberGreen else TextAccent,
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
                        NFTCard(
                            nft = nft,
                            onClick = { onNFTClick(nft) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NFTCard(
    nft: GameNFT,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumCard(
        rarity = nft.rarity?.lowercase() ?: "common",
        onClick = onClick,
        modifier = modifier.aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Network image with fallback to local drawable
            // Replace the existing AsyncImage in NFTCard with:
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(nft.imageUrl ?: "")
                    .crossfade(true)
                    .build(),
                contentDescription = nft.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                colorFilter = if (!nft.isOwned) {
                    ColorFilter.colorMatrix(
                        ColorMatrix().apply {
                            setToSaturation(0.3f)
                        }
                    )
                } else {
                    null
                },
                // Only use local drawable as absolute fallback
                error = painterResource(nft.drawableRes ?: R.drawable.unixy)
            )

            // Overlay for unowned NFTs
            if (!nft.isOwned) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Purchase price overlay
                nft.price?.let { price ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ElectricBlue.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${price.displayAmount} ETH",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
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
                    color = if (nft.isOwned) TextPrimary else Color.White,
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
                                if (nft.isOwned) CyberGreen else WarningOrange
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (nft.isOwned) "Owned" else "Buy Now",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (nft.isOwned) CyberGreen else WarningOrange,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Ownership indicator
            if (nft.isOwned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberGreen)
                )
            }
        }
    }
}