package com.compose.base.presentation.screens.user.otp.viewModel

import com.compose.base.presentation.screens.core.ScreenUiState
import com.compose.base.presentation.util.UiText

enum class OtpNavigationItem {
    OTP_TOAST,
    AUTO_READ_BOTTOM_SHEET,
    ALERT_DIALOG,
    DONE,
}

data class OtpScreenUiState(
    override val isLoading: Boolean = false,
    override val navigationItems: MutableList<OtpNavigationItem> = mutableListOf(OtpNavigationItem.AUTO_READ_BOTTOM_SHEET),
    val phoneNumber: String = "",
    val otp: String = "",
    val remainingTimeToResendOtp: Int = 0,
    val inputErrorMessage: UiText? = null,
    override val alertMessage: UiText = UiText.Value(),
) : ScreenUiState {
    override fun copyWith(isLoading: Boolean) = copy(isLoading = isLoading)
}