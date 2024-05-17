package com.compose.base.presentation.screens.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.base.data.util.DataState
import com.compose.base.domain.useCases.core.GetUserDetailsUseCase
import com.compose.base.domain.useCases.user.LogoutUserUseCase
import com.compose.base.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileScreenUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val rating: String = "",
    val enableAlert: Boolean = false,
    val alertMessage: UiText = UiText.Value(),
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserDetails: GetUserDetailsUseCase,
    private val logoutUser: LogoutUserUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow(ProfileScreenUiState())
    val uiState: StateFlow<ProfileScreenUiState>
        get() = _uiState

    init {
        viewModelScope.launch {
            getUserDetails().collect {
                it?.let {
                    _uiState.update { currentState ->
                        currentState.copy(
                            name = it.name,
                            phoneNumber = it.phone,
                        )
                    }
                }
            }
        }
    }

    fun onLogout() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            logoutUser().collect { dataState ->
                if (dataState is DataState.Error) _uiState.update { currentState ->
                    currentState.copy(
                        alertMessage = dataState.message,
                        enableAlert = true,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onDismissAlert() = _uiState.update { currentState ->
        currentState.copy(enableAlert = false)
    }
}