package com.compose.base.presentation.screens.auth.verifyOtp.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.entity.UserVerificationStatus
import com.compose.base.domain.useCases.user.GetOtpUseCase
import com.compose.base.domain.useCases.user.VerifyOtpUseCase
import com.compose.base.presentation.screens.core.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getOtp: GetOtpUseCase,
    private val verifyOtp: VerifyOtpUseCase,
) : ScreenViewModel<VerifyOtpScreenState, VerifyOtpScreenUiEvent>(
    VerifyOtpScreenState(),
    savedStateHandle
) {

    // Getter for getResendOtpCountDown set by AuthNavHost
    lateinit var getResendOtpCountDown: () -> Int

    // Used to get the phone number from the previous screen set by AuthNavHost
    fun setPhoneNumber(phoneNumber: String) {
        updateScreenState { currentState ->
            currentState.copy(phoneNumber = phoneNumber)
        }
    }

    override fun onUiEvent(event: VerifyOtpScreenUiEvent) {
        when (event) {

            is VerifyOtpScreenUiEvent.OnOTPChanged -> {
                updateScreenState { currentState ->
                    currentState.copy(
                        otp = event.otp,
                        inputErrorMessage = null,
                    )
                }
            }

            VerifyOtpScreenUiEvent.ResendOtp -> resendOtp()

            VerifyOtpScreenUiEvent.OnSubmitOtp -> submitOtp()

            VerifyOtpScreenUiEvent.OnDismiss -> popStack()
        }
    }

    private fun resendOtp() {
        viewModelScope.launch {
            getOtp(
                phoneNumber = screenState.phoneNumber,
                resendOtpCountDown = getResendOtpCountDown(),
            ).collectDataResponse { dataResponse ->
                updateScreenState { currentState ->
                    currentState.copy(
                        successMessage = dataResponse.data,
                    )
                }
                pushStack(OtpNavigationItem.OTP_TOAST)
            }
        }
    }

    private fun submitOtp() {
        viewModelScope.launch {
            verifyOtp(
                otp = screenState.otp,
                phoneNumber = screenState.phoneNumber,
            ).collectDataResponse(
                onFailure = { error ->
                    // If the error is Input validation error show error in support text
                    if (error is DataResponse.Error.Local) {
                        updateScreenState { currentState ->
                            currentState.copy(inputErrorMessage = error.message)
                        }
                    } else {
                        showAlert(error.message)
                    }
                },
            ) {
                pushStack(OtpNavigationItem.DONE)
            }
        }
    }
}