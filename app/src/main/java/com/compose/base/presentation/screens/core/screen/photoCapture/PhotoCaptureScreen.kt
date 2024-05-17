package com.compose.base.presentation.screens.core.screen.photoCapture

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
import com.compose.base.presentation.screens.core.component.OnStop

@Composable
fun PhotoCaptureScreen(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onSubmit: (Uri) -> Unit,
    navigateUp: () -> Unit
) {
    val emptyImageUri = Uri.parse("file://dev/null")
    var imageUri by remember { mutableStateOf(emptyImageUri) }
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