package com.compose.base.presentation.screens.shared.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

/**
 * Composable function for displaying a button that scrolls the content to the top.
 *
 * This composable provides a convenient way to add a button that allows users to scroll a
 * `LazyList` back to the top position. The button only appears when the list content is scrolled
 * down (i.e., there are items above the currently visible area).
 *
 * - `lazyListState`: A `LazyListState` object representing the state of the `LazyList` to be scrolled.
 *
 * This composable utilizes the following functionalities:
 * - `rememberCoroutineScope`: This creates a new coroutine scope for launching asynchronous scrolling animation.
 * - `derivedStateOf`: This observes the `firstVisibleItemIndex` of the `lazyListState` and determines if the button should be enabled (visible).
 * - `FloatingActionButton`: This component provides the visual representation of the scroll-to-top button.
 * - `LaunchedEffect`: This is not used in this specific implementation, but could be considered for more complex behaviors (e.g., animating the button appearance).
 */
@Composable
fun ScrollToTopButton(
    lazyListState: LazyListState
) {
    val localScope = rememberCoroutineScope()
    val enabled: Boolean by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }
    if (enabled) {
        FloatingActionButton(onClick = {
            localScope.launch {
                lazyListState.animateScrollToItem(0)
            }
        }) {
            Icon(imageVector = Icons.Rounded.KeyboardArrowUp, contentDescription = null)
        }
    }
}