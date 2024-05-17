package com.compose.base.domain.repository

import com.compose.base.data.model.remote.request.GetOtpRequest
import com.compose.base.data.model.remote.request.OTPVerificationRequest
import com.compose.base.data.model.remote.response.OTPVerificationResponse
import com.compose.base.data.model.remote.response.PhoneVerificationResponse
import com.compose.base.data.util.DataState
import com.compose.base.domain.entity.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

interface AuthRepository {

    val isLoggedIn: Flow<Boolean>

    val userDetails: Flow<UserData?>

    val autoReadOtp: Flow<String?>
    fun updateAutoReadOtp(otp: String?)

    val otpCountDownInSec: StateFlow<Int>
    fun updateOtpCountDownTime(sec: Int)

    fun sendOtp(getOtpRequest: GetOtpRequest): Flow<DataState<PhoneVerificationResponse>>

    fun verifyOTP(otpVerificationRequest: OTPVerificationRequest): Flow<DataState<OTPVerificationResponse>>

    suspend fun storeUserData(otpVerificationResponse: OTPVerificationResponse)

    suspend fun login()

    fun logout(): Flow<DataState<Response<Unit>>>

}