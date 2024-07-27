package com.compose.base.domain.useCases.user

import com.google.common.truth.Truth.assertThat
import com.compose.base.R
import com.compose.base.data.model.remote.response.Data
import com.compose.base.data.model.remote.response.PhoneVerificationResponse
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.repository.user.AuthRepository
import com.compose.base.presentation.util.UiText
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TriggerOtpTest {

    private lateinit var getOtpUseCase: GetOtpUseCase
    private val mockAuthRepository = mockk<AuthRepository>(relaxed = true)

    @Before
    fun setUp() {
        getOtpUseCase = GetOtpUseCase(mockAuthRepository)
    }

    @Test
    fun `invalid phone - trigger OTP - invalid error result`() {
        runTest {
            val result = getOtpUseCase(phoneNumber = "asfgfg", resendOtpCountDown = 0).first()
            assertThat(((result as DataResponse.Error.Local).message as UiText.Resource).id).isEqualTo(
                R.string.error_phone_invalid
            )
        }
    }

    @Test
    fun `empty phone - trigger OTP - empty error result`() {
        runTest {
            val result = getOtpUseCase(phoneNumber = "", resendOtpCountDown = 0).first()
            assertThat(((result as DataResponse.Error.Local).message as UiText.Resource).id).isEqualTo(
                R.string.error_phone_empty
            )
        }
    }

    @Test
    fun `valid phone - trigger OTP - remote success result`() {
        val phoneNumber = 9498048698
        val successDataResponse = DataResponse.Success(
            PhoneVerificationResponse(
                data = Data(
                    phone = phoneNumber,
                    countryCode = "+91"
                ), message = "OTP sent successfully!"
            )
        )
        coEvery { mockAuthRepository.sendOtp(any()) } returns flowOf(successDataResponse)
        runTest {
            val result =
                getOtpUseCase(phoneNumber = phoneNumber.toString(), resendOtpCountDown = 0).first()
            assertThat(result).isInstanceOf(DataResponse.Success::class.java)
            assertThat((result as DataResponse.Success).data).isNotNull()
        }
    }

    @Test
    fun `valid phone - trigger OTP - local error result`() {
        val phoneNumber = 9498048698
        val errorDataResponse = DataResponse.Error.Remote()
        coEvery { mockAuthRepository.sendOtp(any()) } returns flowOf(errorDataResponse)
        runTest {
            val result =
                getOtpUseCase(phoneNumber = phoneNumber.toString(), resendOtpCountDown = 0).first()
            assertThat(result).isInstanceOf(DataResponse.Error.Remote::class.java)
            assertThat((result as DataResponse.Error.Remote).message).isNotNull()
        }
    }
}