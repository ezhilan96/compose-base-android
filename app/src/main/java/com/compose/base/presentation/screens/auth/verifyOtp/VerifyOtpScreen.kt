@file:OptIn(ExperimentalMaterial3Api::class)

package com.compose.base.presentation.screens.auth.verifyOtp

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.compose.base.R
import com.compose.base.core.Constants.KEY_ACTION
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle
import com.compose.base.presentation.screens.auth.verifyOtp.component.OtpTextField
import com.compose.base.presentation.screens.auth.verifyOtp.viewModel.OtpAnnotations
import com.compose.base.presentation.screens.auth.verifyOtp.viewModel.OtpNavigationItem
import com.compose.base.presentation.screens.auth.verifyOtp.viewModel.VerifyOtpScreenState
import com.compose.base.presentation.screens.auth.verifyOtp.viewModel.VerifyOtpScreenUiEvent
import com.compose.base.presentation.screens.auth.verifyOtp.viewModel.VerifyOtpViewModel
import com.compose.base.presentation.screens.core.ScreenNavItem
import com.compose.base.presentation.screens.core.ScreenUiState
import com.compose.base.presentation.screens.shared.component.OnStop
import com.compose.base.presentation.screens.shared.dialog.DefaultAlert
import com.compose.base.presentation.util.enableGesture
import com.compose.base.presentation.util.isNullOrEmpty

/**
 * The VerifyOtpDestination composable represents the verification screen within the login flow
 * after a phone number is submitted.
 *
 * This composable handles data fetching, state management, and navigation based on user interactions
 * during OTP verification. It utilizes the VerifyOtpViewModel to handle verification logic and
 * communicates UI events to the view model.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param navigateUp A callback function to navigate back to the previous screen.
 * @param enableAutoReadOtp A boolean indicating if auto-read OTP is enabled.
 * @param disableAutoReadOtp A callback function to disable auto-read OTP.
 * @param resendOtpCountDown The remaining time for OTP resend countdown.
 * @param viewModel A reference to the VerifyOtpViewModel instance for verification-specific logic.
 */
@Composable
fun VerifyOtpDestination(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    enableAutoReadOtp: Boolean,
    disableAutoReadOtp: () -> Unit,
    resendOtpCountDown: Int,
    viewModel: VerifyOtpViewModel,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    VerifyOtpScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel::onUiEvent,
        enableAutoReadOtp = enableAutoReadOtp,
        disableAutoReadOtp = disableAutoReadOtp,
        resendOtpCountDown = resendOtpCountDown,
        navigateUp = navigateUp,
    )

    LaunchedEffect(uiState.screenStack) {
        uiState.screenStack.forEach { navigationItem ->
            when (navigationItem) {
                OtpNavigationItem.OTP_TOAST -> {
                    Toast.makeText(
                        context,
                        uiState.screenState.successMessage?.asString(context),
                        Toast.LENGTH_SHORT,
                    ).show()
                    // Remove the Navigation item from the stack after displaying the success message.
                    viewModel.onUiEvent(VerifyOtpScreenUiEvent.OnDismiss)
                }

                else -> return@forEach
            }
        }
    }

    if (uiState.screenStack.contains(ScreenNavItem.ALERT_DIALOG)) {
        DefaultAlert(
            modifier = modifier,
            message = uiState.alertMessage.asString(),
            onDismiss = { viewModel.onUiEvent(VerifyOtpScreenUiEvent.OnDismiss) },
        )
    }

    BackHandler(
        enabled = uiState.screenStack.isNotEmpty() || enableAutoReadOtp,
        onBack = {
            // The state of the Auto-read bottom sheet is hoisted to AuthNavHost.
            // so in order to dismiss the bottom sheet, we need to invoke the callback passed
            // from the AuthNavHost.
            disableAutoReadOtp()
            viewModel.onUiEvent(VerifyOtpScreenUiEvent.OnDismiss)
        },
    )

    OnStop(viewModel::onStop)
}

/**
 * The VerifyOtpScreen composable represents the UI layout for the OTP verification screen that can be previewed.
 *
 * This composable utilizes Jetpack Compose elements to structure the screen, handle user input,
 * and trigger events for the VerifyOtpViewModel based on user actions.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param uiState The current UI state containing data for the OTP verification screen.
 * @param uiEvent A callback function to send UI events (like button clicks) to the VerifyOtpViewModel.
 * @param enableAutoReadOtp A boolean indicating if auto-read OTP is enabled.
 * @param disableAutoReadOtp A callback function to disable auto-read OTP.
 * @param resendOtpCountDown The remaining time for OTP resend countdown.
 * @param navigateUp A callback function to navigate back to the previous screen.
 */
@Composable
fun VerifyOtpScreen(
    modifier: Modifier = Modifier,
    uiState: ScreenUiState<VerifyOtpScreenState>,
    uiEvent: (VerifyOtpScreenUiEvent) -> Unit = {},
    enableAutoReadOtp: Boolean,
    disableAutoReadOtp: () -> Unit,
    resendOtpCountDown: Int,
    navigateUp: () -> Unit,
) {
    val contentColor = LocalContentColor.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val phoneNumberSpanText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = contentColor)) {
            append("We have sent an OTP to ")
        }
        pushStringAnnotation(tag = KEY_ACTION, annotation = OtpAnnotations.Phone.toString())
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
        ) {
            append("+91${uiState.screenState.phoneNumber}")
        }
        pop()
    }
    val resendSpanText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = contentColor)) {
            append("Didn’t receive OTP? ")
        }
        pushStringAnnotation(tag = KEY_ACTION, annotation = OtpAnnotations.Resend.toString())
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("Resend")
        }
        pop()
    }
    val countDownSpanText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = contentColor)) {
            append("You can resend OTP in ")
        }
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.SemiBold)
        ) {
            append("${resendOtpCountDown}s")
        }
    }

    Scaffold(modifier = modifier.enableGesture(!uiState.isLoading)) { safeAreaPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(safeAreaPadding)
        ) {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                IconButton(
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.grid05,
                        vertical = MaterialTheme.spacing.grid1
                    ),
                    onClick = navigateUp,
                ) {
                    Icon(
                        modifier = modifier.padding(MaterialTheme.spacing.grid1),
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.customColors.lightVariant,
                    )
                }

                Column(
                    modifier
                        .weight(1f)
                        .padding(horizontal = MaterialTheme.spacing.unit20),
                ) {
                    Text(
                        text = stringResource(R.string.label_otp),
                        style = MaterialTheme.textStyle.loginHeadline,
                    )
                    Spacer(modifier = modifier.height(MaterialTheme.spacing.grid1))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ClickableText(
                            text = phoneNumberSpanText,
                            style = MaterialTheme.textStyle.loginLabel,
                            onClick = { offset ->
                                phoneNumberSpanText.getStringAnnotations(
                                    KEY_ACTION, offset, offset
                                ).firstOrNull()?.let { annotation ->
                                    if (annotation.item == OtpAnnotations.Phone.toString()) {
                                        navigateUp()
                                    }
                                }
                            },
                        )
                        Icon(
                            modifier = modifier
                                .padding(start = MaterialTheme.spacing.unit2)
                                .height(MaterialTheme.spacing.phoneEditButtonHeight)
                                .clickable { navigateUp() },
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(modifier = modifier.height(MaterialTheme.spacing.unit20))
                    OtpTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = uiState.screenState.otp,
                        onValueChange = { uiEvent(VerifyOtpScreenUiEvent.OnOTPChanged(it)) },
                        onSubmit = {
                            keyboardController?.hide()
                            uiEvent(VerifyOtpScreenUiEvent.OnSubmitOtp)
                        },
                        focusedByDefault = !enableAutoReadOtp
                    )
                    Spacer(modifier = modifier.height(MaterialTheme.spacing.grid1))
                    if (!uiState.screenState.inputErrorMessage.isNullOrEmpty()) {
                        Text(
                            modifier = modifier.align(Alignment.CenterHorizontally),
                            text = uiState.screenState.inputErrorMessage?.asString()!!,
                            style = MaterialTheme.textStyle.loginLabel,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    if (!uiState.isLoading) {
                        Spacer(modifier = modifier.height(MaterialTheme.spacing.unit20))
                        if (resendOtpCountDown == 0) {
                            ClickableText(
                                text = resendSpanText,
                                style = MaterialTheme.textStyle.otpDisplay,
                                onClick = { offset ->
                                    resendSpanText.getStringAnnotations(
                                        KEY_ACTION, offset, offset
                                    ).firstOrNull()?.let { annotation ->
                                        if (annotation.item == OtpAnnotations.Resend.toString()) {
                                            uiEvent(VerifyOtpScreenUiEvent.ResendOtp)
                                        }
                                    }
                                },
                            )
                        } else {
                            Text(
                                text = countDownSpanText,
                                style = MaterialTheme.textStyle.otpDisplay,
                            )
                        }
                    }
                    Spacer(modifier = modifier.height(MaterialTheme.spacing.unit20))
                    FilledTonalButton(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.grid6),
                        onClick = {
                            keyboardController?.hide()
                            uiEvent(VerifyOtpScreenUiEvent.OnSubmitOtp)
                        },
                        shape = MaterialTheme.customShapes.defaultButton,
                        enabled = !uiState.isLoading,
                    ) {
                        if (!uiState.isLoading) {
                            Text(
                                text = stringResource(R.string.button_submit_otp),
                                style = MaterialTheme.textStyle.loginDisplay,
                            )
                        } else {
                            Row(
                                modifier = modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(MaterialTheme.spacing.grid2)
                                ) {
                                    CircularProgressIndicator(strokeWidth = MaterialTheme.spacing.unit2)
                                }
                                Spacer(modifier = modifier.width(MaterialTheme.spacing.grid1))
                                Text(
                                    text = stringResource(R.string.message_verifying),
                                    style = MaterialTheme.textStyle.loginDisplay,
                                )
                            }
                        }
                    }
                }
            }

            if (enableAutoReadOtp) {
                ModalBottomSheet(
                    onDismissRequest = disableAutoReadOtp,
                    dragHandle = null,
                    content = {
                        Column(
                            modifier = modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            LinearProgressIndicator(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .height(MaterialTheme.spacing.unit2)
                            )
                            Row(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(MaterialTheme.spacing.grid2),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.message_otp_auto_read),
                                    style = MaterialTheme.textStyle.otpDisplay,
                                )

                                TextButton(
                                    onClick = disableAutoReadOtp,
                                    shape = MaterialTheme.customShapes.defaultButton,
                                ) {
                                    Text(
                                        text = stringResource(R.string.button_disable_auto_read),
                                        style = MaterialTheme.textStyle.loginLabel,
                                    )
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview
@Composable
fun OtpScreenPreview() {
    ComposeBaseTheme {
        VerifyOtpScreen(
            modifier = Modifier,
            uiState = ScreenUiState(screenState = VerifyOtpScreenState(phoneNumber = "8137461748")),
            uiEvent = {},
            enableAutoReadOtp = false,
            disableAutoReadOtp = {},
            resendOtpCountDown = 0,
            navigateUp = {},
        )
    }
}