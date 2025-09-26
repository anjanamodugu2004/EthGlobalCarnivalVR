package com.eth.vrcarnival.data.models

data class SendOtpRequest(
    val email: String
)

data class VerifyOtpRequest(
    val email: String,
    val code: String
)

data class AuthResponse(
    val isNewUser: Boolean,
    val token: String,
    val type: String,
    val walletAddress: String
)