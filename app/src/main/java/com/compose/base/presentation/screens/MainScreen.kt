package com.compose.base.presentation.screens

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.compose.base.R
import com.compose.base.data.repository.core.ConnectionState
import com.compose.base.presentation.screens.core.component.AnimateConnectionState
import com.compose.base.presentation.screens.core.dialog.DefaultAlert
import com.compose.base.presentation.screens.core.screen.NoInternetScreen
import com.compose.base.presentation.screens.core.screen.SplashScreen
import com.compose.base.presentation.util.enableGesture
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    intentState: StateFlow<Intent>,
    checkForUpdate: ((() -> Unit), (() -> Unit)) -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle(ConnectionState.Pending)
    var isLoading by remember {
        mutableStateOf(false)
    }

    Scaffold(modifier.enableGesture(!isLoading)) {
        AnimateConnectionState(
            modifier = modifier.fillMaxSize(),
            connectionState = connectionState,
            onConnected = {
                val appLoginState by viewModel.appLoginState.collectAsStateWithLifecycle()
                MainNavHost(
                    modifier = modifier,
                    intentState = intentState,
                    appLoginState = appLoginState,
                )

                if (connectionState == ConnectionState.Connected) {
                    when (appLoginState) {

                        AppLoginState.BlockApp -> {
                            DefaultAlert(
                                message = stringResource(R.string.message_app_block),
                                acceptButtonLabel = stringResource(R.string.button_retry),
                                onAccept = { viewModel.checkLoginState() },
                                onDismiss = {},
                            )
                        }

                        AppLoginState.ForceUpdate -> {
                            DefaultAlert(
                                message = stringResource(R.string.message_app_update),
                                acceptButtonLabel = stringResource(R.string.button_update),
                                onAccept = {
                                    isLoading = true
                                    checkForUpdate(
                                        {
                                            isLoading = false
                                        },
                                        {
                                            viewModel.updateAppLoginState(AppLoginState.Authorized)
                                            isLoading = false
                                        },
                                    )
                                },
                                onDismiss = {},
                            )
                        }

                        AppLoginState.UpdateError -> {
                            DefaultAlert(
                                message = stringResource(R.string.message_config_not_found),
                                acceptButtonLabel = stringResource(R.string.button_retry),
                                onAccept = { viewModel.checkLoginState() },
                                onDismiss = {},
                            )
                        }

                        else -> {}
                    }
                }

                LaunchedEffect(connectionState, appLoginState) {
                    if (connectionState == ConnectionState.Connected) {
                        when (appLoginState) {
                            AppLoginState.Authorized -> FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val firebaseToken = task.result
                                    viewModel.updateToken(firebaseToken)
                                }
                            }

                            AppLoginState.ImmediateUpdate -> {
                                checkForUpdate(
                                    {
                                        viewModel.updateAppLoginState(AppLoginState.ForceUpdate)
                                    },
                                    {
                                        viewModel.updateAppLoginState(AppLoginState.Authorized)
                                    },
                                )
                            }

//                            AppLoginState.FlexibleUpdate -> {}

                            else -> {}
                        }
                    }
                }
            },
            onDisconnected = { NoInternetScreen(onRetryConnection = viewModel::checkConnection) },
            onPending = { SplashScreen(modifier = modifier) },
        )
    }
}