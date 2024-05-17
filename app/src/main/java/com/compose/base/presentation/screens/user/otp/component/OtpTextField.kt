package com.compose.base.presentation.screens.user.otp.component

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
import com.compose.base.presentation.config.colors
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle

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
                    if (filteredText.length == 4) onSubmit()
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colors.black,
        )
    )
}

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