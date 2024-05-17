package com.compose.base.presentation.screens.core.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle

@Composable
fun OnStop(onStop: () -> Unit) {
    val currentLifecycleEvent = rememberLifecycleEvent()
    LaunchedEffect(currentLifecycleEvent) {
        if (currentLifecycleEvent == Lifecycle.Event.ON_STOP) {
            onStop()
        }
    }
}