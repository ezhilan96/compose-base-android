package com.compose.base.presentation.screens.user.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle
import com.compose.base.presentation.screens.core.component.AppPermission
import com.compose.base.presentation.screens.core.component.CheckPermission
import com.compose.base.presentation.screens.core.component.EnableLocationService
import com.compose.base.presentation.screens.core.component.OnStop
import com.compose.base.presentation.screens.core.dialog.DefaultAlert
import com.compose.base.presentation.screens.user.login.viewModel.LoginNavigationItem
import com.compose.base.presentation.screens.user.login.viewModel.LoginScreenUiState
import com.compose.base.presentation.screens.user.login.viewModel.LoginUiEvent
import com.compose.base.presentation.screens.user.login.viewModel.LoginViewModel
import com.compose.base.presentation.util.enableGesture
import com.compose.base.presentation.util.getPhoneNumberHintIntent
import com.compose.base.presentation.util.requestCurrentLocation
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.identity.Identity

@Composable
fun LoginDestination(
    modifier: Modifier = Modifier,
    navigateToOtp: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel::onUiEvent,
    )

    if (uiState.navigationItems.contains(LoginNavigationItem.REQUEST_CURRENT_LOCATION)) {
        CheckPermission(
            appPermission = AppPermission.Location,
            optional = true,
            onFailure = { viewModel.onUiEvent(LoginUiEvent.OnLocationResult()) },
        ) {
            EnableLocationService(
                optional = true,
                onFailure = { viewModel.onUiEvent(LoginUiEvent.OnLocationResult()) },
            ) {
                context.requestCurrentLocation {
                    viewModel.onUiEvent(LoginUiEvent.OnLocationResult(it))
                }
            }
        }
    }

    if (uiState.navigationItems.contains(LoginNavigationItem.ALERT_DIALOG)) {
        DefaultAlert(
            modifier = modifier,
            message = uiState.alertMessage.asString(),
            onDismiss = { viewModel.onUiEvent(LoginUiEvent.OnDismiss()) },
        )
    }

    BackHandler(
        enabled = uiState.navigationItems.isNotEmpty(),
        onBack = { viewModel.onUiEvent(LoginUiEvent.OnDismiss()) },
    )

    OnStop(viewModel::onStop)

    LaunchedEffect(uiState.enableOtpDeliveryMessage) {
        if (uiState.enableOtpDeliveryMessage) {
            Toast.makeText(context, uiState.alertMessage.asString(context), Toast.LENGTH_SHORT)
                .show()
            viewModel.onUiEvent(LoginUiEvent.OnDisableOtpDeliveryMessage)
        }
    }

    LaunchedEffect(uiState.navigationItems) {
        if (uiState.navigationItems.contains(LoginNavigationItem.DONE)) {
            navigateToOtp(uiState.phoneNumber)
        }
    }
}

enum class PhoneAnnotations { terms, policy }

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginScreenUiState,
    uiEvent: (LoginUiEvent) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(modifier = modifier.enableGesture(!uiState.isLoading)) { safeAreaPadding ->
        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current
        val customerBaseUrl = stringResource(R.string.base_url)
        val phoneInputFocusRequester = remember { FocusRequester() }
        val termAndPrivacySpanText = buildAnnotatedString {
            append("By continuing, you agree to the ")
            pushStringAnnotation(
                tag = Constants.KEY_ACTION, annotation = PhoneAnnotations.terms.toString()
            )
            withStyle(
                style = SpanStyle(color = MaterialTheme.colorScheme.primary)
            ) {
                append("terms")
            }
            pop()
            append(" and\n")
            pushStringAnnotation(
                tag = Constants.KEY_ACTION,
                annotation = PhoneAnnotations.policy.toString(),
            )
            withStyle(
                style = SpanStyle(color = MaterialTheme.colorScheme.primary)
            ) {
                append("privacy policy")
            }
            pop()
            append(" of Taxida")
        }
        var phoneHintLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>? =
            rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                val phoneNumberHint = try {
                    Identity.getSignInClient(context as Activity)
                        .getPhoneNumberFromIntent(result.data)
                } catch (e: Exception) {
                    e.printStackTrace()
                    val credential: Credential? =
                        result.data?.getParcelableExtra(Credential.EXTRA_KEY)
                    credential?.id
                }?.takeLast(10)
                phoneNumberHint?.let {
                    uiEvent(LoginUiEvent.OnPhoneChange(it))
                    keyboardController?.hide()
                    uiEvent(LoginUiEvent.OnSubmitPhone)
                }
            }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(safeAreaPadding)
                .padding(
                    vertical = MaterialTheme.spacing.grid5,
                    horizontal = MaterialTheme.spacing.unit20
                ), verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.label_phone),
                    style = MaterialTheme.textStyle.loginHeadline,
                )
                Spacer(modifier = modifier.height(MaterialTheme.spacing.grid1))
                Text(
                    text = stringResource(R.string.message_phone),
                    style = MaterialTheme.textStyle.loginLabel,
                )
                Spacer(modifier = modifier.height(MaterialTheme.spacing.unit20))
                OutlinedTextField(
                    modifier = modifier
                        .focusRequester(phoneInputFocusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && uiState.phoneNumber.isEmpty()) {
                                context.getPhoneNumberHintIntent {
                                    it?.let {
                                        val intentSenderRequest = IntentSenderRequest
                                            .Builder(it.intentSender)
                                            .build()
                                        phoneHintLauncher?.launch(intentSenderRequest)
                                    }
                                }
                            }
                        }
                        .fillMaxWidth(),
                    value = uiState.phoneNumber,
                    onValueChange = {
                        it.filter(Char::isDigit).let { filteredText ->
                            uiEvent(LoginUiEvent.OnPhoneChange(filteredText.takeLast(10)))
                            if (filteredText.length >= 10) {
                                keyboardController?.hide()
                                uiEvent(LoginUiEvent.OnSubmitPhone)
                            }
                        }
                    },
                    prefix = {
                        Text(
                            modifier = modifier.padding(end = MaterialTheme.spacing.grid2),
                            text = stringResource(R.string.country_code),
                            style = MaterialTheme.textStyle.loginDisplay,
                        )
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.label_phone),
                            style = MaterialTheme.textStyle.loginLabelLarge,
                        )
                    },
                    textStyle = MaterialTheme.textStyle.loginBody,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number,
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        keyboardController?.hide()
                        uiEvent(LoginUiEvent.OnSubmitPhone)
                    }),
                    trailingIcon = {
                        if (uiState.phoneNumber.isNotEmpty()) {
                            IconButton(onClick = {
                                uiEvent(LoginUiEvent.OnPhoneChange(""))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                    supportingText = {
                        if (!uiState.inputErrorMessage?.asString().isNullOrEmpty()) {
                            Text(text = uiState.inputErrorMessage?.asString()!!)
                        }
                    },
                    isError = !uiState.inputErrorMessage?.asString().isNullOrEmpty(),
                )
                Spacer(modifier = modifier.height(MaterialTheme.spacing.unit20))

                FilledTonalButton(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.unit50),
                    onClick = {
                        keyboardController?.hide()
                        uiEvent(LoginUiEvent.OnSubmitPhone)
                    },
                    shape = MaterialTheme.customShapes.defaultButton,
                    enabled = !uiState.isLoading,
                ) {
                    if (!uiState.isLoading) {
                        Text(
                            text = stringResource(R.string.button_submit_phone),
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
                Spacer(modifier = modifier.height(MaterialTheme.spacing.unit20))
            }

            ClickableText(
                modifier = modifier.fillMaxWidth(),
                text = termAndPrivacySpanText,
                style = MaterialTheme.textStyle.termsNote.copy(
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { offset ->
                    termAndPrivacySpanText.getStringAnnotations(
                        Constants.KEY_ACTION, offset, offset
                    ).firstOrNull()?.let { annotation ->
                        uriHandler.openUri("$customerBaseUrl${annotation.item}")
                    }
                },
            )
        }

        LaunchedEffect(Unit) {
            if (uiState.phoneNumber.isEmpty()) phoneInputFocusRequester.requestFocus()
        }

        DisposableEffect(Unit) {
            onDispose {
                phoneHintLauncher = null
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    ComposeBaseTheme {
        LoginScreen(
            uiState = LoginScreenUiState()
        )
    }
}