package compose.base.app.domain.usecase

import compose.base.app.config.util.NetworkResponse
import compose.base.app.data.model.request.LoginRequest
import compose.base.app.data.model.response.LoginResponse
import compose.base.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCase @Inject constructor(
    private val repo: AuthRepository
) {

    operator fun invoke(phoneNumber: String): Flow<NetworkResponse<LoginResponse>> =
        repo.login(LoginRequest(phoneNumber = phoneNumber)).onEach { response ->
            if (response is NetworkResponse.Success) {
                repo.saveLoginInfo(token = response.responseData.token)
            }
        }
}