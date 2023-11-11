package compose.base.app.data.dataSource.local.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val userPreferencesDataStore: DataStore<Preferences>
) {
    private val _tokenKey = stringPreferencesKey("token")

    val isLoggedIn: Flow<Boolean> = userPreferencesDataStore.data.map {
        it[_tokenKey].isNullOrEmpty()
    }

    suspend fun login(token: String) {
        userPreferencesDataStore.edit {
            it[_tokenKey] = token
        }
    }

    suspend fun putUserDetails(token: String){
        userPreferencesDataStore.edit {
            it[_tokenKey] = token
        }
    }

    val userDetails: Flow<String?> = userPreferencesDataStore.data.map {
        it[_tokenKey]
    }

    fun logout() {
        runBlocking {
            userPreferencesDataStore.edit {
                it.clear()
            }
        }
    }
}