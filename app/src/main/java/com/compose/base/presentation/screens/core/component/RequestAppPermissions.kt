package com.compose.base.presentation.screens.core.component

import android.os.Build
import androidx.compose.runtime.Composable

@Composable
fun RequestAppPermissions(content: @Composable () -> Unit) {
    CheckPermission(AppPermission.Location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            CheckPermission(AppPermission.BackgroundLocation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    CheckPermission(AppPermission.Notification) {
                        content()
                    }
                } else {
                    content()
                }
            }
        } else {
            content()
        }
    }
}

@Composable
fun RequestCameraPermissions(content: @Composable () -> Unit) {
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