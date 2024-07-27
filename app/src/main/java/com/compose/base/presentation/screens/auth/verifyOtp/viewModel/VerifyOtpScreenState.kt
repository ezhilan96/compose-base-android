package com.compose.base.presentation.screens.auth.verifyOtp.viewModel

import com.compose.base.presentation.util.UiText
import java.io.Serializable

enum class OtpNavigationItem {
    OTP_TOAST, // Show resend OTP success toast message
    DONE, // Navigate to Home screen / logins User
}

enum class OtpAnnotations { Phone, Resend }

data class VerifyOtpScreenState(
    val phoneNumber: String = "",
    val otp: String = "",
    val successMessage: UiText? = null, // Resend OTP success toast message state
    val inputErrorMessage: UiText? = null, // Local validation error state
) : Serializable