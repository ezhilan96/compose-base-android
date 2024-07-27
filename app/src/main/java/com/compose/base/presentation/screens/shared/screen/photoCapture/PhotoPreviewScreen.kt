package com.compose.base.presentation.screens.shared.screen.photoCapture

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.compose.base.R
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle
import com.compose.base.presentation.util.enableGesture

/**
 * Composable function for displaying a captured photo and offering retake or submit options.
 *
 * This composable displays the captured image (`imageUri`) and provides buttons for
 * retaking the photo or submitting it for further processing. It also shows a loading
 * indicator while processing the image.
 *
 * - `modifier`: (Optional) A modifier to be applied to the entire screen.
 * - `imageUri`: The Uri of the captured image to be displayed.
 * - `onClearImage`: A callback function to be executed when the user wants to retake the photo (clears the current image and goes back to camera preview).
 * - `onSubmit`: A callback function to be executed when the user wants to submit the captured image (passes the image Uri).
 * - `navigateUp`: A callback function to be executed when the user navigates up (likely goes back).
 */
@Composable
fun PhotoPreviewScreen(
    modifier: Modifier = Modifier,
    imageUri: Uri,
    onClearImage: () -> Unit,
    onSubmit: (Uri) -> Unit,
    navigateUp: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .enableGesture(!isLoading)
    ) {

        // Preview the captured image
        Image(
            modifier = modifier.fillMaxSize(),
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null
        )

        // Close button
        IconButton(
            modifier = modifier
                .padding(
                    horizontal = MaterialTheme.spacing.grid05,
                    vertical = MaterialTheme.spacing.grid1
                )
                .align(Alignment.TopStart),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(.25f), contentColor = Color.White
            ),
            onClick = {
                onClearImage()
                navigateUp()
            },
        ) {
            Icon(
                modifier = modifier.padding(MaterialTheme.spacing.grid1),
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = null,
            )
        }

        // Bottom sheet border calculations
        val outlineWidth = ButtonDefaults.outlinedButtonBorder.width
        val localDensity = LocalDensity.current
        var mainBoxHeight by remember {
            mutableStateOf(0.dp)
        }

        // Bottom sheet border
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

        // Bottom sheet with retake and submit buttons
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
            Row(
                modifier = modifier
                    .padding(MaterialTheme.spacing.grid1)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter), horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Retake button
                TextButton(
                    onClick = {
                        isLoading = true
                        onClearImage()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                    ),
                    shape = MaterialTheme.customShapes.defaultButton,
                ) {
                    Icon(
                        modifier = modifier.padding(end = MaterialTheme.spacing.grid1),
                        imageVector = Icons.Outlined.Replay,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(R.string.button_retake),
                        style = MaterialTheme.textStyle.ubuntuLabel,
                    )
                }

                // Submit button
                TextButton(
                    onClick = {
                        isLoading = true
                        onSubmit(imageUri)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                    ),
                    shape = MaterialTheme.customShapes.defaultButton,
                ) {
                    Icon(
                        modifier = modifier.padding(end = MaterialTheme.spacing.grid1),
                        imageVector = Icons.Outlined.Done,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(R.string.button_select),
                        style = MaterialTheme.textStyle.ubuntuLabel,
                    )
                }
            }
        }

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
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PhotoPreviewScreenPreview() {
    ComposeBaseTheme {
        PhotoPreviewScreen(imageUri = Uri.EMPTY, onClearImage = {}, onSubmit = {}) {}
    }
}