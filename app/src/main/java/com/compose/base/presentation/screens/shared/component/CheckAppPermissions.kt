package com.compose.base.presentation.screens.shared.component

import android.os.Build
import androidx.compose.runtime.Composable

/**
 * Check if all permissions required before app start-up are granted.
 * Required App permissions:
 * * Location
 * * Background Location
 * * Notification
 * even though app uses Camera, we request camera permission just before taking a picture.
 *
 * * @param content The content to be executed if all permissions are granted.
 */
@Composable
fun CheckAppPermissions(content: @Composable () -> Unit) {
    // Check Location Permission
    CheckPermission(AppPermission.Location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check Background Location Permission (Android Q+)
            CheckPermission(AppPermission.BackgroundLocation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Check Notification Permission (Android 13+)
                    CheckPermission(AppPermission.Notification) {
                        // All required permissions granted, proceed with content
                        content()
                    }
                } else {
                    // All required permissions granted, proceed with content
                    content()
                }
            }
        } else {
            // All required permissions granted, proceed with content
            content()
        }
    }
}