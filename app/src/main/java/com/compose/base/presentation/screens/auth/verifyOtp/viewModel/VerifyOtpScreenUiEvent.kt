package com.compose.base.presentation.screens.auth.verifyOtp.viewModel

sealed class VerifyOtpScreenUiEvent {

    class OnOTPChanged(val otp: String) : VerifyOtpScreenUiEvent() // Changes OTP

    data object ResendOtp : VerifyOtpScreenUiEvent() // Resends OTP

    data object OnSubmitOtp : VerifyOtpScreenUiEvent() // Submits OTP

    data object OnDismiss : VerifyOtpScreenUiEvent() // Dismisses navigation items
}