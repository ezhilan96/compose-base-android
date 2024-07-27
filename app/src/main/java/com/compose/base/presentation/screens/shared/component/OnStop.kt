package com.compose.base.presentation.screens.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle

/**
 * Composable function to execute a callback when the lifecycle state reaches ON_STOP.
 *
 * This composable simplifies the process of running code when the composable's lifecycle
 * transitions to the `ON_STOP` state. This state typically indicates that the composable is no longer
 * visible on the screen.
 *
 * - `onStop`: A callback function to be executed when the lifecycle state reaches `ON_STOP`.
 *
 * This composable utilizes `rememberLifecycleEvent` to track the current lifecycle event.
 * It then uses `LaunchedEffect` to trigger the `onStop` callback function only when the
 * `currentLifecycleEvent` becomes `Lifecycle.Event.ON_STOP`.
 */
@Composable
fun OnStop(onStop: () -> Unit) {
    val currentLifecycleEvent = rememberLifecycleEvent()
    LaunchedEffect(currentLifecycleEvent) {
        if (currentLifecycleEvent == Lifecycle.Event.ON_STOP) {
            onStop()
        }
    }
}