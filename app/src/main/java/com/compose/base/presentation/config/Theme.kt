package com.compose.base.presentation.config

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Composable function that defines the Compose base application theme.
 *
 * This function provides a custom theme for the Compose base app based on the device
 * configuration. It leverages Jetpack Compose's `CompositionLocalProvider` and `MaterialTheme`
 * to set various theme aspects.
 *
 * @param content The composable content to be rendered within this theme.
 */
@Composable
fun ComposeBaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {

    // Access the current device configuration
    val configuration = LocalConfiguration.current

    // Define intended screen width for theme scaling
    val intendedScreenWidth = 360f

    // Calculate a screen size factor based on actual vs intended screen width
    val screenSizeFactor = configuration.screenWidthDp / intendedScreenWidth

    // Define custom color schemes based on dark theme preference
    val colorScheme = if (darkTheme) DarkColorScheme
    else LightColorScheme

    // Provide custom Spacing, TextStyles, and CustomShapes based on screen size factor
    CompositionLocalProvider(
        LocalSpacing provides Spacing(sizeFactor = screenSizeFactor),
        LocalTextStyle provides TextStyles(sizeFactor = screenSizeFactor),
        LocalCustomShapes provides CustomShapes(sizeFactor = screenSizeFactor),
        LocalCustomColors provides CustomColors(),
    ) {

        // Apply MaterialTheme with custom shapes, color scheme, and typography
        MaterialTheme(
            shapes = getDefaultShapes(sizeFactor = screenSizeFactor),
            colorScheme = colorScheme,
            typography = getTypography(sizeFactor = screenSizeFactor),
            content = content
        )
    }
}