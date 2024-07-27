package com.compose.base.presentation.screens.auth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.CountDownTimer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.compose.base.core.Constants
import com.compose.base.presentation.routes.AuthRoute
import com.compose.base.presentation.routes.MainRoute
import com.compose.base.presentation.screens.auth.login.LoginDestination
import com.compose.base.presentation.screens.auth.login.viewModel.LoginViewModel
import com.compose.base.presentation.screens.auth.verifyOtp.VerifyOtpDestination
import com.compose.base.presentation.screens.auth.verifyOtp.viewModel.VerifyOtpScreenUiEvent
import com.compose.base.presentation.screens.auth.verifyOtp.viewModel.VerifyOtpViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

/**
 * The navigation host composable for the authentication flow of the application.
 *
 * This composable manages the navigation within the login and verification screens
 * used for user authentication. It utilizes Jetpack Navigation components for routing
 * and screen transitions.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param viewModel A reference to the AuthViewModel instance for managing authentication flow.
 */
@Composable
fun AuthNavHost(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
) {
    MainRoute.AuthRoute
    val context = LocalContext.current
    val navController = rememberNavController()
    val navigateUp: () -> Unit = { navController.navigateUp() }
    val otpState by viewModel.uiState.collectAsStateWithLifecycle()

    // Creates a BroadcastReceiver to handle auto-reading OTPs from SMS.
    val autoReadOtpBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent ?: return
            val status = (intent.extras?.get(SmsRetriever.EXTRA_STATUS) as? Status) ?: return
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms = intent.extras?.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val otp = sms?.let { Constants.OTP_REGEX.toRegex().find(it) }?.value ?: ""
                    viewModel.onAutoOtpReceived(otp = otp)
                }

                else -> {
                    // Disable auto-reading OTPs if an error occurs.
                    viewModel.onDisableAutoRead()
                }
            }
        }
    }
    val autoReadOtpIntentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    val resendOtpCountDownTimer =
        object : CountDownTimer(
            Constants.SIXTY_SEC_IN_MILLIS + Constants.ONE_SEC_IN_MILLIS,
            Constants.ONE_SEC_IN_MILLIS,
        ) {
            override fun onTick(millisUntilFinished: Long) {
                viewModel.updateResendCountDown(millisUntilFinished.toInt() / 1000)
            }

            override fun onFinish() {}
        }

    NavHost(
        navController = navController,
        startDestination = AuthRoute.LoginDestination,
    ) {
        // Login screen for entering phone number.
        composable<AuthRoute.LoginDestination> {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            loginViewModel.getResendOtpCountDown = { otpState.resendOtpCountDown }
            LoginDestination(
                modifier = modifier,
                onOtpSent = { phone ->
                    viewModel.onOtpSent()
                    navController.navigate(AuthRoute.VerifyOtpDestination(phone = phone))
                },
                viewModel = loginViewModel,
            )
        }

        // Verify OTP screen for entering the received code and resending it.
        AuthRoute.VerifyOtpDestination.let { destination ->
            composable<AuthRoute.VerifyOtpDestination> { navBackStackEntry ->
                val phoneNumber = navBackStackEntry.toRoute<AuthRoute.VerifyOtpDestination>().phone
                val verifyOtpViewModel = hiltViewModel<VerifyOtpViewModel>()
                verifyOtpViewModel.getResendOtpCountDown = { otpState.resendOtpCountDown }
                VerifyOtpDestination(
                    modifier = modifier,
                    navigateUp = navigateUp,
                    enableAutoReadOtp = otpState.enableAutoRead,
                    disableAutoReadOtp = { viewModel.onDisableAutoRead() },
                    resendOtpCountDown = otpState.resendOtpCountDown,
                    viewModel = verifyOtpViewModel,
                )
                LaunchedEffect(Unit) {
                    verifyOtpViewModel.setPhoneNumber(phoneNumber)
                }
                LaunchedEffect(otpState.autoReadOtp) {
                    if (otpState.autoReadOtp.isNotEmpty()) {
                        verifyOtpViewModel.onUiEvent(
                            VerifyOtpScreenUiEvent.OnOTPChanged(otpState.autoReadOtp)
                        )
                        verifyOtpViewModel.onUiEvent(VerifyOtpScreenUiEvent.OnSubmitOtp)
                    }
                }
            }
        }
    }

    // When enabled, registers the BroadcastReceiver for auto-reading OTPs and starts the countdown timer.
    // When disabled, unregisters the receiver.
    LaunchedEffect(otpState.enableAutoRead) {
        if (otpState.enableAutoRead) {
            val client = SmsRetriever.getClient(context)
            client.startSmsRetriever().addOnCanceledListener {
                viewModel.onDisableAutoRead()
                context.unregisterReceiver(autoReadOtpBroadcastReceiver)
            }.addOnFailureListener {
                viewModel.onDisableAutoRead()
                context.unregisterReceiver(autoReadOtpBroadcastReceiver)
            }.addOnSuccessListener {
                ContextCompat.registerReceiver(
                    context,
                    autoReadOtpBroadcastReceiver,
                    autoReadOtpIntentFilter,
                    ContextCompat.RECEIVER_EXPORTED,
                )
            }
            resendOtpCountDownTimer.cancel()
            resendOtpCountDownTimer.start()
        } else {
            try {
                context.unregisterReceiver(autoReadOtpBroadcastReceiver)
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            resendOtpCountDownTimer.cancel()
            try {
                context.unregisterReceiver(autoReadOtpBroadcastReceiver)
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
            }
        }
    }
}