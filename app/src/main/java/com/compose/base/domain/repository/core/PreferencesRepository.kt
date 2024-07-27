package com.compose.base.domain.repository.core

import com.compose.base.data.model.remote.response.OTPVerificationResponse
import com.compose.base.domain.entity.AppConfigData
import com.compose.base.domain.entity.UserData
import kotlinx.coroutines.flow.Flow

/**
 * Interface for preferences-related operations.
 */
interface PreferencesRepository {

    /**
     * Flow of booleans indicating the user's login status.
     * Emits updates whenever the login state changes in the preferences.
     */
    val loginStatusFlow: Flow<Boolean>

    /**
     * Flow of nullable `UserData` objects representing the user's information retrieved from preferences.
     * Emits updates whenever the user data changes in the preferences or becomes available.
     */
    val userDataFlow: Flow<UserData?>

    /**
     * Flow of `AppConfigData` objects representing the application configuration data retrieved from preferences.
     * Emits updates whenever the configuration data changes in the preferences.
     */
    val appConfigFlow: Flow<AppConfigData>

    /**
     * Initiates user login and stores user data (e.g., token) in preferences upon successful login.
     */
    suspend fun login()

    /**
     * Stores user data retrieved from the OTP verification response in the application's preferences.
     *
     * @param data: The `OTPVerificationResponse` object containing user data after successful verification.
     */
    suspend fun setUserData(data: OTPVerificationResponse)

    /**
     * Stores application configuration data in the application's preferences.
     * This method is suspended and might throw exceptions that need to be handled by the caller.
     *
     * @param appConfigData: The `AppConfigData` object containing the configuration details.
     */
    suspend fun setAppConfig(appConfigData: AppConfigData)

    /**
     * Removes user data and potentially other relevant information from the application's preferences
     * and logs the user out from the server.
     */
    suspend fun logout()
}