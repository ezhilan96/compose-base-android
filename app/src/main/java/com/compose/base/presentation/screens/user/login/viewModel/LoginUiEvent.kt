package com.compose.base.presentation.screens.user.login.viewModel

import android.location.Location

sealed class LoginUiEvent {
    class OnPhoneChange(val phone: String) : LoginUiEvent()
    data object OnDisableOtpDeliveryMessage : LoginUiEvent()
    class OnLocationResult(val location: Location? = null) : LoginUiEvent()
    data object OnSubmitPhone : LoginUiEvent()
    class OnDismiss(val dismissAll: Boolean = false) : LoginUiEvent()
}