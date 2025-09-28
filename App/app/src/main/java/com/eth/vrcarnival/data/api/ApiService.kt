package com.eth.vrcarnival.data.api

import com.eth.vrcarnival.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Keep all existing working endpoints
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<Unit>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    @GET("v1/wallets/{address}/tokens")
    suspend fun getTokens(
        @Path("address") address: String,
        @Query("chainId") chainId: Int,
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1
    ): Response<ApiResponse<TokensResponse>>

    @GET("v1/wallets/{address}/balance")
    suspend fun getBalance(
        @Path("address") address: String,
        @Query("chainId") chainId: Int
    ): Response<ApiResponse<List<WalletBalance>>>

    @POST("v1/wallets/send")
    suspend fun sendTokens(
        @Header("Authorization") authorization: String,
        @Body request: SendTokenRequest
    ): Response<ApiResponse<SendTransactionResponse>>

    @POST("v1/verify-balance-change")
    suspend fun verifyBalanceChange(
        @Body request: VerifyBalanceChangeRequest
    ): Response<ApiResponse<Boolean>>

    @GET("v1/tokens")
    suspend fun listTokens(
        @Query("chainId") chainId: Int? = null,
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1,
        @Query("symbol") symbol: String? = null
    ): Response<ApiResponse<ListTokensResponse>>

    // KEEP: Working CAR token endpoints
    @GET("v1/wallets/{address}/token-balance")
    suspend fun getCarTokenBalance(
        @Path("address") address: String,
        @Query("chainId") chainId: Int
    ): Response<CarTokenBalance>

    @POST("v1/contracts/car-token/transfer")
    suspend fun transferCarTokens(
        @Header("Authorization") authorization: String,
        @Body request: TransferCarTokenRequest
    ): Response<ApiResponse<TransferCarTokenResponse>>

    // ADD: Unity integration endpoint
    @POST("ismobile")
    suspend fun sendWalletToUnity(@Body request: UnityIntegrationRequest): Response<Unit>

    // ADD: NFT endpoints to replace dummy data
    @GET("v1/contracts/{contractAddress}/universal-nfts")
    suspend fun getUniversalNFTs(
        @Path("contractAddress") contractAddress: String,
        @Query("walletAddress") walletAddress: String
    ): Response<NFTContractResponse>

    @GET("v1/contracts/{contractAddress}/erc1155/metadata/{tokenId}")
    suspend fun getNFTMetadata(
        @Path("contractAddress") contractAddress: String,
        @Path("tokenId") tokenId: String
    ): Response<NFTMetadataResponse>
}