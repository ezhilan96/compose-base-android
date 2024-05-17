package com.compose.base.presentation.screens.user.login.viewModel

import com.compose.base.presentation.screens.core.ScreenUiState
import com.compose.base.presentation.util.UiText

enum class LoginNavigationItem {
    ALERT_DIALOG,
    REQUEST_CURRENT_LOCATION,
    DONE,
}

data class LoginScreenUiState(
    override val isLoading: Boolean = false,
    override val navigationItems: MutableList<LoginNavigationItem> = mutableListOf(),
    val phoneNumber: String = "",
    val enableOtpDeliveryMessage: Boolean = false,
    val inputErrorMessage: UiText? = null,
    override val alertMessage: UiText = UiText.Value(),
) : ScreenUiState {
    override fun copyWith(isLoading: Boolean) = copy(isLoading = isLoading)
}






