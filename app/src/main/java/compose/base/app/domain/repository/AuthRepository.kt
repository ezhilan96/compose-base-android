package compose.base.app.domain.repository

import compose.base.app.config.util.NetworkResponse
import compose.base.app.data.model.request.LoginRequest
import compose.base.app.data.model.response.LoginResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun isLoggedIn(): Flow<Boolean>

    fun login(loginRequest: LoginRequest): Flow<NetworkResponse<LoginResponse>>

    suspend fun saveLoginInfo(token: String)
}