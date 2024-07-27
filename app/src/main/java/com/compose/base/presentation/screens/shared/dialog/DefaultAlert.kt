package com.compose.base.presentation.screens.shared.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.compose.base.R
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.textStyle

/**
 * Composable function for displaying a basic alert dialog with customizable options.
 *
 * This composable provides a convenient way to create a simple alert dialog with a message,
 * optional dismiss and accept buttons, and Material Design styling.
 *
 * - `modifier`: (Optional) A modifier to be applied to the entire `AlertDialog`.
 * - `message`: The text message to be displayed in the alert dialog body.
 * - `dismissButtonLabel`: (Optional) The text label for the dismiss button. Defaults to an empty string (no button).
 * - `onDismiss`: A callback function to be executed when the user dismisses the dialog (either by clicking a button or tapping outside).
 * - `acceptButtonLabel`: (Optional) The text label for the accept button. Defaults to the resource string `R.string.button_ok` (typically "OK").
 * - `onAccept`: (Optional) A callback function to be executed when the user clicks the accept button. Defaults to the same function as `onDismiss`.
 *
 * This composable utilizes the following functionalities:
 * - `AlertDialog`: This is the core component for building the alert dialog.
 * - `DialogProperties`: This configures default behavior for the dialog, such as dismissal on back press and clicking outside.
 * - `onDismissRequest`: This allows specifying a callback for handling dismiss requests.
 * - `Text`: This displays the message content of the alert dialog.
 * - `TextButton`: This creates the buttons for dismissal and acceptance (if provided).
 */
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
                    Text(
                        text = acceptButtonLabel,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissButtonLabel,
                    color = MaterialTheme.customColors.textDark,
                )
            }
        },
    )
}