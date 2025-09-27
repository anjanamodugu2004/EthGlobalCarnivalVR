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
import javax.inject.Inject

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

    var balance by mutableStateOf<WalletBalance?>(null)
        private set

    var availableTokens by mutableStateOf<List<TokenInfo>>(emptyList())
        private set

    var isSendingToken by mutableStateOf(false)
        private set

    var sendTokenSuccess by mutableStateOf<String?>(null)
        private set

    var isVerifyingTransaction by mutableStateOf(false)
        private set

    var isLoadingWalletData by mutableStateOf(false)
        private set

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

        // Load new data for selected chain
        loadWalletData()
        loadAvailableTokens()
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
                    // Convert quantity to wei (multiply by 10^18 for ETH)
                    val quantityInWei = try {
                        val quantityDouble = quantity.toDouble()
                        val wei = (quantityDouble * Math.pow(10.0, 18.0)).toLong()
                        wei.toString()
                    } catch (e: Exception) {
                        quantity // Use original if conversion fails
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
                            // Start balance verification
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

    private fun verifyTransactionSuccess(walletAddress: String, expectedChange: String) {
        viewModelScope.launch {
            isVerifyingTransaction = true

            // Store initial balance for comparison
            val initialBalance = balance?.value

            // Poll for 30 seconds to verify balance change
            repeat(30) { attempt ->
                delay(1000) // Wait 1 second between checks

                try {
                    // Reload balance
                    val response = repository.getBalance(walletAddress, selectedChain.chainId)
                    if (response.isSuccessful) {
                        val newBalances = response.body()?.result
                        val newBalance = newBalances?.firstOrNull()

                        // Compare with initial balance
                        if (newBalance?.value != initialBalance) {
                            // Balance changed, transaction likely successful
                            balance = newBalance
                            loadWalletData() // Refresh all wallet data
                            isVerifyingTransaction = false
                            return@launch
                        }
                    }

                    // Also try the verify balance change endpoint
                    val verifyRequest = VerifyBalanceChangeRequest(
                        walletAddress = walletAddress,
                        chainId = selectedChain.chainId,
                        expectedChange = "-$expectedChange",
                        tokenAddress = null // For native token
                    )

                    val verifyResponse = repository.verifyBalanceChange(verifyRequest)
                    if (verifyResponse.isSuccessful && verifyResponse.body()?.result == true) {
                        // Transaction verified successfully
                        loadWalletData() // Refresh wallet data
                        isVerifyingTransaction = false
                        return@launch
                    }
                } catch (e: Exception) {
                    // Continue polling on error
                }
            }

            // If we reach here, verification failed/timed out
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