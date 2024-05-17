package com.compose.base.presentation.screens.core.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.compose.base.R
import com.compose.base.presentation.config.textStyle

@Composable
fun DefaultAlert(
    modifier: Modifier = Modifier,
    message: String,
    dismissButtonLabel: String = "",
    onDismiss: () -> Unit,
    acceptButtonLabel: String = stringResource(R.string.button_ok),
    onAccept: () -> Unit = onDismiss,
) {
    AlertDialog(
        modifier = modifier,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        onDismissRequest = onDismiss,
        text = { Text(text = message, style = MaterialTheme.textStyle.emptyListTitle) },
        confirmButton = {
            if (acceptButtonLabel.isNotEmpty()) {
                TextButton(onClick = onAccept) {
                    Text(text = acceptButtonLabel)
                }
            }

        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissButtonLabel)
            }
        },
    )
}