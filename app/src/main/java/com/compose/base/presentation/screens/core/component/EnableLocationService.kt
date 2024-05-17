package com.compose.base.presentation.screens.core.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.compose.base.R
import com.compose.base.presentation.config.customShapes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient

@Composable
fun EnableLocationService(
    optional: Boolean = false,
    onFailure: () -> Unit = {},
    onSuccess: () -> Unit,
) {
    val context = LocalContext.current
    var enableLocationRequestAlert by remember { mutableStateOf(false) }
    var isLocationEnabled by remember { mutableStateOf(false) }
    var locationSettingLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>? =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == Activity.RESULT_OK) isLocationEnabled = true
                else enableLocationRequestAlert = true
            },
        )

    if (isLocationEnabled) {
        LaunchedEffect(Unit) {
            onSuccess()
        }
    } else {
        if (enableLocationRequestAlert) {
            if (!optional) {
                AlertDialog(
                    title = { Text(stringResource(R.string.message_location_access_needed)) },
                    text = { Text(stringResource(R.string.message_location_permission_request)) },
                    onDismissRequest = {},
                    confirmButton = {
                        TextButton(
                            onClick = { enableLocationRequestAlert = false },
                            shape = MaterialTheme.customShapes.defaultButton,
                        ) {
                            Text(text = stringResource(R.string.button_enable_location))
                        }
                    },
                )
            } else {
                LaunchedEffect(Unit) {
                    onFailure()
                }
            }
        }

        LaunchedEffect(enableLocationRequestAlert) {
            if (!enableLocationRequestAlert) {
                requestLocationService(
                    context = context,
                    onSuccess = { isLocationEnabled = true },
                    onFailure = {
                        if (it is ResolvableApiException) {
                            locationSettingLauncher?.launch(
                                IntentSenderRequest.Builder(it.resolution.intentSender).build()
                            )
                        }
                    },
                    onCancel = { enableLocationRequestAlert = true },
                )
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                locationSettingLauncher = null
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun requestLocationService(
    context: Context,
    onSuccess: (LocationSettingsResponse) -> Unit,
    onCancel: () -> Unit,
    onFailure: (Exception) -> Unit,
) {
    val mLocationRequest = LocationRequest.Builder(0).build()
    val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
    val client: SettingsClient = LocationServices.getSettingsClient(context)
    val checkLocationSettingsTask = client.checkLocationSettings(builder.build())
    checkLocationSettingsTask.addOnSuccessListener(onSuccess).addOnCanceledListener(onCancel)
        .addOnFailureListener(onFailure)
}