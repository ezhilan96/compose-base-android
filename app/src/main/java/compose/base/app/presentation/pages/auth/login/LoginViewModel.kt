package compose.base.app.presentation.pages.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import compose.base.app.MainRoutes
import compose.base.app.config.util.NetworkResponse
import compose.base.app.data.model.response.Error
import compose.base.app.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    var isLoading: Boolean = false,
    var userName: String = "",
    var error: String = "",
)

sealed class LoginUiEvent {

    data class OnUserNameChange(val name: String) : LoginUiEvent()

    object OnSubmit : LoginUiEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase) : ViewModel() {

    private var _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState>
        get() = _loginUiState

    lateinit var navController: NavController

    fun handleEvent(event: LoginUiEvent) {
        if (!_loginUiState.value.isLoading) {
            when (event) {
                is LoginUiEvent.OnUserNameChange -> {
                    _loginUiState.update { currentState -> currentState.copy(userName = event.name) }
                }

                LoginUiEvent.OnSubmit -> {
                    _loginUiState.update { currentState -> currentState.copy(isLoading = true) }
                    formSubmit()
                }
            }
        }
    }

    private fun formSubmit() {
        viewModelScope.launch {
            loginUseCase(_loginUiState.value.userName).collect { response ->
                when (response) {
                    is NetworkResponse.Success -> navController.navigate(
                        MainRoutes.LoginScreen.route + "/" + response.responseData.token
                    )

                    is NetworkResponse.Error -> _loginUiState.update { currentState ->
                        currentState.copy(
                            error = response.message
                        )
                    }

                    is NetworkResponse.Exception -> _loginUiState.update { currentState ->
                        currentState.copy(
                            error = response.e.message ?: Error().message
                        )
                    }
                }
            }
        }
    }
}