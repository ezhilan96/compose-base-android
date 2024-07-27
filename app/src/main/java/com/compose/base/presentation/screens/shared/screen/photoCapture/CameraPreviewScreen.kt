package com.compose.base.presentation.screens.shared.screen.photoCapture

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.compose.base.R
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle
import com.compose.base.presentation.util.enableGesture
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Composable function for displaying camera preview and capture functionality.
 *
 * This composable provides a live camera preview based on the selected camera
 * (`cameraSelector`). It allows users to take a picture and handles the capture process.
 *
 * - `modifier`: (Optional) A modifier to be applied to the entire screen.
 * - `cameraSelector`: The camera to be used (default: back camera).
 * - `onResult`: A callback function to be executed when a picture is taken,
providing the captured image Uri.
 * - `navigateUp`: A callback function to be executed when the user navigates up (likely goes back).
 *
 * This composable utilizes various Jetpack Compose and Android functionalities:
 * - `Preview` for displaying the camera feed.
 * - `ImageCapture` for capturing images.
 * - `IconButton` for the close button.
 * - `TextButton` for the capture button.
 * - `LaunchedEffect` to bind the camera preview and image capture with lifecycle.
 * - `takePicture` helper function to handle image capture logic.
 */
@SuppressLint("RestrictedApi")
@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onResult: (Uri) -> Unit,
    navigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isLoading by remember { mutableStateOf(false) }
    val preview = Preview.Builder().build()
    val previewView = PreviewView(context)
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCameraSelector(cameraSelector)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    val outlineWidth = ButtonDefaults.outlinedButtonBorder.width
    val localDensity = LocalDensity.current
    var mainBoxHeight by remember {
        mutableStateOf(0.dp)
    }
    var flashEnabled by remember {
        mutableStateOf(false)
    }
    var camera: Camera? by remember {
        mutableStateOf(null)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .enableGesture(!isLoading)
    ) {
        AndroidView(modifier = modifier.fillMaxSize(), factory = { previewView })

        // Back button
        IconButton(
            modifier = modifier
                .align(Alignment.TopStart)
                .padding(
                    start = MaterialTheme.spacing.grid05,
                    top = MaterialTheme.spacing.grid1,
                ),
            onClick = navigateUp,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(.25f),
                contentColor = Color.White,
            ),
        ) {
            Icon(
                modifier = modifier.padding(MaterialTheme.spacing.grid1),
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = null,
            )
        }

        // Flash button
        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            IconButton(
                modifier = modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        start = MaterialTheme.spacing.grid05,
                        top = MaterialTheme.spacing.grid1,
                    ),
                onClick = {
                    camera?.cameraControl?.enableTorch(!flashEnabled)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(.25f),
                    contentColor = Color.White,
                ),
            ) {
                Icon(
                    modifier = modifier.padding(MaterialTheme.spacing.grid1),
                    imageVector = if (flashEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                    contentDescription = null,
                )
            }
        }

        // Bottom sheet outline
        Box(
            modifier = modifier
                .height(mainBoxHeight.plus(outlineWidth))
                .background(
                    color = MaterialTheme.customColors.darkVariant,
                    shape = MaterialTheme.customShapes.defaultBottomSheet,
                )
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Text(text = "")
        }

        // Bottom sheet
        Box(modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.customShapes.defaultBottomSheet,
            )
            .align(Alignment.BottomCenter)
            .onGloballyPositioned { coordinates ->
                mainBoxHeight = with(localDensity) { coordinates.size.height.toDp() }
            }) {

            // Capture button
            TextButton(
                modifier = modifier
                    .align(Alignment.Center)
                    .padding(MaterialTheme.spacing.grid1),
                onClick = {
                    isLoading = true
                    takePicture(
                        context = context,
                        imageCapture,
                    ) {
                        if (camera?.cameraInfo?.hasFlashUnit() == true) {
                            camera?.cameraControl?.enableTorch(false)
                        }
                        it?.let { onResult(it) } ?: run {
                            Toast.makeText(context, "Failed to take photo", Toast.LENGTH_SHORT)
                                .show()
                        }
                        isLoading = false
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface
                ),
                shape = MaterialTheme.customShapes.defaultButton,
            ) {
                Text(
                    text = stringResource(id = R.string.button_take_photo),
                    style = MaterialTheme.textStyle.ubuntuLabel,
                )
            }
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = Color.White.copy(alpha = .7f))
            ) {
                CircularProgressIndicator(
                    modifier = modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
    LaunchedEffect(lifecycleOwner) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageCapture
        )
        camera?.cameraInfo?.torchState?.observe(lifecycleOwner) {
            flashEnabled = it == TorchState.ON
        }
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
}

// Helper function for image capturing
private fun takePicture(context: Context, imageCapture: ImageCapture, onResult: (Uri?) -> Unit) {
    val name =
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            val appName = context.resources.getString(R.string.app_name)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${appName}")
        }
    }
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues,
    ).build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onResult(outputFileResults.savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onResult(null)
            }

        },
    )
}

// Suspend function to get the CameraProvider instance
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraPreviewScreenPreview() {
    ComposeBaseTheme {
        CameraPreviewScreen(onResult = {}) {}
    }
}