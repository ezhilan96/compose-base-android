@file:OptIn(ExperimentalMaterial3Api::class)

package com.compose.base.presentation.screens.shared.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.base.R
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing

/**
 * Composable function for displaying a screen indicating no internet connection.
 *
 * This composable provides a user-friendly way to inform users when an internet connection
 * is unavailable. It displays an icon, message, and a retry button.
 *
 * - `modifier`: (Optional) A modifier to be applied to the entire screen.
 * - `onRetryConnection`: A callback function to be executed when the user clicks the retry button.
 *
 * This composable utilizes the following functionalities:
 * - `Scaffold`: Provides the base structure for the screen with specific colors.
 * - `TopAppBar`: Sets a transparent top app bar with an empty title.
 * - `containerColor`: Uses the inverse surface color from Material Theme for the background.
 * - `Column`: Arranges the content of the screen vertically.
 * - `Icon`: Displays an icon representing "no internet" retrieved from a drawable resource.
 * - `Text`: Displays a styled message using `AnnotatedString` builder to combine different fonts, sizes, and colors for visual hierarchy.
 * - `Row`: Positions the retry button horizontally in the center of the screen.
 * - `Button`: Creates the retry button with Material Design styling and calls `onRetryConnection` on click.
 */
@Composable
fun NoInternetScreen(
    modifier: Modifier = Modifier,
    onRetryConnection: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseSurface,
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // No internet icon
            Icon(
                modifier = modifier
                    .padding(start = MaterialTheme.spacing.grid6)
                    .size(200.dp)
                    .aspectRatio(1f),
                imageVector = Icons.Filled.CloudOff,
                contentDescription = null,
//                tint = Color.Unspecified
            )

            // Message
            Text(
                modifier = modifier.padding(horizontal = MaterialTheme.spacing.grid6),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                            fontSize = 22.sp,
                        )
                    ) {
                        append(
                            "Oops!\n"
                        )
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                            fontSize = 22.sp,

                            )
                    ) {
                        append("Unable to reach you!\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                            fontSize = 16.sp,
                            color = MaterialTheme.customColors.textLight
                        )
                    ) {
                        append("\nCheck your internet connection to get back in network.")
                    }
                })

            // Retry button
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.grid4),
                horizontalArrangement = Arrangement.Center
            ) {

                Button(
                    onClick = onRetryConnection,
                    shape = MaterialTheme.customShapes.defaultButton,
                ) {
                    Text(
                        text = stringResource(R.string.button_try_again),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
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
fun NoInternetPreview() {
    ComposeBaseTheme {
        NoInternetScreen {

        }
    }
}