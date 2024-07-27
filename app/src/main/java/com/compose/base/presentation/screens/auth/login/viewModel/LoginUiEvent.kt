package com.compose.base.presentation.screens.auth.login.viewModel

import android.location.Location

sealed class LoginUiEvent {

    class OnPhoneChange(val phone: String) : LoginUiEvent()// Changes Phone number

    data object OnDisableOtpSentMessage :
        LoginUiEvent()// Prevents from showing OTP success toast message once it's displayed

    class OnLocationResult(val location: Location? = null) : LoginUiEvent()// Sets Location result

    data class OnTermsCheckChanged(val checked: Boolean) :
        LoginUiEvent()// Changes Terms and conditions check state

    data object OnSubmitPhone : LoginUiEvent()// Submits phone number

    data object OnDismiss : LoginUiEvent()// Dismisses navigation items
}