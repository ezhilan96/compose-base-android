package com.compose.base.presentation.screens.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.useCases.user.UserActionsUseCase
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
    private val userActions: UserActionsUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow(ProfileScreenUiState())
    val uiState: StateFlow<ProfileScreenUiState>
        get() = _uiState

    init {
        viewModelScope.launch {
            userActions.userDetails.collect {
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
            userActions.logout().collect { dataResponse ->
                if (dataResponse is DataResponse.Error) _uiState.update { currentState ->
                    currentState.copy(
                        alertMessage = dataResponse.message,
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