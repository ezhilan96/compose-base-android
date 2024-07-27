package com.compose.base.presentation.screens.shared.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.compose.base.R
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing

/**
 * Composable function for displaying a bottom sheet with options for uploading content.
 *
 * This composable presents a bottom sheet with three buttons:
 * - Select from Gallery: Triggers the `onSelectGallery` callback when clicked.
 * - Take Photo: Triggers the `onSelectCamera` callback when clicked.
 * - Cancel: Triggers the `onDismiss` callback when clicked.
 *
 * @param modifier An optional modifier to be applied to the bottom sheet container.
 * @param onSelectGallery A callback function to be called when the user selects "Select from Gallery".
 * @param onSelectCamera A callback function to be called when the user selects "Take Photo".
 * @param onDismiss A callback function to be called when the user dismisses the bottom sheet (can be by clicking "Cancel" or tapping outside the sheet).
 */
@Composable
fun UploadOptionBottomSheet(
    modifier: Modifier = Modifier,
    onSelectGallery: () -> Unit,
    onSelectCamera: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = modifier.padding(MaterialTheme.spacing.grid3),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            modifier = modifier.fillMaxWidth(),
            onClick = onSelectGallery,
            shape = MaterialTheme.customShapes.defaultButton,
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = stringResource(R.string.button_select_from_gallery),
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider()
        TextButton(
            modifier = modifier.fillMaxWidth(),
            onClick = onSelectCamera,
            shape = MaterialTheme.customShapes.defaultButton,
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = stringResource(R.string.button_take_photo),
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider()
        TextButton(
            modifier = modifier.fillMaxWidth(),
            onClick = onDismiss,
            shape = MaterialTheme.customShapes.defaultButton,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.customColors.black),
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = stringResource(id = R.string.button_cancel),
                textAlign = TextAlign.Center,
                color = MaterialTheme.customColors.red,
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview
@Composable
fun UploadOptionBottomSheetPreview() {
    ComposeBaseTheme {
        UploadOptionBottomSheet(onSelectCamera = {}, onSelectGallery = {}, onDismiss = {})
    }
}