package com.compose.base.presentation.screens.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class OtpState(
    val resendOtpCountDown: Int = 0,
    val enableAutoRead: Boolean = false,
    val autoReadOtp: String = "",
)

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtpState())
    val uiState: StateFlow<OtpState> = _uiState

    fun onOtpSent() {
        _uiState.update { currentState ->
            currentState.copy(enableAutoRead = true)
        }
    }

    fun updateResendCountDown(count: Int) {
        _uiState.update { currentState ->
            currentState.copy(resendOtpCountDown = count)
        }
    }

    fun onDisableAutoRead() {
        _uiState.update { currentState ->
            currentState.copy(enableAutoRead = false)
        }
    }

    fun onAutoOtpReceived(otp: String) {
        _uiState.update { currentState ->
            currentState.copy(
                autoReadOtp = otp,
                enableAutoRead = false,
            )
        }
    }
}