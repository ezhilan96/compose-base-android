package com.compose.base.presentation.screens.auth.login.viewModel

import com.compose.base.presentation.util.UiText
import java.io.Serializable

enum class LoginNavigationItem {
    REQUEST_CURRENT_LOCATION, // Request current location from LocalContext
    DONE, // Navigate to OTP screen
}

data class LoginScreenState(
    val phoneNumber: String = "",
    val isTermsChecked: Boolean = false, // terms and conditions checkbox state
    val enableOtpSentMessage: Boolean = false, // OTP success toast message state
    val successMessage: UiText? = null, // OTP success toast message state
    val inputErrorMessage: UiText? = null, // Local validation error state
) : Serializable






