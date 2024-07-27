@file:OptIn(ExperimentalCoroutinesApi::class)

package com.compose.base.domain.useCases.user

import com.compose.base.domain.repository.user.UserRepository
import com.compose.base.presentation.screens.AppState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Use case class responsible for determining the user's login state
 * and overall application state based on login status and app configuration.
 * This class combines information from the user repository's `loginStatusFlow` and
 * `getAppConfig` functions to determine the appropriate `AppState` for the application.
 */
class GetLoginStateUseCase @Inject constructor(private val userRepository: UserRepository) {

    /**
     * Initiates the process to retrieve the current login state and application state.
     *
     * @return: A Flow of `AppState` indicating the overall application state based on login status and app configuration.
     *      - UnAuthorized: User is not logged in.
     *      - Authorized: User is logged in and app configuration allows access.
     *      - ConfigError: Error occurred while retrieving app configuration.
     *      - BlockApp: App is blocked by the server according to app configuration.
     *      - ImmediateUpdate: A forced update is required based on app configuration.
     *      - FlexibleUpdate: An optional update is available based on app configuration.
     *      - Init: Initial state while fetching app configuration (only emitted once).
     */
    operator fun invoke(): Flow<AppState> =
        userRepository.loginStatusFlow
            .flatMapLatest { isLoggedIn ->
                if (isLoggedIn) {
//                userRepository.getAppConfig().map { dataResponse ->
//                    when (dataResponse) {
//
//                        is DataResponse.InProgress -> AppState.Init
//
//                        is DataResponse.Success -> {
//                            val driverAppConfig =
//                                dataResponse.data.KillSwitch?.firstOrNull { it.`package` == BuildConfig.APPLICATION_ID }
//                            val isFlexibleUpdate = driverAppConfig?.isPartialUpdate ?: false
//                            val isImmediateUpdate = driverAppConfig?.isForceUpdate ?: false
//                            val isBlockApp = driverAppConfig?.isBlockApp ?: false
//                            val updateVersionCode = driverAppConfig?.versionCode?.toIntOrNull()
//                            if (updateVersionCode != null && updateVersionCode > BuildConfig.VERSION_CODE) {
//                                when {
//                                    isBlockApp -> AppState.BlockApp
//                                    isImmediateUpdate -> AppState.ImmediateUpdate
//                                    isFlexibleUpdate -> AppState.FlexibleUpdate
//                                    else -> AppState.Authorized
//                                }
//                            } else {
//                                AppState.Authorized
//                            }
//                        }
//
//                        is DataResponse.Error -> AppState.ConfigError
//                    }
//                }
                    flowOf(AppState.Authorized)
                } else {
                    flowOf(AppState.UnAuthorized)
                }
            }
}