package com.compose.base.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.base.data.repository.core.ConnectionState
import com.compose.base.domain.repository.core.NetworkConnectionRepository
import com.compose.base.domain.useCases.core.SubmitDeviceDataUseCase
import com.compose.base.domain.useCases.user.GetLoginStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AppLoginState {
    Init,
    UnAuthorized,
    UpdateError,
    BlockApp,
    ImmediateUpdate,
    ForceUpdate,
    FlexibleUpdate,
    Authorized,
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkRepo: NetworkConnectionRepository,
    private val submitDeviceData: SubmitDeviceDataUseCase,
    private val getLoginState: GetLoginStateUseCase,
) : ViewModel() {

    val connectionState: Flow<ConnectionState> = networkRepo.connectionState

    private val _appLoginState: MutableStateFlow<AppLoginState> =
        MutableStateFlow(AppLoginState.Init)
    val appLoginState: StateFlow<AppLoginState> = _appLoginState

    init {
        viewModelScope.launch {
            networkRepo.connectionState.filter { it == ConnectionState.Connected }.first().let {
                checkLoginState()
            }
        }
    }

    fun checkLoginState() = viewModelScope.launch {
        getLoginState().collect {
            _appLoginState.value = it
        }
    }

    fun updateAppLoginState(appLoginState: AppLoginState) {
        _appLoginState.value = appLoginState
    }

    fun updateToken(token: String) = submitDeviceData(token).launchIn(viewModelScope)

    fun checkConnection() = networkRepo.checkConnection()
}