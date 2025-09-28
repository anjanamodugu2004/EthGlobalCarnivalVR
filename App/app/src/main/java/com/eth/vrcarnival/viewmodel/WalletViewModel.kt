package com.eth.vrcarnival.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eth.vrcarnival.data.auth.AuthManager
import com.eth.vrcarnival.data.models.*
import com.eth.vrcarnival.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject
import com.eth.vrcarnival.data.models.GameNFT
import com.eth.vrcarnival.data.models.GameNFTData
import com.eth.vrcarnival.R

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository,
    private val authManager: AuthManager
) : ViewModel() {

    var showNFTPurchaseDialog by mutableStateOf(false)
        private set

    var selectedNFTForPurchase by mutableStateOf<GameNFT?>(null)
        private set

    var isPurchasingNFT by mutableStateOf(false)
        private set

    // Company wallet address for NFT purchases
    private val nftPurchaseWalletAddress = "0x88C8665671C970813afF2172043e93a00b941c54"


    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var isOtpSent by mutableStateOf(false)
        private set

    var authResponse by mutableStateOf<AuthResponse?>(null)
        private set

    var selectedChain by mutableStateOf(getChains().first { it.chainId == 11155111 }) // Start with Sepolia
        private set

    var tokens by mutableStateOf<List<TokenBalance>>(emptyList())
        private set

    var nfts by mutableStateOf<List<NFT>>(emptyList())
        private set

    var gameNFTs by mutableStateOf<List<GameNFT>>(emptyList())
        private set

    var balance by mutableStateOf<WalletBalance?>(null)
        private set

    var availableTokens by mutableStateOf<List<TokenInfo>>(emptyList())
        private set

    var isSendingToken by mutableStateOf(false)
        private set

    var showSendDialog by mutableStateOf(false)
        private set

    var sendTokenSuccess by mutableStateOf<String?>(null)
        private set

    var isVerifyingTransaction by mutableStateOf(false)
        private set

    var isLoadingWalletData by mutableStateOf(false)
        private set

    var carTokenBalance by mutableStateOf<CarTokenBalance?>(null)
        private set

    var isSendingCarToken by mutableStateOf(false)
        private set

    var sendCarTokenSuccess by mutableStateOf<String?>(null)

    var isInitializing by mutableStateOf(true)
        private set

    init {
        initializeAuth()
    }

    private fun initializeAuth() {
        viewModelScope.launch {
            authManager.authFlow.collect { authState ->
                if (authState.isAuthenticated) {
                    authResponse = AuthResponse(
                        isNewUser = false,
                        token = authState.token!!,
                        type = "existing",
                        walletAddress = authState.walletAddress!!
                    )
                    loadWalletData()
                }
                isInitializing = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authManager.clearAuth()
            withContext(Dispatchers.Main) {
                authResponse = null
                tokens = emptyList()
                nfts = emptyList()
                balance = null
                availableTokens = emptyList()
                error = null
                isOtpSent = false
            }
        }
    }

    companion object {
        private const val NFT_CONTRACT_ADDRESS = "0xB68de9667F7361561f53114a4A6907Ed9360acE3"
        private const val MAX_TOKEN_ID = 2
    }

    // Replace the existing declarations with:
    private val nftContractAddress = NFT_CONTRACT_ADDRESS
    private var maxTokenId = MAX_TOKEN_ID

    fun loadGameNFTs() {
        if (selectedChain.chainId == 11155111) {
            authResponse?.let { auth ->
                viewModelScope.launch {
                    try {
                        val nftList = mutableListOf<GameNFT>()

                        for (tokenId in 0..maxTokenId) {
                            try {
                                val metadataResponse = repository.getNFTMetadata(
                                    nftContractAddress,
                                    tokenId.toString()
                                )

                                if (metadataResponse.isSuccessful) {
                                    val metadata = metadataResponse.body()

                                    val ownershipResponse = repository.getUniversalNFTs(
                                        nftContractAddress,
                                        auth.walletAddress
                                    )

                                    val isOwned = ownershipResponse.isSuccessful &&
                                            ownershipResponse.body()?.data?.tokenId == tokenId.toString() &&
                                            (ownershipResponse.body()?.data?.balance?.toIntOrNull() ?: 0) > 0

                                    metadata?.let { meta ->
                                        // Parse price from metadata
                                        val price = meta.metadata.price_amount?.let { priceAmount ->
                                            NFTPrice(
                                                amount = priceAmount,
                                                currency = meta.metadata.price_currency ?: "ETH",
                                                displayAmount = priceAmount
                                            )
                                        }

                                        // Get rarity from attributes
                                        val rarity = meta.metadata.attributes
                                            .find { it.trait_type.equals("Rarity", ignoreCase = true) }
                                            ?.value

                                        nftList.add(
                                            GameNFT(
                                                id = tokenId.toString(),
                                                name = meta.metadata.name,
                                                description = meta.metadata.description,
                                                imageUrl = meta.resolvedImageUrl,
                                                drawableRes = getDrawableForTokenId(tokenId), // Fallback
                                                isMinted = true, // All fetched NFTs are minted
                                                isOwned = isOwned,
                                                price = price,
                                                rarity = rarity
                                            )
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                // Continue with next token
                            }
                        }

                        gameNFTs = nftList
                    } catch (e: Exception) {
                        // Fall back to dummy data if server fails
                        gameNFTs = GameNFTData.getGameNFTs()
                    } finally {
                        checkIfAllDataLoaded()
                    }
                }
            }
        } else {
            gameNFTs = emptyList()
        }
    }

    fun openNFTPurchaseDialog(nft: GameNFT) {
        selectedNFTForPurchase = nft
        showNFTPurchaseDialog = true
    }

    fun closeNFTPurchaseDialog() {
        showNFTPurchaseDialog = false
        selectedNFTForPurchase = null
        isPurchasingNFT = false
        error = null
    }

    fun purchaseNFT(nft: GameNFT) {
        authResponse?.let { auth ->
            nft.price?.let { price ->
                viewModelScope.launch {
                    isPurchasingNFT = true
                    error = null

                    try {
                        // Convert price to wei
                        val priceInWei = try {
                            val priceBigDecimal = price.amount.toBigDecimal()
                            val weiMultiplier = BigDecimal.TEN.pow(18)
                            val weiAmount = priceBigDecimal * weiMultiplier
                            weiAmount.toBigInteger().toString()
                        } catch (e: Exception) {
                            error = "Invalid price format"
                            return@launch
                        }

                        // Create payment transaction to our wallet
                        val request = SendTokenRequest(
                            from = auth.walletAddress,
                            chainId = selectedChain.chainId,
                            recipients = listOf(
                                Recipient(
                                    address = nftPurchaseWalletAddress,
                                    quantity = priceInWei
                                )
                            )
                        )

                        val response = repository.sendTokens(auth.token, request)
                        if (response.isSuccessful) {
                            val result = response.body()?.result
                            val transactionId = result?.transactionIds?.firstOrNull()
                            if (transactionId != null) {
                                // Close dialog immediately after successful payment
                                closeNFTPurchaseDialog()
                                // Refresh NFT data to check ownership
                                loadGameNFTs()
                                // Monitor balance change in background
                                monitorBalanceChangeInBackground(auth.walletAddress, price.amount)
                            } else {
                                error = "Purchase failed - no transaction ID returned"
                            }
                        } else {
                            error = "Failed to purchase NFT: ${response.message()}"
                        }
                    } catch (e: Exception) {
                        error = "Network error: ${e.message}"
                    } finally {
                        isPurchasingNFT = false
                    }
                }
            } ?: run {
                error = "NFT price not available"
            }
        }
    }

    private fun getDrawableForTokenId(tokenId: Int): Int {
        return when (tokenId) {
            0 -> R.drawable.unixy
            1 -> R.drawable.spinx
            2 -> R.drawable.charmz
            // Add more mappings as token IDs expand to 10+
            3 -> R.drawable.squidy
            4 -> R.drawable.wiz
            5 -> R.drawable.witty_fox
            6 -> R.drawable.katz
            7 -> R.drawable.pup
            8 -> R.drawable.joko
            9 -> R.drawable.charmz  // Fallback for future tokens
            else -> R.drawable.unixy  // Default fallback
        }
    }

    fun getChains(): List<Chain> = listOf(
        Chain(1, "Ethereum Mainnet", "ETH"),
        Chain(11155111, "Sepolia Testnet", "ETH", true),
        Chain(8453, "Base", "ETH"),
        Chain(84532, "Base Sepolia", "ETH", true),
        Chain(137, "Polygon", "MATIC"),
        Chain(56, "BSC", "BNB"),
        Chain(42161, "Arbitrum", "ETH"),
        Chain(10, "Optimism", "ETH")
    )

    fun selectChain(chain: Chain) {
        selectedChain = chain
        // Clear previous data
        tokens = emptyList()
        nfts = emptyList()
        balance = null
        availableTokens = emptyList()
        carTokenBalance = null
        gameNFTs = emptyList()

        loadWalletData()
        loadGameNFTs()
    }

    fun sendOtp(email: String) {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                val response = repository.sendOtp(email)
                if (response.isSuccessful) {
                    isOtpSent = true
                } else {
                    error = "Failed to send OTP: ${response.message()}"
                }
            } catch (e: Exception) {
                error = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun verifyOtp(email: String, code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isLoading = true
                error = null
            }

            try {
                val response = repository.verifyOtp(email, code)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { auth ->
                            authResponse = auth
                            // Save authentication
                            launch(Dispatchers.IO) {
                                authManager.saveAuth(auth.token, auth.walletAddress, email)
                                // ADD: Send wallet to Unity
                                try {
                                    repository.sendWalletToUnity(email, auth.walletAddress)
                                } catch (e: Exception) {
                                    // Handle silently
                                }
                            }
                        }
                    } else {
                        error = "Invalid OTP: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Network error: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    fun loadWalletData() {
        authResponse?.let { auth ->
            isLoadingWalletData = true
            loadTokens(auth.walletAddress)
//            loadNFTs(auth.walletAddress)
            loadBalance(auth.walletAddress)
            loadCarToken(auth.walletAddress)
            loadGameNFTs()
        }
    }

    private fun loadCarToken(address: String) {
        // Only load CAR token on Sepolia testnet
        if (selectedChain.chainId == 11155111) {
            viewModelScope.launch {
                try {
                    val response = repository.getCarTokenBalance(address, selectedChain.chainId)
                    if (response.isSuccessful) {
                        carTokenBalance = response.body()
                    }
                } catch (e: Exception) {
                    // Handle error silently for now
                } finally {
                    checkIfAllDataLoaded()
                }
            }
        } else {
            carTokenBalance = null
        }
    }

    private fun loadTokens(address: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTokens(address, selectedChain.chainId)
                if (response.isSuccessful) {
                    tokens = response.body()?.result?.tokens ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error silently for now
            } finally {
                checkIfAllDataLoaded()
            }
        }
    }

    fun openSendDialog() {
        showSendDialog = true
    }


    fun sendCarTokens(recipientAddress: String, quantity: String) {
        authResponse?.let { auth ->
            viewModelScope.launch {
                isSendingCarToken = true
                error = null
                sendCarTokenSuccess = null

                try {
                    val quantityInWei = try {
                        val quantityBigDecimal = quantity.toBigDecimal()
                        val weiMultiplier = BigDecimal.TEN.pow(18)
                        val weiAmount = quantityBigDecimal * weiMultiplier
                        weiAmount.toBigInteger().toString()
                    } catch (e: Exception) {
                        error = "Invalid amount format"
                        return@launch
                    }

                    val request = TransferCarTokenRequest(
                        toAddress = recipientAddress,
                        amount = quantityInWei,
                        fromAddress = auth.walletAddress
                    )

                    val response = repository.transferCarTokens(auth.token, request)
                    if (response.isSuccessful) {
                        val result = response.body()?.result
                        val transactionId = result?.transactionId ?: result?.transactionHash
                        if (transactionId != null) {
                            sendCarTokenSuccess = transactionId
                            // CHANGED: Close dialog immediately after transaction success
                            delay(1500)
                            closeSendDialog()
                            // Start background verification for CAR token balance change
                            monitorCarTokenBalanceInBackground(auth.walletAddress, quantity)
                        } else {
                            error = "CAR token transfer failed - no transaction ID returned"
                        }
                    } else {
                        error = "Failed to transfer CAR tokens: ${response.message()}"
                    }
                } catch (e: Exception) {
                    error = "Network error: ${e.message}"
                } finally {
                    isSendingCarToken = false
                }
            }
        }
    }

    private fun monitorBalanceChangeInBackground(walletAddress: String, expectedChange: String) {
        viewModelScope.launch {
            val initialBalance = balance?.value

            repeat(60) { attempt ->
                delay(1000)
                try {
                    val response = repository.getBalance(walletAddress, selectedChain.chainId)
                    if (response.isSuccessful) {
                        val newBalances = response.body()?.result
                        val newBalance = newBalances?.firstOrNull()

                        if (newBalance?.value != initialBalance) {
                            balance = newBalance
                            loadWalletData()
                            // Show toast instead of dialog
                            // Note: We'll handle toast in the UI layer
                            return@launch
                        }
                    }
                } catch (e: Exception) {
                    // Continue polling
                }
            }
        }
    }

    private fun monitorCarTokenBalanceInBackground(walletAddress: String, expectedChange: String) {
        viewModelScope.launch {
            val initialCarBalance = carTokenBalance?.displayValue

            repeat(60) { attempt ->
                delay(1000)
                try {
                    val response = repository.getCarTokenBalance(walletAddress, selectedChain.chainId)
                    if (response.isSuccessful) {
                        val newCarBalance = response.body()

                        if (newCarBalance?.displayValue != initialCarBalance) {
                            carTokenBalance = newCarBalance
                            loadWalletData()
                            // Show toast for balance change
                            return@launch
                        }
                    }
                } catch (e: Exception) {
                    // Continue polling
                }
            }
        }
    }

    // ADD: CAR token verification function (same pattern as normal tokens)
    private fun verifyCarTokenTransaction(walletAddress: String, expectedChange: String) {
        viewModelScope.launch {
            isVerifyingTransaction = true
            val initialCarBalance = carTokenBalance?.displayValue

            repeat(60) { attempt ->
                delay(1000)

                try {
                    // Check CAR token balance change
                    val response = repository.getCarTokenBalance(walletAddress, selectedChain.chainId)
                    if (response.isSuccessful) {
                        val newCarBalance = response.body()

                        if (newCarBalance?.displayValue != initialCarBalance) {
                            carTokenBalance = newCarBalance
                            loadWalletData()
                            isVerifyingTransaction = false
                            // Auto-close dialog on success
                            delay(1500)
                            closeSendDialog()
                            return@launch
                        }
                    }

                    // Also try verify endpoint for CAR tokens
                    val verifyRequest = VerifyBalanceChangeRequest(
                        walletAddress = walletAddress,
                        chainId = selectedChain.chainId,
                        expectedChange = "-$expectedChange",
                        tokenAddress = "CAR" // or appropriate token address
                    )

                    val verifyResponse = repository.verifyBalanceChange(verifyRequest)
                    if (verifyResponse.isSuccessful && verifyResponse.body()?.result == true) {
                        loadWalletData()
                        isVerifyingTransaction = false
                        delay(1500)
                        closeSendDialog()
                        return@launch
                    }
                } catch (e: Exception) {
                    // Continue polling
                }
            }

            // Verification timed out
            isVerifyingTransaction = false
            error = "Transaction verification timed out - check transaction status manually"
        }
    }

    fun closeSendDialog() {
        showSendDialog = false
        // Reset states when closing
        sendTokenSuccess = null
        sendCarTokenSuccess = null
        error = null
        isSendingToken = false
        isSendingCarToken = false
        isVerifyingTransaction = false
    }

//    private fun loadNFTs(address: String) {
//        viewModelScope.launch {
//            try {
//                val response = repository.getNFTs(address, selectedChain.chainId)
//                if (response.isSuccessful) {
//                    nfts = response.body()?.result?.nfts ?: emptyList()
//                }
//            } catch (e: Exception) {
//                // Handle error silently for now
//            } finally {
//                checkIfAllDataLoaded()
//            }
//        }
//    }

    private fun loadBalance(address: String) {
        viewModelScope.launch {
            try {
                val response = repository.getBalance(address, selectedChain.chainId)
                if (response.isSuccessful) {
                    val balances = response.body()?.result
                    balance = balances?.firstOrNull()
                }
            } catch (e: Exception) {
                // Handle error silently for now
            } finally {
                checkIfAllDataLoaded()
            }
        }
    }

    private fun checkIfAllDataLoaded() {
        // Simple check - if we have made all calls, set loading to false
        if (tokens.isNotEmpty() || nfts.isNotEmpty() || balance != null) {
            isLoadingWalletData = false
        }
    }

    fun sendTokens(recipientAddress: String, quantity: String) {
        authResponse?.let { auth ->
            viewModelScope.launch {
                isSendingToken = true
                error = null
                sendTokenSuccess = null

                try {
                    val quantityInWei = try {
                        val quantityBigDecimal = quantity.toBigDecimal()
                        val weiMultiplier = BigDecimal.TEN.pow(18)
                        val weiAmount = quantityBigDecimal * weiMultiplier
                        weiAmount.toBigInteger().toString()
                    } catch (e: Exception) {
                        error = "Invalid amount format"
                        return@launch
                    }

                    val request = SendTokenRequest(
                        from = auth.walletAddress,
                        chainId = selectedChain.chainId,
                        recipients = listOf(
                            Recipient(
                                address = recipientAddress,
                                quantity = quantityInWei
                            )
                        )
                    )

                    val response = repository.sendTokens(auth.token, request)
                    if (response.isSuccessful) {
                        val result = response.body()?.result
                        val transactionId = result?.transactionIds?.firstOrNull()
                        if (transactionId != null) {
                            sendTokenSuccess = transactionId
                            // CHANGED: Close dialog immediately after transaction success
                            delay(1500) // Show success briefly
                            closeSendDialog()
                            // Start background verification for balance change toast
                            monitorBalanceChangeInBackground(auth.walletAddress, quantity)
                        } else {
                            error = "Transaction failed - no transaction ID returned"
                        }
                    } else {
                        error = "Failed to send tokens: ${response.message()}"
                    }
                } catch (e: Exception) {
                    error = "Network error: ${e.message}"
                } finally {
                    isSendingToken = false
                }
            }
        }
    }

    // Replace the existing verifyTransactionSuccess function:
    private fun verifyTransactionSuccess(walletAddress: String, expectedChange: String) {
        viewModelScope.launch {
            isVerifyingTransaction = true
            val initialBalance = balance?.value

            repeat(60) { attempt ->
                delay(1000)

                try {
                    // Check balance change
                    val response = repository.getBalance(walletAddress, selectedChain.chainId)
                    if (response.isSuccessful) {
                        val newBalances = response.body()?.result
                        val newBalance = newBalances?.firstOrNull()

                        if (newBalance?.value != initialBalance) {
                            balance = newBalance
                            loadWalletData()
                            isVerifyingTransaction = false
                            // Auto-close dialog on success
                            delay(1500) // Show success message briefly
                            closeSendDialog()
                            return@launch
                        }
                    }

                    // Also try verify endpoint
                    val verifyRequest = VerifyBalanceChangeRequest(
                        walletAddress = walletAddress,
                        chainId = selectedChain.chainId,
                        expectedChange = "-$expectedChange",
                        tokenAddress = null
                    )

                    val verifyResponse = repository.verifyBalanceChange(verifyRequest)
                    if (verifyResponse.isSuccessful && verifyResponse.body()?.result == true) {
                        loadWalletData()
                        isVerifyingTransaction = false
                        delay(1500)
                        closeSendDialog()
                        return@launch
                    }
                } catch (e: Exception) {
                    // Continue polling
                }
            }

            // Verification timed out but don't auto-close - let user decide
            isVerifyingTransaction = false
            error = "Transaction verification timed out - check transaction status manually"
        }
    }

    fun clearError() {
        error = null
    }

    fun clearSendTokenSuccess() {
        sendTokenSuccess = null
    }
}