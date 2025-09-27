package com.eth.vrcarnival.data.models

import com.eth.vrcarnival.R

data class GameNFT(
    val id: String,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val drawableRes: Int? = null,
    val isMinted: Boolean,
    val isOwned: Boolean = false,
    val price: NFTPrice? = null,
    val rarity: String? = null
)


data class NFTPrice(
    val amount: String,
    val currency: String, // Token address or "ETH"
    val displayAmount: String // Formatted amount for display
)

data class NFTPurchaseRequest(
    val from: String,
    val chainId: Int,
    val recipients: List<Recipient>, // Payment to our wallet
    val nftTokenId: String,
    val nftContractAddress: String
)

object GameNFTData {
    fun getGameNFTs(): List<GameNFT> = listOf(
    )
}