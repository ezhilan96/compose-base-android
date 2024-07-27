package com.compose.base.presentation.screens.shared.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.compose.base.R
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.util.requestLocationService
import com.google.android.gms.common.api.ResolvableApiException
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

enum class LocationServiceState { Requested, Enabled, Disabled }

/**
 * Check if location service is enabled and request it if not and
 * listens to location service changes and prompts the user to enable it if it is disabled.
 *
 * @param content The content to be executed if location service is enabled.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EnableLocationService(content: @Composable () -> Unit) {
    val context = LocalContext.current

    var locationServiceState by remember { mutableStateOf(LocationServiceState.Requested) }

    val locationSettingLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> by rememberUpdatedState(
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                locationServiceState = if (result.resultCode == Activity.RESULT_OK) {
                    LocationServiceState.Enabled
                } else {
                    LocationServiceState.Disabled
                }
            },
        )
    )

    // Broadcast receiver for location changes
    val locationServiceListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (locationServiceState == LocationServiceState.Enabled) {
                locationServiceState = LocationServiceState.Requested
            }
        }
    }

    when (locationServiceState) {
        LocationServiceState.Requested ->
            // System prompt to enable location service
            LaunchedEffect(Unit) {
                context.requestLocationService(
                    onSuccess = {
                        // If location service is enabled, set the state to enabled
                        locationServiceState = LocationServiceState.Enabled
                    },
                    onFailure = { exception ->
                        if (exception is ResolvableApiException) {
                            // If location service is disabled, launch the system location settings prompt if available
                            locationSettingLauncher?.launch(
                                IntentSenderRequest.Builder(exception.resolution.intentSender)
                                    .build()
                            )
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.message_enable_location_toast),
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                    },
                    onCancel = { locationServiceState = LocationServiceState.Disabled },
                )
            }

        LocationServiceState.Disabled -> {
            // If location service is disabled manually, show an alert dialog
            AlertDialog(
                title = { Text(stringResource(R.string.message_location_access_needed)) },
                text = { Text(stringResource(R.string.message_location_permission_request)) },
                onDismissRequest = {},
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Request location service on 'Enable location' button click
                            locationServiceState = LocationServiceState.Requested
                        },
                        shape = MaterialTheme.customShapes.defaultButton,
                    ) {
                        Text(text = stringResource(R.string.button_enable_location))
                    }
                },
            )
        }

        LocationServiceState.Enabled -> content()
    }

    // Register location service changes listener
    LaunchedEffect(Unit) {
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED)
        ContextCompat.registerReceiver(
            context,
            locationServiceListener,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    // Clean up the location service changes listener
    DisposableEffect(Unit) {
        onDispose {
            try {
                context.unregisterReceiver(locationServiceListener)
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
            }
        }
    }
}