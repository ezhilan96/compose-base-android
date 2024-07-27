package com.compose.base.presentation.screens.shared.screen.photoCapture

import android.app.Activity
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.compose.base.presentation.screens.shared.component.OnStop

/**
 * Composable function for capturing and potentially submitting a photo.
 *
 * This composable offers a screen for users to take a photo or select an existing one
 * (if already captured). It handles camera selection, image capture, and provides options
 * to retake, discard, or submit the captured image.
 *
 * - `modifier`: (Optional) A modifier to be applied to the entire screen.
 * - `cameraSelector`: The camera to be used (default: back camera).
 * - `onSubmit`: A callback function to be executed when the user submits the photo (providing its Uri).
 * - `navigateUp`: A callback function to be executed when the user navigates up (likely goes back).
 *
 * This composable utilizes conditional rendering based on the `imageUri` state variable:
 * - If `imageUri` is empty, it displays the `CameraPreviewScreen`.
 * - If `imageUri` is not empty, it displays the `PhotoPreviewScreen`.
 */
@Composable
fun PhotoCaptureScreen(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onSubmit: (Uri) -> Unit,
    navigateUp: () -> Unit
) {
    val emptyImageUri = Uri.parse("file://dev/null")
    var imageUri by remember { mutableStateOf(emptyImageUri) }

    // Accessing system resources to hide system bars (full screen)
    val view = LocalView.current
    val context = LocalContext.current
    val window = (view.context as Activity).window
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (imageUri != emptyImageUri) {
            PhotoPreviewScreen(
                modifier = modifier,
                imageUri = imageUri,
                onClearImage = {
                    imageUri = emptyImageUri
                    context.cacheDir.deleteRecursively()
                    context.cacheDir.mkdir()
                },
                onSubmit = onSubmit,
                navigateUp = navigateUp,
            )
        } else {
            CameraPreviewScreen(
                modifier = modifier,
                cameraSelector = cameraSelector,
                onResult = { imageUri = it },
                navigateUp = navigateUp,
            )
        }
    }

    LaunchedEffect(Unit) {
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    OnStop {
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }
}