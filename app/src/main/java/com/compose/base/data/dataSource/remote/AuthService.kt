package com.compose.base.data.dataSource.remote

import com.compose.base.data.model.remote.request.GetOtpRequest
import com.compose.base.data.model.remote.request.OTPVerificationRequest
import com.compose.base.data.model.remote.response.OTPVerificationResponse
import com.compose.base.data.model.remote.response.PhoneVerificationResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * This interface defines the methods for making API requests related to authentication.
 */
interface AuthService {

    /**
     * Initiates a phone number verification process by sending an OTP.
     * also used to resend OTP.
     *
     * @param getOtpRequest: An object containing details required for OTP generation (likely phone number).
     * @return: A [PhoneVerificationResponse] object containing the OTP (if successful) or an error response.
     */
    @POST("/v3/get_otp")
    suspend fun getOtp(@Body getOtpRequest: GetOtpRequest): PhoneVerificationResponse

    /**
     * Verifies the user-provided OTP against the one sent during the initial request
     * and returns a response with user details.
     *
     * @param otpVerificationRequest: An object containing the user-provided OTP and potentially other verification details.
     * @return: An [OTPVerificationResponse] object indicating success or an error response.
     */
    @POST("/v3/verify_otp")
    suspend fun verifyOTP(@Body otpVerificationRequest: OTPVerificationRequest): OTPVerificationResponse
}

