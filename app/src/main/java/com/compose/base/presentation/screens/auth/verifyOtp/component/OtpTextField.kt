package com.compose.base.presentation.screens.auth.verifyOtp.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle

/**
 * The OtpTextField composable represents a text field specifically designed for entering a 4-digit OTP code.
 *
 * This composable utilizes multiple `DigitInputField` composables to handle individual digit
 * entries and provides logic to manage focus movement between digits and handle complete OTP submission.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param value The current OTP value (String).
 * @param onValueChange A callback function to be triggered when the overall OTP value changes.
 * @param onSubmit A callback function to be called when the user submits the complete OTP.
 * @param focusedByDefault A boolean indicating if the first digit field should be focused initially.
 */
@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    focusedByDefault: Boolean = false,
) {
    val localModifier: Modifier = Modifier

    val focusManager = LocalFocusManager.current

    // Focus requesters to switch focus between digits on input and on backspace
    val firstFocusRequester = remember { FocusRequester() }
    val secondFocusRequester = remember { FocusRequester() }
    val thirdFocusRequester = remember { FocusRequester() }
    val fourthFocusRequester = remember { FocusRequester() }

    var otp1Value by remember { mutableStateOf(value.getOrNull(0)) }
    var otp2Value by remember { mutableStateOf(value.getOrNull(1)) }
    var otp3Value by remember { mutableStateOf(value.getOrNull(2)) }
    var otp4Value by remember { mutableStateOf(value.getOrNull(3)) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        DigitInputField(
            modifier = localModifier.focusRequester(firstFocusRequester),
            value = otp1Value?.toString() ?: "",
            onValueChange = {
                it.filter(Char::isDigit).let { filteredText ->
                    otp1Value = filteredText.firstOrNull()
                    if (filteredText.isNotEmpty()) {
                        secondFocusRequester.requestFocus()
                    }
                }
            },
            onSubmit = onSubmit,
        )
        DigitInputField(
            modifier = localModifier.focusRequester(secondFocusRequester),
            value = otp2Value?.toString() ?: "",
            onValueChange = {
                it.filter(Char::isDigit).let { filteredText ->
                    otp2Value = filteredText.firstOrNull()
                    if (filteredText.isNotEmpty()) {
                        thirdFocusRequester.requestFocus()
                    } else {
                        firstFocusRequester.requestFocus()
                    }
                }
            },
            onSubmit = onSubmit,
        )
        DigitInputField(
            modifier = localModifier.focusRequester(thirdFocusRequester),
            value = otp3Value?.toString() ?: "",
            onValueChange = {
                it.filter(Char::isDigit).let { filteredText ->
                    otp3Value = filteredText.firstOrNull()
                    if (filteredText.isNotEmpty()) {
                        fourthFocusRequester.requestFocus()
                    } else {
                        if (otp2Value != null) {
                            secondFocusRequester.requestFocus()
                        } else {
                            firstFocusRequester.requestFocus()
                        }
                    }
                }
            },
            onSubmit = onSubmit,
        )
        DigitInputField(
            modifier = localModifier.focusRequester(fourthFocusRequester),
            value = otp4Value?.toString() ?: "",
            onValueChange = {
                it.filter(Char::isDigit).let { filteredText ->
                    otp4Value = filteredText.firstOrNull()
                    if (filteredText.isEmpty()) {
                        when {
                            otp3Value != null -> {
                                thirdFocusRequester.requestFocus()
                            }

                            otp2Value != null -> {
                                secondFocusRequester.requestFocus()
                            }

                            else -> {
                                firstFocusRequester.requestFocus()
                            }
                        }
                    }
                }
            },
            onSubmit = onSubmit,
        )
    }

    LaunchedEffect(otp1Value, otp2Value, otp3Value, otp4Value) {
        val otp = listOfNotNull(otp1Value, otp2Value, otp3Value, otp4Value).joinToString("")
        onValueChange(otp)
        if (otp.length == 4) onSubmit()
    }

    LaunchedEffect(focusedByDefault) {
        if (focusedByDefault) {
            firstFocusRequester.requestFocus()
        } else {
            focusManager.clearFocus(true)
        }
    }
}

/**
 * The DigitInputField composable represents a single digit input field used within the `OtpTextField`.
 *
 * This composable utilizes an `OutlinedTextField` with specific configurations to restrict input
 * to a single digit and handle user interactions for entering and submitting the digit.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param value The current value (String) of the digit.
 * @param onValueChange A callback function to be triggered when the digit value changes.
 * @param onSubmit A callback function to be called when the user submits the digit (typically used for next digit focus in OtpTextField).
 */
@Composable
fun DigitInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    OutlinedTextField(
        modifier = modifier.size(MaterialTheme.spacing.unit50),
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.textStyle.otpInputBody,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number,
        ),
        keyboardActions = KeyboardActions(onNext = { onSubmit() }),
//        colors = OutlinedTextFieldDefaults.colors(
//            focusedBorderColor = MaterialTheme.customColors.black,
//            cursorColor = MaterialTheme.customColors.blue
//        )
    )
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview
@Composable
fun OtpTextFieldPreview() {
    ComposeBaseTheme {
        Surface {
            OtpTextField(
                value = "",
                onValueChange = {},
                onSubmit = {},
            )
        }
    }
}