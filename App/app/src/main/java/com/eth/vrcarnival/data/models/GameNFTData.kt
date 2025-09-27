package com.eth.vrcarnival.data.models

import com.eth.vrcarnival.R

data class GameNFT(
    val id: String,
    val name: String,
    val drawableRes: Int,
    val isMinted: Boolean,
    val color: String = "#6366F1"
)

object GameNFTData {
    fun getGameNFTs(): List<GameNFT> = listOf(
        GameNFT("spinx", "SpinX", R.drawable.spinx, true),
        GameNFT("charmz", "Charmz", R.drawable.charmz, false),
        GameNFT("unixy", "Unixy", R.drawable.unixy, true),
        GameNFT("squidy", "Squidy", R.drawable.squidy, false),
        GameNFT("wiz", "Wiz", R.drawable.wiz, true),
        GameNFT("wittyfox", "WittyFox", R.drawable.witty_fox, false),
        GameNFT("kat", "Kat", R.drawable.katz, true),
        GameNFT("pup", "Pup", R.drawable.pup, false),
        GameNFT("joko", "Joko", R.drawable.joko, true)
    )
}