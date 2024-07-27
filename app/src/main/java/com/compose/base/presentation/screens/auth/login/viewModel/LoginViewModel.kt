package com.compose.base.presentation.screens.auth.login.viewModel

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.useCases.user.GetOtpUseCase
import com.compose.base.presentation.screens.core.ScreenViewModel
import com.compose.base.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PhoneAnnotations { terms, policy }

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getOtp: GetOtpUseCase,
) : ScreenViewModel<LoginScreenState, LoginUiEvent>(LoginScreenState(), savedStateHandle) {

    lateinit var getResendOtpCountDown: () -> Int

    override fun onUiEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnPhoneChange -> {
                updateScreenState { currentState ->
                    currentState.copy(
                        phoneNumber = event.phone,
                        inputErrorMessage = null,
                    )
                }
            }

            is LoginUiEvent.OnLocationResult -> {
                popStack()
                submitPhone(event.location)
            }

            is LoginUiEvent.OnTermsCheckChanged -> {
                updateScreenState { currentState ->
                    currentState.copy(
                        isTermsChecked = event.checked,
                        inputErrorMessage = null,
                    )
                }
            }

            LoginUiEvent.OnSubmitPhone -> {
                if (screenState.isTermsChecked) {
                    submitPhone()
                } else {
                    updateScreenState { currentState ->
                        currentState.copy(inputErrorMessage = UiText.Value("Please accept terms and conditions"))
                    }
                }
            }

            LoginUiEvent.OnDisableOtpSentMessage -> updateScreenState { currentState ->
                currentState.copy(enableOtpSentMessage = false)
            }

            is LoginUiEvent.OnDismiss -> popStack()
        }
    }

    private fun submitPhone(location: Location? = null) {
        viewModelScope.launch {
            getOtp(
                phoneNumber = screenState.phoneNumber,
                location = location,
                resendOtpCountDown = getResendOtpCountDown(),
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
            ) { dataResponse ->
                pushStack(LoginNavigationItem.DONE)
                updateScreenState { currentState ->
                    currentState.copy(
                        successMessage = dataResponse.data,
                        enableOtpSentMessage = true,
                    )
                }
            }
        }
    }
}