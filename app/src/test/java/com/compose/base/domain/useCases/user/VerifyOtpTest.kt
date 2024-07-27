package com.compose.base.domain.useCases.user

import com.compose.base.R
import com.compose.base.data.model.remote.response.OTPVerificationResponse
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.repository.user.AuthRepository
import com.compose.base.presentation.util.UiText
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class VerifyOtpTest {

    private lateinit var verifyOtp: VerifyOtpUseCase
    private val mockAuthRepository = mockk<AuthRepository>(relaxed = true)

    @Before
    fun setUp() {
        verifyOtp = VerifyOtpUseCase(Dispatchers.IO, mockAuthRepository)
    }

    @Test
    fun `verifyOTP - invalid otp - failure result`() {
        runTest {
            val result = verifyOtp(otp = "fhsh", phoneNumber = "12345678890").first()
            assertThat(((result as DataResponse.Error.Local).message as UiText.Resource).id).isEqualTo(
                R.string.error_otp_invalid
            )
        }
    }

    @Test
    fun `verifyOTP - empty otp - failure result`() {
        runTest {
            val result = verifyOtp(otp = "", phoneNumber = "1234567890").first()
            assertThat(((result as DataResponse.Error.Local).message as UiText.Resource).id).isEqualTo(
                R.string.error_otp_empty
            )
        }
    }

    @Test
    fun `verifyOTP - valid otp - remote success result`() {
        val phoneNumber = 9498048698
        val successDataResponse = DataResponse.Success(
            OTPVerificationResponse(
                id = 0,
                token = "",
                userName = "",
                phone = 9498048698,
            )
        )
        coEvery { mockAuthRepository.verifyOTP(any()) } returns flowOf(successDataResponse)
        runTest {
            val result = verifyOtp(otp = "1323", phoneNumber = phoneNumber.toString()).first()
            assertThat(result).isInstanceOf(DataResponse.Success::class.java)
            assertThat((result as DataResponse.Success).data).isNotNull()
        }
    }

    @Test
    fun `verifyOTP - valid otp - local error result`() {
        val phoneNumber = 9498048698
        val errorDataResponse = DataResponse.Error.Remote(message = UiText.Value("invalid"))
        coEvery { mockAuthRepository.verifyOTP(any()) } returns flowOf(errorDataResponse)
        runTest {
            val result = verifyOtp(otp = "1323", phoneNumber = phoneNumber.toString()).first()
            assertThat(result).isInstanceOf(DataResponse.Error.Local::class.java)
        }
    }
}