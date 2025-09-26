package com.eth.vrcarnival.data.repository

import com.eth.vrcarnival.data.api.ApiService
import com.eth.vrcarnival.data.models.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun sendOtp(email: String) = apiService.sendOtp(SendOtpRequest(email))

    suspend fun verifyOtp(email: String, code: String) =
        apiService.verifyOtp(VerifyOtpRequest(email, code))

    suspend fun getTokens(address: String, chainId: Int) =
        apiService.getTokens(address, chainId)

    suspend fun getNFTs(address: String, chainId: Int) =
        apiService.getNFTs(address, chainId)

    suspend fun getBalance(address: String, chainId: Int) =
        apiService.getBalance(address, chainId)

    suspend fun sendTokens(token: String, request: SendTokenRequest) =
        apiService.sendTokens("Bearer $token", request)

    suspend fun verifyBalanceChange(request: VerifyBalanceChangeRequest) =
        apiService.verifyBalanceChange(request)

    suspend fun listTokens(chainId: Int?, limit: Int = 20, page: Int = 1, symbol: String? = null) =
        apiService.listTokens(chainId, limit, page, symbol)
}