package com.compose.base.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.base.data.repository.core.ConnectionState
import com.compose.base.domain.repository.core.NetworkConnectionRepository
import com.compose.base.domain.useCases.user.GetLoginStateUseCase
import com.compose.base.domain.useCases.user.UserActionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * An enum class representing different application states.
 *
 * This enum defines various states that the application can be in at any given time. It provides
 * a structured way to represent the current app status and potentially trigger actions based on
 * the state change.
 */
enum class AppState {
    Init, UnAuthorized, ConfigError, BlockApp, ImmediateUpdate, FlexibleUpdate, Authorized,
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkConnectionRepository: NetworkConnectionRepository,
    private val getLoginState: GetLoginStateUseCase,
    private val userActions: UserActionsUseCase,
) : ViewModel() {

    // Network connection state
    val connectionState: Flow<ConnectionState> = networkConnectionRepository.connectionState

    private val _appState: MutableStateFlow<AppState> =
        MutableStateFlow(AppState.Init)
    val appState: StateFlow<AppState> = _appState

    // Check AppLoginState once the network connection is established,
    // so that the GetLoginStateUseCase can check AppConfig for ForceUpdate.
    init {
        viewModelScope.launch {
            networkConnectionRepository.connectionState.filter { it == ConnectionState.Connected }
                .first().let {
                    checkLoginState()
                }
        }
    }

    fun checkLoginState() = viewModelScope.launch {
        getLoginState().collect {
            _appState.value = it
        }
    }

    fun updateAppLoginState(appState: AppState) {
        _appState.value = appState
    }

    // Update FCM token
    fun updateToken(token: String) = userActions.updateDeviceToken(token).launchIn(viewModelScope)

    fun checkConnection() = networkConnectionRepository.checkConnection()
}