@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)

package com.compose.base.presentation.screens.user.otp

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.compose.base.R
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle
import com.compose.base.presentation.screens.core.component.OnStop
import com.compose.base.presentation.screens.core.dialog.DefaultAlert
import com.compose.base.presentation.screens.user.otp.view.OtpInputView
import com.compose.base.presentation.screens.user.otp.viewModel.OtpNavigationItem
import com.compose.base.presentation.screens.user.otp.viewModel.OtpScreenUiEvent
import com.compose.base.presentation.screens.user.otp.viewModel.OtpScreenUiState
import com.compose.base.presentation.screens.user.otp.viewModel.OtpViewModel
import com.compose.base.presentation.util.enableGesture
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient

@Composable
fun OtpDestination(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    viewModel: OtpViewModel,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OtpScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel::onUiEvent,
        navigateUp = navigateUp,
    )

    if (uiState.navigationItems.contains(OtpNavigationItem.ALERT_DIALOG)) {
        DefaultAlert(
            modifier = modifier,
            message = uiState.alertMessage.asString(),
            onDismiss = { viewModel.onUiEvent(OtpScreenUiEvent.OnDismiss()) },
        )
    }

    BackHandler(
        enabled = uiState.navigationItems.isNotEmpty(),
        onBack = { viewModel.onUiEvent(OtpScreenUiEvent.OnDismiss()) },
    )

    OnStop(viewModel::onStop)

    LaunchedEffect(Unit) {
        val client: SmsRetrieverClient = SmsRetriever.getClient(context)
        client.startSmsRetriever().addOnCanceledListener {
            viewModel.onUiEvent(OtpScreenUiEvent.OnDismiss())
        }.addOnFailureListener {
            viewModel.onUiEvent(OtpScreenUiEvent.OnDismiss())
        }
    }

    LaunchedEffect(uiState.navigationItems) {
        uiState.navigationItems.forEach { navigationItem ->
            when (navigationItem) {
                OtpNavigationItem.OTP_TOAST -> {
                    Toast.makeText(
                        context, uiState.alertMessage.asString(context), Toast.LENGTH_SHORT
                    ).show()
                    viewModel.onUiEvent(OtpScreenUiEvent.OnDismiss())
                }

                else -> return@forEach
            }
        }
    }
}

@Composable
fun OtpScreen(
    modifier: Modifier = Modifier,
    uiState: OtpScreenUiState,
    uiEvent: (OtpScreenUiEvent) -> Unit = {},
    navigateUp: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        modifier = modifier.enableGesture(!uiState.isLoading),
        scaffoldState = bottomSheetScaffoldState,
        sheetDragHandle = {},
        sheetContent = {
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
                        onClick = { uiEvent(OtpScreenUiEvent.OnDismiss()) },
                        shape = MaterialTheme.customShapes.defaultButton,
                    ) {
                        Text(
                            text = stringResource(R.string.button_disable_auto_read),
                            style = MaterialTheme.textStyle.loginLabel,
                        )
                    }
                }
            }
        }
    ) { safeAreaPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(safeAreaPadding)
        ) {
            Column(
                modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {

                IconButton(
                    modifier = modifier
                        .padding(
                            horizontal = MaterialTheme.spacing.unit4,
                            vertical = MaterialTheme.spacing.grid1
                        ),
                    onClick = navigateUp,
                ) {
                    Icon(
                        modifier = modifier.padding(MaterialTheme.spacing.grid1),
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                    )
                }

                OtpInputView(
                    modifier = modifier.weight(1f),
                    phoneNumber = uiState.phoneNumber,
                    otp = uiState.otp,
                    countdownTimer = uiState.remainingTimeToResendOtp,
                    onOtpChanged = {
                        uiEvent(OtpScreenUiEvent.OnOTPChange(it))
                    },
                    onEditPhonePressed = navigateUp,
                    onResendPressed = { uiEvent(OtpScreenUiEvent.ResendOtp) },
                    onSubmit = {
                        keyboardController?.hide()
                        uiEvent(OtpScreenUiEvent.OnSubmitOtp)
                    },
                    isLoading = uiState.isLoading || uiState.navigationItems.contains(
                        OtpNavigationItem.DONE
                    ),
                    error = uiState.inputErrorMessage?.asString(),
                    focusedByDefault = !uiState.navigationItems.contains(OtpNavigationItem.AUTO_READ_BOTTOM_SHEET)
                )
            }
        }
    }

    LaunchedEffect(uiState.navigationItems) {
        if (uiState.navigationItems.contains(OtpNavigationItem.AUTO_READ_BOTTOM_SHEET)) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        } else {
            bottomSheetScaffoldState.bottomSheetState.hide()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun OtpScreenPreview() {
    ComposeBaseTheme {
        OtpScreen(modifier = Modifier, uiState = OtpScreenUiState(), uiEvent = {}, navigateUp = {})
    }
}