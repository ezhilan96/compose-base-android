package com.compose.base.data.repository.core

import androidx.datastore.core.DataStore
import com.compose.base.UserPreferences
import com.compose.base.data.model.remote.response.OTPVerificationResponse
import com.compose.base.domain.entity.AppConfigData
import com.compose.base.domain.entity.UserData
import com.compose.base.domain.repository.core.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(private val userPreferencesDataStore: DataStore<UserPreferences>) :
    PreferencesRepository {

    override val loginStatusFlow: Flow<Boolean> = userPreferencesDataStore.data.map { preferences ->
        preferences.loginStatus
    }

    override val userDataFlow: Flow<UserData?> = userPreferencesDataStore.data.map {
        try {
            UserData(
                id = it.userData.id,
                token = it.userData.token!!,
                phone = "+91 ${it.userData.phone}",
                name = it.userData.name ?: "",
            )
        } catch (e: Exception) {
            null
        }
    }

    override val appConfigFlow: Flow<AppConfigData> = userPreferencesDataStore.data.map {
        AppConfigData(
            googleApiKey = it.appConfig.googleApiKey ?: "",
            isForceUpdate = it.appConfig.isForceUpdate,
            isPartialUpdate = it.appConfig.isPartialUpdate,
            versionCode = it.appConfig.versionCode,
        )
    }

    override suspend fun login() {
        userPreferencesDataStore.updateData { currentUserPreferences ->
            currentUserPreferences.toBuilder().setLoginStatus(true).build()
        }
    }

    override suspend fun setUserData(data: OTPVerificationResponse) {
        userPreferencesDataStore.updateData { currentUserPreferences ->
            currentUserPreferences.toBuilder().setUserData(
                UserPreferences.UserData.newBuilder().setId(data.id).setToken(data.token)
                    .setPhone(data.phone ?: 0).setName(data.userName).build()
            ).build()
        }
    }

    override suspend fun setAppConfig(appConfigData: AppConfigData) {
        userPreferencesDataStore.updateData { currentUserPreferences ->
            currentUserPreferences.toBuilder().setAppConfig(
                currentUserPreferences.appConfig.toBuilder()
                    .setGoogleApiKey(appConfigData.googleApiKey)
                    .setIsForceUpdate(appConfigData.isForceUpdate)
                    .setIsPartialUpdate(appConfigData.isPartialUpdate)
                    .setVersionCode(appConfigData.versionCode ?: 0).build()
            ).build()
        }
    }

    override suspend fun logout() {
        userPreferencesDataStore.updateData { UserPreferences.getDefaultInstance() }
    }
}