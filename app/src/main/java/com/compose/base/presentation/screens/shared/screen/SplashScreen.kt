package com.compose.base.presentation.screens.shared.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.compose.base.R
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.spacing

/**
 * Composable function for displaying a splash screen with logo and background color.
 *
 * This composable provides a basic implementation for a splash screen using Material Design
 * components. It displays a logo centered on the screen with a background color matching the
 * Material Theme's inverse surface color.
 *
 * - `modifier`: (Optional) A modifier to be applied to the entire splash screen.
 *
 * **Note:** The `@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")` annotation likely
 * suppresses a lint warning because the `Scaffold` padding parameter isn't explicitly used
 * in this composable's implementation.
 *
 * This composable utilizes the following functionalities:
 * - `Scaffold`: Provides the base structure for the splash screen, although its padding parameter might not be directly utilized here.
 * - `Box`: Acts as the main container for the splash screen content.
 * - `background`: Sets the background color of the container using the inverse surface color from Material Theme.
 * - `Image`: Displays the application logo centered on the screen.
 * - The logo is retrieved from a drawable resource using `painterResource`.
 * - The size of the logo is determined by `MaterialTheme.spacing.splashScreenLogoSize`.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Scaffold {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.background))
        ) {
            Image(
                modifier = modifier
                    .align(Alignment.Center)
                    .size(MaterialTheme.spacing.splashScreenLogoSize),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
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
fun SplashScreenPreview() {
    ComposeBaseTheme {
        SplashScreen()
    }
}