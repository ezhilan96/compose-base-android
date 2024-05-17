package com.compose.base.data.dataSource.local.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.compose.base.core.Constants.DEFAULT_LONG
import com.compose.base.core.Constants.DEFAULT_STRING
import com.compose.base.data.model.remote.response.OTPVerificationResponse
import com.compose.base.domain.entity.AppConfigData
import com.compose.base.domain.entity.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val userPreferencesDataStore: DataStore<Preferences>
) {
    private val loginStatusKey = booleanPreferencesKey("isLoggedIn")
    private val idKey = intPreferencesKey("id")
    private val tokenKey = stringPreferencesKey("token")
    private val phoneKey = longPreferencesKey("phone")
    private val driverNameKey = stringPreferencesKey("driverNameKey")
    private val googleApiKey = stringPreferencesKey("googleApiKey")
    private val versionCodeKey = intPreferencesKey("versionCode")
    private val isForceUpdateKey = booleanPreferencesKey("isForceUpdate")
    private val isPartialUpdateKey = booleanPreferencesKey("isPartialUpdate")

    val isLoggedIn: Flow<Boolean> = userPreferencesDataStore.data.map { preferences ->
        preferences[loginStatusKey] ?: false
    }

    suspend fun storeUserData(data: OTPVerificationResponse) {
        userPreferencesDataStore.edit {
            it[idKey] = data.id
            it[tokenKey] = data.token
            it[phoneKey] = data.phone ?: DEFAULT_LONG
            it[driverNameKey] = data.name ?: DEFAULT_STRING
        }
    }

    suspend fun login() {
        userPreferencesDataStore.edit {
            it[loginStatusKey] = true
        }
    }

    suspend fun putAppConfig(appConfigData: AppConfigData) {
        userPreferencesDataStore.edit { mutablePreferences ->
            mutablePreferences[googleApiKey] = appConfigData.googleApiKey
            appConfigData.versionCode?.let { mutablePreferences[versionCodeKey] = it }
            mutablePreferences[isForceUpdateKey] = appConfigData.isForceUpdate
            mutablePreferences[isPartialUpdateKey] = appConfigData.isPartialUpdate
        }
    }

    val appConfig: Flow<AppConfigData> = userPreferencesDataStore.data.map {
        AppConfigData(
            googleApiKey = it[googleApiKey] ?: "",
            isForceUpdate = it[isForceUpdateKey] ?: false,
            isPartialUpdate = it[isPartialUpdateKey] ?: false,
            versionCode = it[versionCodeKey],
        )
    }


    val userDetails: Flow<UserData?> = userPreferencesDataStore.data.map {
        try {
            UserData(
                id = it[idKey]!!,
                token = it[tokenKey]!!,
                phone = "+91 ${it[phoneKey]}",
                name = it[driverNameKey]!!,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun logout() {
        runBlocking {
            userPreferencesDataStore.edit {
                it.clear()
            }
        }
    }
}