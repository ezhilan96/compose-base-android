package com.compose.base.data.model.remote.request

data class GetOtpRequest(
    val phone: Long,
    val lat: Double?,
    val lng: Double?,
)

data class OTPVerificationRequest(
    val phone: Long?,
    val otp: Int?,
)