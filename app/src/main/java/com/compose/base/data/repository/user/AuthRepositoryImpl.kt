package com.compose.base.data.repository.user

import com.compose.base.data.dataSource.remote.AuthService
import com.compose.base.data.dataSource.remote.UserService
import com.compose.base.data.model.remote.request.GetOtpRequest
import com.compose.base.data.model.remote.request.OTPVerificationRequest
import com.compose.base.data.model.remote.response.OTPVerificationResponse
import com.compose.base.data.model.remote.response.PhoneVerificationResponse
import com.compose.base.data.util.DataResponse
import com.compose.base.data.util.NetworkHandler
import com.compose.base.domain.repository.core.PreferencesRepository
import com.compose.base.domain.repository.user.AuthRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val executeApiRequest: NetworkHandler,
    private val preferencesRepository: PreferencesRepository,
    private val authService: AuthService,
    private val userService: UserService,
) : AuthRepository {

    override fun sendOtp(getOtpRequest: GetOtpRequest): Flow<DataResponse<PhoneVerificationResponse>> =
        executeApiRequest {
            authService.getOtp(getOtpRequest)
        }

    override fun verifyOTP(otpVerificationRequest: OTPVerificationRequest): Flow<DataResponse<OTPVerificationResponse>> =
        executeApiRequest {
            authService.verifyOTP(otpVerificationRequest)
        }

    override suspend fun storeUserData(otpVerificationResponse: OTPVerificationResponse): Unit =
        preferencesRepository.setUserData(otpVerificationResponse)

    override suspend fun login(): Unit = preferencesRepository.login()

    override fun logout(): Flow<DataResponse<Response<Unit>>> = executeApiRequest {
        userService.logout()
    }
}