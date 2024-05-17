package com.compose.base.presentation.screens.user.otp.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.compose.base.data.util.DataState
import com.compose.base.domain.repository.AuthRepository
import com.compose.base.domain.useCases.user.GetOtpUseCase
import com.compose.base.domain.useCases.user.VerifyOtpUseCase
import com.compose.base.presentation.screens.core.ScreenViewModel
import com.compose.base.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    authRepo: AuthRepository,
    savedStateHandle: SavedStateHandle,
    private val triggerOtp: GetOtpUseCase,
    private val verifyOtp: VerifyOtpUseCase,
) : ScreenViewModel<OtpScreenUiState, OtpScreenUiEvent>(OtpScreenUiState(), savedStateHandle) {

    init {
        authRepo.autoReadOtp.onEach { otp ->
            if (uiState.value.navigationItems.contains(OtpNavigationItem.AUTO_READ_BOTTOM_SHEET)) {
                if (otp != null) {
                    if (otp.isNotEmpty()) {
                        updateUiState { currentState ->
                            currentState.copy(otp = otp)
                        }
                        submitOtp()
                    } else {
                        updateUiState { currentState ->
                            currentState.copy(
                                navigationItems = currentState.navigationItems.toMutableList()
                                    .apply { removeLast() },
                            )
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
        triggerOtp.remainingTimeToResendOtp.onEach {
            updateUiState { currentState ->
                currentState.copy(
                    remainingTimeToResendOtp = it
                )
            }
        }.launchIn(viewModelScope)
    }

    fun setPhoneNumber(phoneNumber: String) {
        updateUiState { currentState -> currentState.copy(phoneNumber = phoneNumber) }
    }

    override fun onUiEvent(event: OtpScreenUiEvent) {
        when (event) {

            is OtpScreenUiEvent.OnOTPChange -> {
                updateUiState { currentState ->
                    currentState.copy(
                        otp = event.otp,
                        inputErrorMessage = null,
                    )
                }
            }

            OtpScreenUiEvent.ResendOtp -> resendOtp()

            OtpScreenUiEvent.OnSubmitOtp -> submitOtp()

            is OtpScreenUiEvent.OnDismiss -> {
                updateUiState { currentState ->
                    currentState.copy(
                        navigationItems = currentState.navigationItems.toMutableList().apply {
                            if (event.dismissAll) clear()
                            else if (isNotEmpty()) removeLast()
                        },
                    )
                }
            }
        }
    }

    private fun resendOtp() {
        viewModelScope.launch {
            triggerOtp(phoneNumber = uiState.value.phoneNumber).collectDataState { dataState ->
                updateUiState { currentState ->
                    currentState.copy(
                        alertMessage = dataState.data,
                        navigationItems = currentState.navigationItems.toMutableList().apply {
                            add(OtpNavigationItem.OTP_TOAST)
                        },
                    )
                }
            }
        }
    }

    private fun submitOtp() {
        viewModelScope.launch {
            verifyOtp(
                otp = uiState.value.otp,
                phoneNumber = uiState.value.phoneNumber,
            ).collectDataState(
                onFailure = { error ->
                    if (error is DataState.Error.Local) {
                        updateUiState { currentState ->
                            currentState.copy(inputErrorMessage = error.message)
                        }
                    } else {
                        showAlert(error.message)
                    }
                },
            ) {
                updateUiState { currentState ->
                    currentState.copy(
                        navigationItems = currentState.navigationItems.toMutableList()
                            .apply { add(OtpNavigationItem.DONE) },
                    )
                }
            }
        }
    }

    override fun showAlert(message: UiText) {
        updateUiState { currentState ->
            currentState.copy(
                navigationItems = currentState.navigationItems.toMutableList().apply {
                    add(OtpNavigationItem.ALERT_DIALOG)
                },
                alertMessage = message,
            )
        }
    }

    override fun onStop() {
        updateUiState { it.copy(navigationItems = mutableListOf()) }
    }
}