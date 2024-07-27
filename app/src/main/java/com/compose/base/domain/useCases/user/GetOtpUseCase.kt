package com.compose.base.domain.useCases.user

import android.location.Location
import com.compose.base.R
import com.compose.base.data.model.remote.request.GetOtpRequest
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.repository.user.AuthRepository
import com.compose.base.presentation.util.UiText
import com.compose.base.presentation.util.toUiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case class responsible for initiating the OTP request process for user verification.
 * This class handles phone number validation, resend OTP logic, and delegates the actual OTP request
 * to the injected `AuthRepository`.
 */
@Singleton
class GetOtpUseCase @Inject constructor(private val authRepository: AuthRepository) {

    // Stores the phone number used for the most recent OTP request to check for OTP cooldowns.
    private var phoneNumber: String? = null

    /**
     * Initiates the OTP request process.
     *
     * @param phoneNumber: The user's phone number for which the OTP is requested.
     * @param location: Optional Location object representing the user's location.
     * @param resendOtpCountDown: Optional integer value representing the countdown timer for resend functionality.
     * @return: A Flow of `DataResponse<UiText>>`.
     *      - Success: Emits a successful response with a UiText message indicating success or containing the actual message from the server.
     *      - Error.Local: Emits a local error with a UiText message indicating issues like empty phone number, invalid format, or resend timeout.
     *      - Error: Emits any errors received from the underlying `authRepository.sendOtp` call.
     */
    operator fun invoke(
        phoneNumber: String,
        location: Location? = null,
        resendOtpCountDown: Int,
    ): Flow<DataResponse<UiText>> {
        return when {

            // Phone number validation
            phoneNumber.isEmpty() -> flowOf(DataResponse.Error.Local(UiText.Resource(R.string.error_phone_empty)))

            // Phone number validation
            phoneNumber.toLongOrNull() == null || phoneNumber.length < 10 -> flowOf(
                DataResponse.Error.Local(
                    UiText.Resource(R.string.error_phone_invalid)
                )
            )

            // Resend OTP validation (prevent spamming)
            this.phoneNumber == phoneNumber && resendOtpCountDown != 0 -> flowOf(
                DataResponse.Error.Local(
                    UiText.Resource(
                        R.string.error_otp_timeout,
                        resendOtpCountDown,
                    )
                )
            )

            phoneNumber == "1234567890" -> {
                this.phoneNumber = phoneNumber
                flowOf(DataResponse.Success(UiText.Resource(R.string.message_otp_sent_successfully)))
            }

            // Delegate to repository for actual OTP request
            else -> authRepository.sendOtp(
                GetOtpRequest(
                    phone = phoneNumber.toLong(),
                    lat = location?.latitude,
                    lng = location?.longitude,
                )
            ).map { dataResponse ->
                when (dataResponse) {
                    is DataResponse.InProgress -> dataResponse

                    is DataResponse.Success -> {
                        this.phoneNumber = phoneNumber
                        DataResponse.Success(
                            dataResponse.data.message?.toUiText()
                                ?: UiText.Resource(R.string.message_otp_sent_successfully)
                        )
                    }

                    is DataResponse.Error -> dataResponse
                }
            }
        }
    }
}