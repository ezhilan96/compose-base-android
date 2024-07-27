package com.compose.base.domain.useCases.user

import com.compose.base.data.model.remote.request.DeviceDataSubmitRequest
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.entity.UserData
import com.compose.base.domain.repository.core.PreferencesRepository
import com.compose.base.domain.repository.user.AuthRepository
import com.compose.base.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import retrofit2.Response
import javax.inject.Inject

/**
 * Class responsible for handling various user-related actions within the application.
 * It interacts with multiple repositories to manage user login status, user details,
 * device data, and location.
 */
class UserActionsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository,
) {

    /**
     * Flow of booleans representing the user's login status (logged in or not).
     */
    val loginStatusFlow: Flow<Boolean> = userRepository.loginStatusFlow

    /**
     * Flow of `UserData` objects containing user details, or null if not logged in.
     */
    val userDetails: Flow<UserData?> = userRepository.userDetailFlow

    /**
     * Updates the user's FCM token on the server.
     *
     * @param token: The new device token as a String.
     */
    fun updateDeviceToken(token: String) =
        userRepository.submitDeviceData(DeviceDataSubmitRequest(deviceId = token))

    /**
     * Initiates user logout and manages local data.
     *
     * @return: A Flow of `DataResponse<Response<Unit>>` representing the logout response from the server.
     *      - Success: Indicates successful logout with an empty response object.
     *      - Error: Indicates any errors encountered during logout.
     */
    fun logout(): Flow<DataResponse<Response<Unit>>> = authRepository.logout().onEach {
        if (it is DataResponse.Success) {
            preferencesRepository.logout()
        }
    }
}