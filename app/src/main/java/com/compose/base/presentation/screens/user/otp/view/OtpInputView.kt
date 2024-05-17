package com.compose.base.presentation.screens.user.otp.view

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
import com.compose.base.presentation.screens.user.otp.component.OtpTextField

enum class OtpAnnotations { Phone, Resend }

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
    val localModifier = Modifier
    val phoneNumberSpanText = buildAnnotatedString {
        append("We have sent an OTP to ")
        pushStringAnnotation(tag = KEY_ACTION, annotation = OtpAnnotations.Phone.toString())
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
        ) {
            append("+91${phoneNumber}")
        }
        pop()
    }
    val resendSpanText = buildAnnotatedString {
        append("Didn’t receive OTP? ")
        pushStringAnnotation(tag = KEY_ACTION, annotation = OtpAnnotations.Resend.toString())
        withStyle(
            style = SpanStyle(color = MaterialTheme.colorScheme.primary)
        ) {
            append("Resend")
        }
        pop()
    }
    val countDownSpanText = buildAnnotatedString {
        append("You can resend OTP in ")
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.SemiBold)
        ) {
            append("${countdownTimer}s")
        }
    }
    Column(
        modifier = modifier.padding(
            vertical = MaterialTheme.spacing.grid5,
            horizontal = MaterialTheme.spacing.unit20,
        )
    ) {
        Text(
            text = stringResource(R.string.label_otp),
            style = MaterialTheme.textStyle.loginHeadline,
        )
        Spacer(modifier = localModifier.height(MaterialTheme.spacing.grid1))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ClickableText(
                text = phoneNumberSpanText,
                style = MaterialTheme.textStyle.loginLabel,
                onClick = { offset ->
                    phoneNumberSpanText.getStringAnnotations(
                        KEY_ACTION, offset, offset
                    ).firstOrNull()?.let { annotation ->
                        if (annotation.item == OtpAnnotations.Phone.toString()) {
                            onEditPhonePressed()
                        }
                    }
                },
            )
            Icon(
                modifier = localModifier
                    .padding(start = MaterialTheme.spacing.unit2)
                    .height(MaterialTheme.spacing.unit10)
                    .clickable { onEditPhonePressed() },
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = localModifier.height(MaterialTheme.spacing.unit20))
        OtpTextField(
            modifier = localModifier.fillMaxWidth(),
            value = otp,
            onValueChange = onOtpChanged,
            onSubmit = onSubmit,
            focusedByDefault = focusedByDefault
        )
        Spacer(modifier = localModifier.height(MaterialTheme.spacing.grid1))
        if (!error.isNullOrEmpty()) Text(
            modifier = localModifier.align(Alignment.CenterHorizontally),
            text = error,
            style = MaterialTheme.textStyle.loginLabel,
            color = MaterialTheme.colorScheme.error,
        )
        if (!isLoading) {
            Spacer(modifier = localModifier.height(MaterialTheme.spacing.unit20))
            if (countdownTimer == 0) {
                ClickableText(
                    text = resendSpanText,
                    style = MaterialTheme.textStyle.otpDisplay,
                    onClick = { offset ->
                        resendSpanText.getStringAnnotations(
                            KEY_ACTION, offset, offset
                        ).firstOrNull()?.let { annotation ->
                            if (annotation.item == OtpAnnotations.Resend.toString()) {
                                onResendPressed()
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
        Spacer(modifier = localModifier.height(MaterialTheme.spacing.unit20))
        FilledTonalButton(
            modifier = localModifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.unit50),
            onClick = onSubmit,
            shape = MaterialTheme.customShapes.defaultButton,
            enabled = !isLoading,
        ) {
            if (!isLoading) {
                Text(
                    text = stringResource(R.string.button_submit_otp),
                    style = MaterialTheme.textStyle.loginDisplay,
                )
            } else {
                Row(
                    modifier = localModifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(MaterialTheme.spacing.grid2)
                    ) {
                        CircularProgressIndicator(strokeWidth = MaterialTheme.spacing.unit2)
                    }
                    Spacer(modifier = localModifier.width(MaterialTheme.spacing.grid1))
                    Text(
                        text = stringResource(R.string.message_verifying),
                        style = MaterialTheme.textStyle.loginDisplay,
                    )
                }
            }
        }
    }
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