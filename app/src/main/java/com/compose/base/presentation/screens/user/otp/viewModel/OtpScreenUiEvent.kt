package com.compose.base.presentation.screens.user.otp.viewModel

sealed class OtpScreenUiEvent {
    class OnOTPChange(val otp: String) : OtpScreenUiEvent()
    data object ResendOtp : OtpScreenUiEvent()
    data object OnSubmitOtp : OtpScreenUiEvent()
    class OnDismiss(val dismissAll: Boolean = false) : OtpScreenUiEvent()
}