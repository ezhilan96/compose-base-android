package compose.base.app.data.dataSource.remote

import compose.base.app.data.model.request.LoginRequest
import compose.base.app.data.model.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}