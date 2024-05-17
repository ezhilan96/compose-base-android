package com.compose.base.presentation.screens.core.component

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.compose.base.R
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.util.UiText

@Composable
fun CheckPermission(
    appPermission: AppPermission,
    optional: Boolean = false,
    onFailure: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val context = LocalContext.current
    var isPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, appPermission.permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    if (isPermissionGranted) {
        content()
    } else {
        var isInitialRationale by remember {
            mutableStateOf(true)
        }
        var isRationaleAvailable = ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity,
            appPermission.permission,
        )
        var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>? =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {
                    isPermissionGranted = it
                    isInitialRationale = false
                    if (optional && !it) {
                        onFailure()
                    }
                },
            )
        when {
            isInitialRationale || isRationaleAvailable -> {
                AlertDialog(
                    title = { Text(text = stringResource(R.string.title_permissions_required)) },
                    onDismissRequest = {
                        if (optional) {
                            isRationaleAvailable = false
                        }
                    },
                    text = {
                        Text(text = getPermissionDescription(appPermission))
                    },
                    confirmButton = {
                        TextButton(onClick = { permissionLauncher?.launch(appPermission.permission) }) {
                            Text(text = stringResource(R.string.button_grant_permissions))
                        }
                    },
                )
            }

            !optional -> {
                AlertDialog(
                    modifier = Modifier.padding(MaterialTheme.spacing.grid2),
                    title = { Text(text = stringResource(R.string.title_permissions_declined_permanently)) },
                    onDismissRequest = { },
                    text = { Text(text = getDeclinedInstruction(appPermission)) },
                    confirmButton = {
                        TextButton(onClick = {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                val packageUri = UiText.Resource(
                                    R.string.uri_package, context.packageName
                                )
                                data = Uri.parse(packageUri.asString(context))
                                context.startActivity(this)
                            }
                        }) {
                            Text(text = stringResource(R.string.button_go_to_app_settings))
                        }
                    },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                )
            }

            else -> {
                LaunchedEffect(Unit) {
                    onFailure()
                }
            }
        }
        val currentLifecycleEvent = rememberLifecycleEvent()
        LaunchedEffect(currentLifecycleEvent) {
            if (currentLifecycleEvent == Lifecycle.Event.ON_RESUME) {
                isPermissionGranted = ContextCompat.checkSelfPermission(
                    context, appPermission.permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                permissionLauncher = null
            }
        }
    }
}

@SuppressLint("InlinedApi")
sealed class AppPermission(val name: String, val permission: String) {
    data object Location : AppPermission(
        name = "Location",
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
    )

    data object ExternalStorage : AppPermission(
        name = "External storage",
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    data object BackgroundLocation : AppPermission(
        name = "Background location",
        permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )

    data object Camera : AppPermission(
        name = "Camera",
        permission = Manifest.permission.CAMERA,
    )

    data object Notification : AppPermission(
        name = "Notification",
        permission = Manifest.permission.POST_NOTIFICATIONS,
    )
}

@Composable
fun getPermissionDescription(appPermission: AppPermission): AnnotatedString =
    if (appPermission == AppPermission.BackgroundLocation) {
        buildAnnotatedString {
            append("App needs")
            withStyle(
                style = SpanStyle(fontWeight = FontWeight.Black)
            ) {
                append(" Background location ")
            }
            append("permission to proceed further. To enable go to Settings > Apps > Compose base > Permissions > Location\nand select ")
            withStyle(
                style = SpanStyle(fontWeight = FontWeight.Black)
            ) {
                append("Allow all the time")
            }
        }
    } else {
        buildAnnotatedString {
            append("App needs")
            withStyle(
                style = SpanStyle(fontWeight = FontWeight.Black)
            ) {
                append(" ${appPermission.name}")
            }
            append(" permission to proceed further.")
        }
    }

@Composable
fun getDeclinedInstruction(appPermission: AppPermission): AnnotatedString =
    if (appPermission == AppPermission.BackgroundLocation || (appPermission == AppPermission.Location && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) buildAnnotatedString {
        append(stringResource(R.string.message_permission_instruction))
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Black)
        ) {
            append(" ${appPermission.name}")
        }
        append("\nand select ")
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Black)
        ) {
            append("Allow all the time")
        }
        append("\nand make sure ")
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Black)
        ) {
            append("Use precise location")
        }
        append(" is enabled")
    } else {
        buildAnnotatedString {
            append(stringResource(R.string.message_permission_instruction))
            withStyle(
                style = SpanStyle(fontWeight = FontWeight.Black)
            ) {
                append(" ${appPermission.name} ")
            }
            append("and allow permission")
        }
    }