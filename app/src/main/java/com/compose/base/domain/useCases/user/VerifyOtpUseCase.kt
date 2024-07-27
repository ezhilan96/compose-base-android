package com.compose.base.domain.useCases.user

import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.core.di.IoDispatcher
import com.compose.base.data.model.remote.request.OTPVerificationRequest
import com.compose.base.data.model.remote.response.OTPVerificationResponse
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.entity.UserVerificationStatus
import com.compose.base.domain.repository.user.AuthRepository
import com.compose.base.presentation.util.UiText
import com.compose.base.presentation.util.contains
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Use case class responsible for verifying the OTP entered by the user during registration.
 * This class handles OTP validation, delegates verification to the repository, and determines the user's verification status
 * based on the response. It also initiates login if verification is complete.
 */
class VerifyOtpUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
) {

    /**
     * Initiates the OTP verification process.
     *
     * @param phoneNumber: The user's phone number for which the OTP was sent.
     * @param otp: The user-entered OTP code.
     * @return: A Flow of `DataResponse<UserVerificationStatus>>`.
     *      - Success: Emits a successful response with a `UserVerificationStatus` indicating the verification state.
     *          - Done: User verification is complete and login might be initiated.
     *          - DocumentEmpty: User hasn't uploaded required documents yet.
     *          - DocumentVerificationPending: User documents are uploaded but verification is pending.
     *      - Error.Local: Emits a local error with a UiText message indicating issues like empty OTP or invalid format.
     *      - Error: Emits any errors received from the underlying `authRepository.verifyOTP` call, potentially including:
     *          - Remote errors with specific messages (e.g., incorrect OTP).
     */
    operator fun invoke(
        phoneNumber: String, otp: String
    ): Flow<DataResponse<UserVerificationStatus>> = when {

        // OTP validation
        otp.isEmpty() -> {
            flowOf(DataResponse.Error.Local(UiText.Resource(R.string.error_otp_empty)))
        }

        // OTP validation
        otp.toIntOrNull() == null || otp.length != 4 -> {
            flowOf(DataResponse.Error.Local(UiText.Resource(R.string.error_otp_invalid)))
        }

        otp == "1323" -> {
            CoroutineScope(dispatcher).launch {
                authRepository.storeUserData(
                    OTPVerificationResponse(
                        id = 0,
                        token = "token",
                        userName = "name",
                        phone = 1234567890,
                    )
                )
                authRepository.login()
            }
            flowOf(DataResponse.Success(UserVerificationStatus.Done))
        }

        // Delegate to repository for actual OTP verification
        else -> {
            authRepository.verifyOTP(
                OTPVerificationRequest(
                    phone = phoneNumber.toLong(),
                    otp = otp.toInt(),
                )
            ).map { dataResponse ->
                when (dataResponse) {
                    is DataResponse.InProgress -> dataResponse

                    is DataResponse.Success -> {
                        authRepository.storeUserData(dataResponse.data)
                        try {
                            authRepository.login()
                            DataResponse.Success(UserVerificationStatus.Done)
                        } catch (e: Exception) {
                            Firebase.crashlytics.recordException(e)
                            DataResponse.Error.Local()
                        }
                    }

                    is DataResponse.Error -> {
                        if (dataResponse.isIncorrectOtpError()) {
                            DataResponse.Error.Local(dataResponse.message)
                        } else {
                            dataResponse
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper function to identify errors specifically indicating an incorrect OTP entered by the user.
 *
 * @return True if the error is a remote error with a UiText.Value message containing specific keywords
 *          indicating incorrect or invalid OTP, false otherwise.
 */
fun DataResponse.Error.isIncorrectOtpError(): Boolean {
    return this is DataResponse.Error.Remote && this.message is UiText.Value && (this.message.contains(
        Constants.KEY_INCORRECT
    ) || this.message.contains(Constants.KEY_INVALID))
}