package com.compose.base.presentation.screens.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_ANY
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Composable function to remember the current lifecycle event of a LifecycleOwner.
 *
 * This composable retrieves and remembers the current lifecycle state (event) of the provided
 * `lifecycleOwner`. It defaults to using the `LocalLifecycleOwner.current` if no `lifecycleOwner`
 * is specified.
 *
 * - `lifecycleOwner`: (Optional) A LifecycleOwner object whose lifecycle state is to be tracked.
 * Defaults to the current `LocalLifecycleOwner`.
 *
 * This composable uses `remember` to store the current lifecycle event state. It also employs
 * `DisposableEffect` to add and remove a `LifecycleEventObserver` from the `lifecycleOwner`.
 * The observer updates the `currentLifecycleEvent` state whenever the lifecycle state changes.
 *
 * The function returns the current remembered lifecycle event.
 */
@Composable
fun rememberLifecycleEvent(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current): Lifecycle.Event {
    var currentLifecycleEvent by remember { mutableStateOf(ON_ANY) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            currentLifecycleEvent = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return currentLifecycleEvent
}