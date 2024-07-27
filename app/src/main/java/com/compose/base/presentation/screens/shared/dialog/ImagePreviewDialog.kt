package com.compose.base.presentation.screens.shared.dialog

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.spacing

/**
 * Composable function for displaying a fullscreen image preview dialog.
 *
 * This composable provides a user-friendly way to present a full-screen image within a dialog.
 * It includes a loading indicator, close button, and click dismissal functionality.
 *
 * - `modifier`: (Optional) A modifier to be applied to the entire dialog.
 * - `imageUrl`: The URL of the image to be displayed.
 * - `onDismiss`: A callback function to be executed when the user dismisses the dialog.
 *
 * This composable utilizes `Scaffold` and various components to create the dialog structure:
 * - `Scaffold`: Provides a base for the dialog with a dark background.
 * - `Box`: Acts as the main container for the dialog content.
 * - `CircularProgressIndicator`: Displays a loading indicator while the image is being fetched.
 * - `Image`: Displays the downloaded image using `rememberAsyncImagePainter`.
 * - `IconButton`: Creates the close button in the top left corner.
 * - `Icon`: Defines the close button icon (back arrow).
 */
@Composable
fun ImagePreviewDialog(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onDismiss: () -> Unit,
) {
    Scaffold(containerColor = Color.Black.copy(alpha = .7f)) { safeAreaPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(safeAreaPadding)
                .clickable(onClick = onDismiss),
        ) {
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
            )
            Box(modifier = modifier.align(Alignment.Center)) {
                Image(
                    modifier = modifier
                        .clickable { }
                        .padding(MaterialTheme.spacing.grid2)
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null,
                )
            }
            IconButton(
                modifier = modifier
                    .padding(
                        horizontal = MaterialTheme.spacing.grid05,
                        vertical = MaterialTheme.spacing.grid1
                    )
                    .align(Alignment.TopStart),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(.25f),
                    contentColor = Color.White,
                ),
                onClick = onDismiss,
            ) {
                Icon(
                    modifier = modifier.padding(MaterialTheme.spacing.grid1),
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview
@Composable
fun ImagePreviewDialogPreview() {
    ComposeBaseTheme {
        ImagePreviewDialog(imageUrl = "") {}
    }
}