package com.compose.base.domain.useCases.user

import com.compose.base.R
import com.compose.base.data.model.remote.response.Data
import com.compose.base.data.model.remote.response.PhoneVerificationResponse
import com.compose.base.data.util.DataState
import com.compose.base.domain.repository.AuthRepository
import com.compose.base.presentation.components.OtpCountDownTimer
import com.compose.base.presentation.util.UiText
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetOtpTest {

    private lateinit var triggerOtp: GetOtpUseCase
    private val mockAuthRepository = mockk<AuthRepository>(relaxed = true)
    private val mockOtpCountDownTimer = mockk<OtpCountDownTimer>(relaxed = true)

    @Before
    fun setUp() {
        triggerOtp = GetOtpUseCase(mockAuthRepository, mockOtpCountDownTimer)
    }

    @Test
    fun `invalid phone - trigger OTP - invalid error result`() {
        runTest {
            val result = triggerOtp(phoneNumber = "asfgfg").first()
            assertThat(((result as DataState.Error.Local).message as UiText.Resource).id).isEqualTo(
                R.string.error_phone_invalid
            )
        }
    }

    @Test
    fun `empty phone - trigger OTP - empty error result`() {
        runTest {
            val result = triggerOtp(phoneNumber = "").first()
            assertThat(((result as DataState.Error.Local).message as UiText.Resource).id).isEqualTo(
                R.string.error_phone_empty
            )
        }
    }

    @Test
    fun `valid phone - trigger OTP - remote success result`() {
        val phoneNumber = 9498048698
        val successDataState = DataState.Success(
            PhoneVerificationResponse(
                data = Data(
                    phone = phoneNumber,
                    countryCode = "+91"
                ), message = "OTP sent successfully!"
            )
        )
        coEvery { mockAuthRepository.sendOtp(any()) } returns flowOf(successDataState)
        runTest {
            val result = triggerOtp(phoneNumber = phoneNumber.toString()).first()
            assertThat(result).isInstanceOf(DataState.Success::class.java)
            assertThat((result as DataState.Success).data).isNotNull()
        }
    }

    @Test
    fun `valid phone - trigger OTP - local error result`() {
        val phoneNumber = 9498048698
        val errorDataState = DataState.Error.Remote()
        coEvery { mockAuthRepository.sendOtp(any()) } returns flowOf(errorDataState)
        runTest {
            val result = triggerOtp(phoneNumber = phoneNumber.toString()).first()
            assertThat(result).isInstanceOf(DataState.Error.Remote::class.java)
            assertThat((result as DataState.Error.Remote).message).isNotNull()
        }
    }
}