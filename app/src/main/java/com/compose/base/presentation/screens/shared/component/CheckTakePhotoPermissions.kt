package com.compose.base.presentation.screens.shared.component

import android.os.Build
import androidx.compose.runtime.Composable

/**
 * Composable function for handling camera and storage permissions(if applicable) for taking photos.
 *
 * This composable simplifies permission checks for taking photos. It checks for both camera
 * permission (required for taking pictures) and external storage permission (required for saving
 * the captured photo on Android versions below Q).
 *
 * - `content`: A content lambda representing the composable content to be displayed
if both permissions are granted.
 *
 * This composable uses nested `CheckPermission` calls:
- The outer `CheckPermission` checks for camera permission.
- If camera permission is granted, it checks for external storage permission (only on Android P
and below).
- If all permissions are granted, the `content` composable is displayed.
 */
@Composable
fun CheckTakePhotoPermissions(content: @Composable () -> Unit) {
    CheckPermission(appPermission = AppPermission.Camera) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            CheckPermission(AppPermission.ExternalStorage) {
                content()
            }
        } else {
            content()
        }
    }
}