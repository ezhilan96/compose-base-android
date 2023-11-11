package compose.base.app.data.repositoryImpl

import compose.base.app.config.di.IoDispatcher
import compose.base.app.config.util.NetworkResponse
import compose.base.app.config.util.apiResponseHandler
import compose.base.app.data.dataSource.local.preference.UserPreferencesDataStore
import compose.base.app.data.dataSource.remote.AuthService
import compose.base.app.data.model.request.LoginRequest
import compose.base.app.data.model.response.LoginResponse
import compose.base.app.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val dataStore: UserPreferencesDataStore
) : AuthRepository {

    override fun isLoggedIn(): Flow<Boolean> = dataStore.isLoggedIn

    override fun login(loginRequest: LoginRequest): Flow<NetworkResponse<LoginResponse>> = flow {
        emit(apiResponseHandler {
            api.login(loginRequest)
        })
    }.flowOn(ioDispatcher)

    override suspend fun saveLoginInfo(token: String) = dataStore.putUserDetails(token)
}