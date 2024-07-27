package com.compose.base.data.model.remote.response

data class PhoneVerificationResponse(
    val `data`: Data?,
    val message: String?,
)

data class Data(
    val countryCode: String?,
    val phone: Long?,
)

data class OTPVerificationResponse(
    val id: Int,
    val token: String,
    val userName: String?,
    val phone: Long?,
)