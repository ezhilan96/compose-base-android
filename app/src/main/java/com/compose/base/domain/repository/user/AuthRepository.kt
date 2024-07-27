package com.compose.base.domain.repository.user

import com.compose.base.data.model.remote.request.GetOtpRequest
import com.compose.base.data.model.remote.request.OTPVerificationRequest
import com.compose.base.data.model.remote.response.OTPVerificationResponse
import com.compose.base.data.model.remote.response.PhoneVerificationResponse
import com.compose.base.data.util.DataResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

/**
 * Interface for authentication-related operations.
 */
interface AuthRepository {

    /**
     * Initiates the account verification process by sending an OTP.
     *
     * @param getOtpRequest: An object containing details like phone number for OTP request.
     * @return: A Flow of `DataResponse<PhoneVerificationResponse>>`.
     *      - Success: Emits a `PhoneVerificationResponse` object containing details
     *                 related to the verification request (e.g., verification ID).
     *      - Error: Emits a `DataResponse.Error` indicating the OTP sending failure reason.
     */
    fun sendOtp(getOtpRequest: GetOtpRequest): Flow<DataResponse<PhoneVerificationResponse>>

    /**
     * Verifies the OTP provided by the user against the previously sent OTP.
     *
     * @param otpVerificationRequest: An object containing the OTP and verification ID.
     * @return: A Flow of `DataResponse<OTPVerificationResponse>>`.
     *      - Success: Emits a `OTPVerificationResponse` object containing user data
     *                 upon successful verification.
     *      - Error: Emits a `DataResponse.Error` indicating the OTP verification failure reason.
     */
    fun verifyOTP(otpVerificationRequest: OTPVerificationRequest): Flow<DataResponse<OTPVerificationResponse>>

    /**
     * Persists the user data retrieved from the verification response.
     *
     * @param otpVerificationResponse: The response object containing user data after successful verification.
     * @throws: This method is suspended and might throw exceptions that need to be handled by the caller.
     */
    suspend fun storeUserData(otpVerificationResponse: OTPVerificationResponse)

    /**
     * Handles the user login process.
     */
    suspend fun login()

    /**
     * Initiates user logout and potentially clears any stored user data.
     *
     * @return: A Flow of `DataResponse<Response<Unit>>>`.
     *      - Success: Emits a successful response if the logout was successful.
     *      - Error: Emits a `DataResponse.Error` indicating the logout failure reason.
     */
    fun logout(): Flow<DataResponse<Response<Unit>>>

}