package com.compose.base.presentation.screens.shared.component

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
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.util.UiText

/**
 * Sealed class representing different app permissions.
 *
 * This sealed class defines various permissions that the app might need to request from the user.
 * Each permission object has a `name` (human-readable description) and a `permission` (the actual Android permission string).
 */
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

/**
 * Composable function for handling permission requests and displaying rationale or settings dialogs.
 *
 * This composable takes an `AppPermission` object, optional parameters, and content lambda.
 * - `appPermission`: The AppPermission object representing the permission to be checked.
 * - `optional`: (Optional) A boolean indicating whether the permission is optional.
If true, the `onFailure` callback will be triggered if the user denies the permission.
- `onFailure`: (Optional) A callback function to be called if the permission is denied
and `optional` is true.
- `content`: A content lambda representing the composable content to be displayed
if the permission is granted.
 *
 * This composable checks the permission state and displays different UIs based on the status:
- If permission is granted: The content composable is displayed.
- If permission is denied but rationale should be shown: An AlertDialog is displayed explaining
why the permission is needed and requesting permission again.
- If permission is denied permanently (never ask again): An AlertDialog is displayed informing
the user that the permission is denied and providing a way to navigate to app settings.
- If permission is optional and denied: The `onFailure` callback is triggered (if provided).
 *
 * This composable also handles permission state changes during the lifecycle and cleans up the
 * permission launcher on composable disposal.
 */
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
            // isRationaleAvailable initially returns false so check isInitialRationale first
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

/**
 * Composable function to generate an AnnotatedString describing a specific AppPermission.
 *
 * This function takes an `AppPermission` object and returns an `AnnotatedString` describing
 * why the app needs that permission. The description is styled to highlight the permission name.
 *
 * This function provides a special case for BackgroundLocation permission, including more
 * detailed instructions on how to enable it in app settings.
 */
@Composable
fun getPermissionDescription(appPermission: AppPermission): AnnotatedString =
    if (appPermission == AppPermission.BackgroundLocation) {
        buildAnnotatedString {
            append("App needs")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.customColors.textDark,
                )
            ) {
                append(" Background location ")
            }
            append("permission to proceed further. To enable go to Settings > Apps > Compose base > Permissions > Location\nand select ")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.customColors.textDark,
                )
            ) {
                append("Allow all the time")
            }
        }
    } else {
        buildAnnotatedString {
            append("App needs")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.customColors.textDark,
                )
            ) {
                append(" ${appPermission.name}")
            }
            append(" permission to proceed further.")
        }
    }

/**
 * Composable function to generate an AnnotatedString with instructions for enabling a permanently denied permission.
 *
 * This function takes an `AppPermission` object and returns an `AnnotatedString` with instructions
 * on how to enable the permission in app settings. The permission name is styled for emphasis.
 *
 * This function provides a special case for BackgroundLocation and Location permissions (on Android Q+),
 * including more detailed instructions on enabling "Allow all the time" and "Use precise location".
 */
@Composable
fun getDeclinedInstruction(appPermission: AppPermission): AnnotatedString =
    if (appPermission == AppPermission.BackgroundLocation || (appPermission == AppPermission.Location && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) buildAnnotatedString {
        append(stringResource(R.string.message_permission_instruction))
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.customColors.textDark,
            )
        ) {
            append(" ${appPermission.name}")
        }
        append("\nand select ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.customColors.textDark,
            )
        ) {
            append("Allow all the time")
        }
        append("\nand make sure ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.customColors.textDark,
            )
        ) {
            append("Use precise location")
        }
        append(" is enabled")
    } else {
        buildAnnotatedString {
            append(stringResource(R.string.message_permission_instruction))
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.customColors.textDark,
                )
            ) {
                append(" ${appPermission.name} ")
            }
            append("and allow permission")
        }
    }