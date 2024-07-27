package com.compose.base.presentation.screens

import android.annotation.SuppressLint
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
import com.google.firebase.messaging.FirebaseMessaging
import com.compose.base.R
import com.compose.base.data.repository.core.ConnectionState
import com.compose.base.presentation.screens.shared.component.AnimateConnectionState
import com.compose.base.presentation.screens.shared.dialog.DefaultAlert
import com.compose.base.presentation.screens.shared.screen.NoInternetScreen
import com.compose.base.presentation.screens.shared.screen.SplashScreen
import com.compose.base.presentation.util.enableGesture

/**
 * The main screen composable of the application.
 *
 * This composable function displays the main user interface of the app based on the current
 * connection state, app login state, and other factors. It utilizes various Jetpack Compose
 * components for layout and functionality.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param checkForUpdate A lambda function responsible for checking for app updates and initiating
 * the update flow.
 * @param viewModel A reference to the MainViewModel instance used for data and logic.
 *
 * @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") (Optional Annotation)
 *   - This annotation suppresses a potential lint warning about an unused parameter in the
 *     Scaffold composable.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    checkForUpdate: ((() -> Unit), (() -> Unit)) -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
) {
    //Network connection state
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle(ConnectionState.Pending)

    //Disable screen gestures while checking for app update
    var isLoading by remember {
        mutableStateOf(false)
    }

    Scaffold(modifier.enableGesture(!isLoading)) {
        AnimateConnectionState(
            modifier = modifier.fillMaxSize(),
            connectionState = connectionState,
            connectedContent = {
                val appLoginState by viewModel.appState.collectAsStateWithLifecycle()
                MainNavHost(
                    modifier = modifier,
                    appState = appLoginState,
                )

                //Show App update prompts only when connection is established
                if (connectionState == ConnectionState.Connected) {
                    when (appLoginState) {

                        AppState.BlockApp -> {
                            DefaultAlert(
                                message = stringResource(R.string.message_app_block),
                                acceptButtonLabel = stringResource(R.string.button_retry),
                                onAccept = {
                                    viewModel.checkLoginState()
                                },
                                onDismiss = {},
                            )
                        }

                        AppState.ImmediateUpdate -> {
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
                                            viewModel.updateAppLoginState(AppState.Authorized)
                                            isLoading = false
                                        },
                                    )
                                },
                                onDismiss = {},
                            )
                        }

                        AppState.ConfigError -> {
                            DefaultAlert(
                                message = stringResource(R.string.message_config_not_found),
                                acceptButtonLabel = stringResource(R.string.button_retry),
                                onAccept = {
                                    viewModel.checkLoginState()
                                },
                                onDismiss = {},
                            )
                        }

                        else -> {}
                    }
                }

                LaunchedEffect(connectionState, appLoginState) {
                    if (connectionState == ConnectionState.Connected) {
                        when (appLoginState) {
                            AppState.Authorized -> {
                                //Update firebase token once the connection is established & the user is authorized
                                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val firebaseToken = task.result
                                        viewModel.updateToken(firebaseToken)
                                    }
                                }
                            }

                            AppState.ImmediateUpdate -> {
                                //Start In-app update flow
                                checkForUpdate(
                                    { viewModel.updateAppLoginState(AppState.ImmediateUpdate) },
                                    { viewModel.updateAppLoginState(AppState.Authorized) },
                                )
                            }

                            else -> {}
                        }
                    }
                }
            },
            noInternetContent = { NoInternetScreen(onRetryConnection = viewModel::checkConnection) },
            pendingContent = { SplashScreen(modifier = modifier) },
        )
    }
}