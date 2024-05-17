package com.compose.base.presentation.screens.core.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing

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
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = stringResource(id = R.string.button_cancel),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview
@Composable
fun UploadOptionBottomSheetPreview() {
    ComposeBaseTheme {
        UploadOptionBottomSheet(onSelectCamera = {}, onSelectGallery = {}, onDismiss = {})
    }
}