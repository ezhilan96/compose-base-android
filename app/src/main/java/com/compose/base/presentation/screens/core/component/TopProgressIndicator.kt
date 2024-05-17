package com.compose.base.presentation.screens.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.compose.base.presentation.config.spacing

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