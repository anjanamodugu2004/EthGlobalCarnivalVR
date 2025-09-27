package com.eth.vrcarnival.data.models

// API Response Wrappers
data class ApiResponse<T>(
    val result: T
)

data class TokensResponse(
    val tokens: List<TokenBalance>,
    val pagination: Pagination
)

data class ListTokenInfo(
    val address: String?,
    val name: String?,
    val symbol: String?,
    val decimals: Int?,
    val chainId: Int?
)

data class ListTokensResponse(
    val tokens: List<ListTokenInfo>,
    val pagination: Pagination
)

data class NFTsResponse(
    val nfts: List<NFT>,
    val pagination: Pagination
)

data class Pagination(
    val hasMore: Boolean,
    val limit: Int,
    val page: Int
)

data class SendTransactionResponse(
    val transactionIds: List<String>
)

// Fixed models to match actual API response
data class TokenBalance(
    val chain_id: Int?,
    val token_address: String?,
    val owner_address: String?,
    val balance: String?,
    val name: String?,
    val symbol: String?,
    val decimals: Int?
) {
    val balanceFormatted: String?
        get() = if (balance != null && decimals != null) {
            try {
                val balanceDouble = balance.toDouble() / Math.pow(10.0, decimals.toDouble())
                String.format("%.6f", balanceDouble)
            } catch (e: Exception) {
                balance
            }
        } else balance
}

data class NFT(
    val tokenId: String?,
    val name: String?,
    val description: String?,
    val image: String?,
    val contractAddress: String?
)

data class WalletBalance(
    val chainId: Int?,
    val decimals: Int?,
    val displayValue: String?,
    val name: String?,
    val symbol: String?,
    val tokenAddress: String?,
    val value: String?
) {
    val balanceFormatted: String?
        get() = displayValue
}

data class SendTokenRequest(
    val from: String,
    val chainId: Int,
    val recipients: List<Recipient>
)

data class Recipient(
    val address: String,
    val quantity: String
)

data class VerifyBalanceChangeRequest(
    val walletAddress: String,
    val chainId: Int,
    val expectedChange: String,
    val tokenAddress: String?
)

data class TokenInfo(
    val address: String?,
    val name: String?,
    val symbol: String?,
    val decimals: Int?,
    val chainId: Int?
)

data class Chain(
    val chainId: Int,
    val name: String,
    val symbol: String,
    val isTestnet: Boolean = false
)

data class SendTokenResponse(
    val success: Boolean = true,
    val transactionHash: String?,
    val message: String?
)

data class CarTokenBalance(
    val displayValue: String?,
    val symbol: String?,
    val name: String?,
    val address: String?,
    val decimals: Int?
)

data class TransferCarTokenRequest(
    val toAddress: String,
    val amount: String,
    val fromAddress: String
)

data class TransferCarTokenResponse(
    val transactionHash: String?,
    val transactionId: String?,
    val message: String?
)