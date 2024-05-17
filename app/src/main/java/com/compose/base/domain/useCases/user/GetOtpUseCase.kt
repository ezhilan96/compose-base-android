package com.compose.base.domain.useCases.user

import android.location.Location
import com.compose.base.R
import com.compose.base.data.model.remote.request.GetOtpRequest
import com.compose.base.data.util.DataState
import com.compose.base.domain.repository.AuthRepository
import com.compose.base.presentation.components.OtpCountDownTimer
import com.compose.base.presentation.util.UiText
import com.compose.base.presentation.util.toUiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetOtpUseCase @Inject constructor(
    private val repo: AuthRepository,
    private val otpCountDownTimer: OtpCountDownTimer,
) {

    private var phoneNumber: String? = null
    val remainingTimeToResendOtp: StateFlow<Int>
        get() = repo.otpCountDownInSec


    operator fun invoke(
        phoneNumber: String,
        location: Location? = null,
    ): Flow<DataState<UiText>> {
        return when {

            phoneNumber.isEmpty() -> flowOf(DataState.Error.Local(UiText.Resource(R.string.error_phone_empty)))

            phoneNumber.toLongOrNull() == null || phoneNumber.length < 10 -> flowOf(
                DataState.Error.Local(
                    UiText.Resource(R.string.error_phone_invalid)
                )
            )

            this.phoneNumber == phoneNumber && remainingTimeToResendOtp.value != 0 -> flowOf(
                DataState.Error.Local(
                    UiText.Resource(
                        R.string.error_otp_timeout,
                        remainingTimeToResendOtp.value,
                    )
                )
            )

            else -> repo.sendOtp(
                GetOtpRequest(
                    phone = phoneNumber.toLong(),
                    lat = location?.latitude,
                    lng = location?.longitude,
                )
            ).map { dataState ->
                when (dataState) {
                    is DataState.InProgress -> dataState

                    is DataState.Success -> {
                        this.phoneNumber = phoneNumber
                        otpCountDownTimer.cancel()
                        otpCountDownTimer.start()
                        repo.updateAutoReadOtp(null)
                        DataState.Success(
                            dataState.data.message?.toUiText()
                                ?: UiText.Resource(R.string.message_otp_sent_successfully)
                        )
                    }

                    is DataState.Error -> dataState
                }
            }
        }
    }
}