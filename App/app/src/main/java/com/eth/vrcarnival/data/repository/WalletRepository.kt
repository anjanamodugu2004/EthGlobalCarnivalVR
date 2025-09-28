package com.eth.vrcarnival.data.repository

import com.eth.vrcarnival.data.api.ApiService
import com.eth.vrcarnival.data.models.*
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Response

@Singleton
class WalletRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun sendOtp(email: String) = apiService.sendOtp(SendOtpRequest(email))

    suspend fun verifyOtp(email: String, code: String) =
        apiService.verifyOtp(VerifyOtpRequest(email, code))

    suspend fun getTokens(address: String, chainId: Int) =
        apiService.getTokens(address, chainId)

    suspend fun getBalance(address: String, chainId: Int) =
        apiService.getBalance(address, chainId)

    suspend fun sendTokens(token: String, request: SendTokenRequest) =
        apiService.sendTokens("Bearer $token", request)

    suspend fun verifyBalanceChange(request: VerifyBalanceChangeRequest) =
        apiService.verifyBalanceChange(request)

    suspend fun listTokens(chainId: Int?, limit: Int = 20, page: Int = 1, symbol: String? = null) =
        apiService.listTokens(chainId, limit, page, symbol)

    suspend fun getCarTokenBalance(address: String, chainId: Int) =
        apiService.getCarTokenBalance(address, chainId)

    suspend fun transferCarTokens(token: String, request: TransferCarTokenRequest) =
        apiService.transferCarTokens("Bearer $token", request)

    suspend fun sendWalletToUnity(email: String, walletAddress: String, token: String) =
        apiService.sendWalletToUnity(UnityIntegrationRequest(email, walletAddress, token))

    suspend fun getUniversalNFTs(contractAddress: String, walletAddress: String) =
        apiService.getUniversalNFTs(contractAddress, walletAddress)

    suspend fun getNFTMetadata(contractAddress: String, tokenId: String) =
        apiService.getNFTMetadata(contractAddress, tokenId)

    suspend fun saveToFilecoin(request: FilecoinSaveRequest): Response<FilecoinSaveResponse> {
        return apiService.saveToFilecoin(request)
    }

    suspend fun getUserFilecoinData(email: String): Response<FilecoinUserDataResponse> {
        return apiService.getUserFilecoinData(email)
    }
}