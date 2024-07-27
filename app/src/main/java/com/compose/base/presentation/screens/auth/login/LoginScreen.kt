package com.compose.base.presentation.screens.auth.login

import android.app.Activity
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.compose.base.R
import com.compose.base.core.Constants.KEY_ACTION
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle
import com.compose.base.presentation.screens.auth.login.viewModel.LoginNavigationItem
import com.compose.base.presentation.screens.auth.login.viewModel.LoginScreenState
import com.compose.base.presentation.screens.auth.login.viewModel.LoginUiEvent
import com.compose.base.presentation.screens.auth.login.viewModel.LoginViewModel
import com.compose.base.presentation.screens.auth.login.viewModel.PhoneAnnotations
import com.compose.base.presentation.screens.core.ScreenNavItem
import com.compose.base.presentation.screens.core.ScreenUiState
import com.compose.base.presentation.screens.shared.component.OnStop
import com.compose.base.presentation.screens.shared.dialog.DefaultAlert
import com.compose.base.presentation.util.enableGesture
import com.compose.base.presentation.util.getCustomerUrl
import com.compose.base.presentation.util.getPhoneNumberHintIntent
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

/**
 * The LoginDestination composable represents the login screen within the authentication flow.
 *
 * This composable handles data fetching, state management, and navigation based on user interactions.
 * It utilizes the LoginViewModel to handle login logic and communicates UI events to the view model.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param onOtpSent A callback function invoked when the login process completes successfully,
 *        providing the phone number for OTP verification.
 * @param viewModel A reference to the LoginViewModel instance for login-specific logic.
 */
@Composable
fun LoginDestination(
    modifier: Modifier = Modifier,
    onOtpSent: (String) -> Unit,
    viewModel: LoginViewModel,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel::onUiEvent,
    )

    if (uiState.screenStack.contains(ScreenNavItem.ALERT_DIALOG)) {
        DefaultAlert(
            modifier = modifier,
            message = uiState.alertMessage.asString(),
            onDismiss = { viewModel.onUiEvent(LoginUiEvent.OnDismiss) },
        )
    }

    BackHandler(
        enabled = uiState.screenStack.isNotEmpty(),
        onBack = { viewModel.onUiEvent(LoginUiEvent.OnDismiss) },
    )

    OnStop(viewModel::onStop)

    LaunchedEffect(uiState.screenState.enableOtpSentMessage) {
        if (uiState.screenState.enableOtpSentMessage) {
            Toast.makeText(
                context,
                uiState.screenState.successMessage?.asString(context),
                Toast.LENGTH_SHORT,
            ).show()
            // Remove the Navigation item from the stack after displaying the success message.
            viewModel.onUiEvent(LoginUiEvent.OnDisableOtpSentMessage)
        }
    }

    LaunchedEffect(uiState.screenStack) {
        if (uiState.screenStack.contains(LoginNavigationItem.DONE)) {
            onOtpSent(uiState.screenState.phoneNumber)
        }
    }

    LaunchedEffect(Unit) {
        // Enable edge-to-edge system UI to match the StatusBar and NavigationBar colors to the Login screen.
        (context as ComponentActivity).enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                ContextCompat.getColor(context, R.color.black)
            ),
            navigationBarStyle = SystemBarStyle.light(
                // The scrim color to be used for the background.
                // It is expected to be light for the contrast against the dark system icons.
                ContextCompat.getColor(context, R.color.white),
                // The scrim color to be used for the background on devices where the system icon color is always light.
                // It is expected to be dark.
                ContextCompat.getColor(context, R.color.light)
            ),
        )
    }
}

/**
 * The LoginScreen composable represents the visual layout of the login screen that can be previewed.
 *
 * This composable focuses on presenting UI elements and handling user input events. It receives data
 * and event callbacks from the LoginDestination composable.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param uiState The current UI state containing login screen data.
 * @param uiEvent A callback function to send UI events (like button clicks) to the LoginViewModel.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: ScreenUiState<LoginScreenState>,
    uiEvent: (LoginUiEvent) -> Unit = {},
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val contentColor = LocalContentColor.current
    val customerBaseUrl = getCustomerUrl()
    val phoneInputFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val termAndPrivacySpanText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = contentColor)) {
            append("By continuing, you agree to the ")
        }
        pushStringAnnotation(
            tag = KEY_ACTION, annotation = PhoneAnnotations.terms.toString()
        )
        withStyle(
            style = SpanStyle(color = MaterialTheme.colorScheme.primary)
        ) {
            append("terms")
        }
        pop()
        withStyle(style = SpanStyle(color = contentColor)) {
            append(" and\n")
        }
        pushStringAnnotation(
            tag = KEY_ACTION,
            annotation = PhoneAnnotations.policy.toString(),
        )
        withStyle(
            style = SpanStyle(color = MaterialTheme.colorScheme.primary)
        ) {
            append("privacy policy")
        }
        pop()
        withStyle(style = SpanStyle(color = contentColor)) {
            append(" of Compose base")
        }
    }
    Scaffold(modifier = modifier.enableGesture(!uiState.isLoading)) { safeAreaPadding ->

        // Phone number hint launcher
        var phoneHintLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>? =
            rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val phoneNumberHint = try {
                        Identity.getSignInClient(context as Activity)
                            .getPhoneNumberFromIntent(result.data)
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        val credential: Credential? =
                            result.data?.getParcelableExtra(Credential.EXTRA_KEY)
                        credential?.id
                    }?.takeLast(10)
                    phoneNumberHint?.let {
                        uiEvent(LoginUiEvent.OnPhoneChange(it))
                    }
                }
            }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(safeAreaPadding)
                .padding(
                    vertical = MaterialTheme.spacing.grid5,
                    horizontal = MaterialTheme.spacing.unit20
                ),
            verticalArrangement = Arrangement.SpaceBetween,
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
                            // If the focus state changes to focused and the phone number is empty,
                            // show the phone number hint.
                            if (focusState.isFocused && uiState.screenState.phoneNumber.isEmpty()) {
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
                    value = uiState.screenState.phoneNumber,
                    onValueChange = {
                        it.filter(Char::isDigit).let { filteredText ->
                            // This ignores country code from the Phone hint provider.
                            uiEvent(LoginUiEvent.OnPhoneChange((filteredText.takeLast(10))))
                            // Hide the keyboard after entering 10 digits so that the user can see the
                            // Terms and privacy checkbox and submit button.
                            if (filteredText.length >= 10) {
                                keyboardController?.hide()
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
                            color = MaterialTheme.customColors.lightVariant,
                        )
                    },
                    textStyle = MaterialTheme.textStyle.loginBody,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number,
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        uiEvent(LoginUiEvent.OnSubmitPhone)
                    }),
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedBorderColor = MaterialTheme.customColors.black,
//                                cursorColor = MaterialTheme.customColors.blue
//                            ),
                    trailingIcon = {
                        // Clear button
                        if (uiState.screenState.phoneNumber.isNotEmpty()) IconButton(onClick = {
                            uiEvent(LoginUiEvent.OnPhoneChange(""))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear, contentDescription = null
                            )
                        }
                    },
                    supportingText = {
                        if (!uiState.screenState.inputErrorMessage?.asString().isNullOrEmpty()) {
                            Text(text = uiState.screenState.inputErrorMessage?.asString()!!)
                        }
                    },
                    isError = !uiState.screenState.inputErrorMessage?.asString().isNullOrEmpty(),
                )
                Spacer(modifier = modifier.height(MaterialTheme.spacing.unit20))

                FilledTonalButton(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.grid6),
                    onClick = { uiEvent(LoginUiEvent.OnSubmitPhone) },
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = uiState.screenState.isTermsChecked,
                    onCheckedChange = { uiEvent(LoginUiEvent.OnTermsCheckChanged(it)) },
                )
                ClickableText(
                    modifier = modifier.fillMaxWidth(),
                    text = termAndPrivacySpanText,
                    style = MaterialTheme.textStyle.termsNote.copy(
                        textAlign = TextAlign.Start,
                        lineHeight = 20.sp,
                    ),
                    onClick = { offset ->
                        termAndPrivacySpanText.getStringAnnotations(
                            KEY_ACTION, offset, offset
                        ).firstOrNull()?.let { annotation ->
                            uriHandler.openUri("$customerBaseUrl${annotation.item}")
                        }
                    },
                )
            }
        }

        LaunchedEffect(Unit) {
            if (uiState.screenState.phoneNumber.isEmpty()) {
                phoneInputFocusRequester.requestFocus()
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                phoneHintLauncher = null
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
fun LoginScreenPreview() {
    ComposeBaseTheme {
        LoginScreen(
            uiState = ScreenUiState(screenState = LoginScreenState())
        )
    }
}