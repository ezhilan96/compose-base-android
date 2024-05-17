package com.compose.base.domain.useCases.user

import com.compose.base.BuildConfig
import com.compose.base.data.util.DataState
import com.compose.base.domain.repository.HomeRepository
import com.compose.base.presentation.screens.AppLoginState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CheckForUpdateUseCase @Inject constructor(private val driverRepo: HomeRepository) {

    operator fun invoke(): Flow<AppLoginState> = driverRepo.getAppConfig().map { dataState ->
        when (dataState) {

            is DataState.InProgress -> {
                AppLoginState.Init
            }

            is DataState.Success -> {
                val driverAppConfig =
                    dataState.data.KillSwitch?.firstOrNull { it.`package` == BuildConfig.APPLICATION_ID }
                val isFlexibleUpdate = driverAppConfig?.isPartialUpdate ?: false
                val isImmediateUpdate = driverAppConfig?.isForceUpdate ?: false
                val isForceUpdate = driverAppConfig?.block ?: false
                val updateVersionCode = driverAppConfig?.versionCode?.toIntOrNull()
                if (updateVersionCode != null && updateVersionCode > BuildConfig.VERSION_CODE) {
                    when {
                        isForceUpdate -> AppLoginState.BlockApp
                        isImmediateUpdate -> AppLoginState.ImmediateUpdate
                        isFlexibleUpdate -> AppLoginState.FlexibleUpdate
                        else -> AppLoginState.Authorized
                    }
                } else {
                    AppLoginState.Authorized
                }
            }

            is DataState.Error -> {
                AppLoginState.UpdateError
            }
        }
    }
}