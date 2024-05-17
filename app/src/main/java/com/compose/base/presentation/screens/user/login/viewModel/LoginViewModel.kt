package com.compose.base.presentation.screens.user.login.viewModel

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.compose.base.data.util.DataState
import com.compose.base.domain.useCases.user.GetOtpUseCase
import com.compose.base.presentation.screens.core.ScreenViewModel
import com.compose.base.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getOtp: GetOtpUseCase,
) : ScreenViewModel<LoginScreenUiState, LoginUiEvent>(LoginScreenUiState(), savedStateHandle) {

    override fun onUiEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnPhoneChange -> {
                updateUiState { currentState ->
                    currentState.copy(
                        phoneNumber = event.phone,
                        inputErrorMessage = null,
                    )
                }
            }

            is LoginUiEvent.OnLocationResult -> {
                updateUiState { currentState ->
                    currentState.copy(
                        navigationItems = currentState.navigationItems.toMutableList().apply {
                            remove(LoginNavigationItem.REQUEST_CURRENT_LOCATION)
                        },
                    )
                }
                submitPhone(event.location)
            }

            LoginUiEvent.OnSubmitPhone -> submitPhone()

            LoginUiEvent.OnDisableOtpDeliveryMessage -> updateUiState { currentState ->
                currentState.copy(enableOtpDeliveryMessage = false)
            }

            is LoginUiEvent.OnDismiss -> {
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

    private fun submitPhone(location: Location? = null) {
        viewModelScope.launch {
            getOtp(
                phoneNumber = uiState.value.phoneNumber,
                location = location,
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
            ) { dataState ->
                updateUiState { currentState ->
                    currentState.copy(
                        navigationItems = currentState.navigationItems.toMutableList()
                            .apply { add(LoginNavigationItem.DONE) },
                        alertMessage = dataState.data,
                        enableOtpDeliveryMessage = true,
                    )
                }
            }
        }
    }

    override fun showAlert(message: UiText) {
        updateUiState { currentState ->
            currentState.copy(
                navigationItems = currentState.navigationItems.toMutableList().apply {
                    add(LoginNavigationItem.ALERT_DIALOG)
                },
                alertMessage = message,
            )
        }
    }

    override fun onStop() {
        updateUiState { it.copy(navigationItems = mutableListOf()) }
    }
}