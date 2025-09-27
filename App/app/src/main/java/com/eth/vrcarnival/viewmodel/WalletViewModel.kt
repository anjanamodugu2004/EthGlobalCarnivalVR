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

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository,
    private val authManager: AuthManager
) : ViewModel() {

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
                    loadAvailableTokens()
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

    fun loadGameNFTs() {
        // Only load NFTs on Sepolia testnet
        if (selectedChain.chainId == 11155111) {
            gameNFTs = GameNFTData.getGameNFTs()
        } else {
            gameNFTs = emptyList()
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
        loadAvailableTokens()
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
            loadNFTs(auth.walletAddress)
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

    fun loadAvailableTokens() {
        viewModelScope.launch {
            try {
                val response = repository.listTokens(selectedChain.chainId)
                if (response.isSuccessful) {
                    // Access the tokens directly from the response
                    val responseTokens = response.body()?.result?.tokens ?: emptyList()
                    availableTokens = responseTokens.map { token ->
                        TokenInfo(
                            address = token.token_address,
                            name = token.name,
                            symbol = token.symbol,
                            decimals = token.decimals,
                            chainId = token.chain_id
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error silently for now
            }
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
                    // Convert to wei (CAR token has 18 decimals)
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
                            // Refresh CAR token balance
                            loadCarToken(auth.walletAddress)
                            delay(1500) // Show success briefly
                            closeSendDialog()
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

    private fun loadNFTs(address: String) {
        viewModelScope.launch {
            try {
                val response = repository.getNFTs(address, selectedChain.chainId)
                if (response.isSuccessful) {
                    nfts = response.body()?.result?.nfts ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error silently for now
            } finally {
                checkIfAllDataLoaded()
            }
        }
    }

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
                    // Better wei conversion using BigDecimal to avoid overflow
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
                            // Start verification and auto-close dialog when done
                            verifyTransactionSuccess(auth.walletAddress, quantity)
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