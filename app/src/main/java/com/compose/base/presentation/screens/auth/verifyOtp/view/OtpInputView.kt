package com.compose.base.presentation.screens.auth.verifyOtp.view

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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.compose.base.R
import com.compose.base.core.Constants.KEY_ACTION
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle
import com.compose.base.presentation.screens.auth.verifyOtp.component.OtpTextField


/**
 * The OtpInputView composable represents a section for OTP input and verification.
 *
 * This composable displays informative text, handle user interactions for phone number edit,
 * OTP input, resend request, and submission.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param phoneNumber The phone number where the OTP is sent.
 * @param otp The current OTP value entered by the user.
 * @param countdownTimer The remaining time in seconds for OTP resend.
 * @param onEditPhonePressed A callback function to be called when the user wants to edit the phone number.
 * @param onResendPressed A callback function to be called when the user requests OTP resend.
 * @param onOtpChanged A callback function to be triggered when the OTP value changes.
 * @param onSubmit A callback function to be called when the user submits the OTP.
 * @param isLoading A boolean indicating if the OTP verification is in progress.
 * @param error An optional error message to be displayed if OTP verification fails.
 * @param focusedByDefault A boolean indicating if soft-keyboard focus should be set on input by default.
 */
@Composable
fun OtpInputView(
    modifier: Modifier = Modifier,
    phoneNumber: String,
    otp: String,
    countdownTimer: Int,
    onEditPhonePressed: () -> Unit,
    onResendPressed: () -> Unit,
    onOtpChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    error: String? = null,
    focusedByDefault: Boolean = false,
) {
}

@Preview
@Composable
fun OtpInputViewPreview() {
    ComposeBaseTheme {
        OtpInputView(
            modifier = Modifier.fillMaxSize(),
            phoneNumber = "937825234354",
            otp = "1323",
            onOtpChanged = {},
            countdownTimer = 12,
            onSubmit = {},
            onEditPhonePressed = {},
            onResendPressed = {},
            isLoading = false,
        )
    }
}