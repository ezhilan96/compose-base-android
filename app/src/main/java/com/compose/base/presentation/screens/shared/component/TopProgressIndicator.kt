package com.compose.base.presentation.screens.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.compose.base.presentation.config.spacing

/**
 * Composable function for displaying a top linear progress indicator in light theme.
 *
 * This composable provides a way to display a linear progress indicator at the top of the screen
 * using Material Design light theme colors. The indicator is only visible when `isLoading` is
 * `true`.
 *
 * - `modifier`: (Optional) A modifier to be applied to the progress indicator.
 * - `isLoading`: A boolean indicating whether the indicator should be visible.
 *
 * This composable uses conditional logic to display either a `LinearProgressIndicator` when
 * `isLoading` is `true` or a `Spacer` with the same height and background color as the surface
 * when `isLoading` is `false`.
 */
@Composable
fun TopProgressIndicatorLight(modifier: Modifier = Modifier, isLoading: Boolean) {
    if (isLoading) {
        LinearProgressIndicator(
            modifier = modifier
                .height(MaterialTheme.spacing.unit2),
            trackColor = MaterialTheme.colorScheme.surface,
        )
    } else {
        Spacer(
            modifier = modifier
                .height(MaterialTheme.spacing.unit2)
                .background(color = MaterialTheme.colorScheme.surface),
        )
    }
}

/**
 * Composable function (same as [TopProgressIndicatorLight]) for displaying a top linear progress indicator in dark theme.
 *
 * This composable likely functions similarly to `TopProgressIndicatorLight` but uses Material Design
 * dark theme colors for the progress indicator track.
 *
 * - `modifier`: (Optional) A modifier to be applied to the progress indicator.
 * - `isLoading`: A boolean indicating whether the indicator should be visible.
 */
@Composable
fun TopProgressIndicatorDark(modifier: Modifier = Modifier, isLoading: Boolean) {
    if (isLoading) {
        LinearProgressIndicator(
            modifier = modifier
                .height(MaterialTheme.spacing.unit2),
            trackColor = MaterialTheme.colorScheme.inverseSurface,
        )
    } else {
        Spacer(
            modifier = modifier
                .height(MaterialTheme.spacing.unit2)
                .background(color = MaterialTheme.colorScheme.inverseSurface),
        )
    }
}