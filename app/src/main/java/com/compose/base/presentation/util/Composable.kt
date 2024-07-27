package com.compose.base.presentation.util

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Enables or disables user gestures for a composable element.
 *
 * This extension function provides a convenient way to control user interaction with a composable based on a boolean flag.
 *
 * @receiver The Modifier object applied to the composable.
 * @param isEnabled A flag indicating whether gestures should be enabled (true) or disabled (false).
 * @return A new Modifier object with gesture handling applied.
 */
@SuppressLint("ReturnFromAwaitPointerEventScope")
fun Modifier.enableGesture(isEnabled: Boolean) = if (isEnabled) this else {
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach(
                    // Consume all pointer events to disable all touch events
                    PointerInputChange::consume
                )
            }
        }
    }
}

/**
 * Checks if the user has scrolled to the bottom of a LazyList composable, considering a buffer.
 *
 * This extension function helps determine when the user has reached near the end of a LazyList,
 * taking an optional buffer into account to avoid triggering actions right at the last item.
 *
 * @receiver The LazyListState object associated with the list.
 * @param buffer (Optional) The number of items to consider as a buffer before the actual end. Defaults to 1.
 * @return True if the user has scrolled close to the bottom of the list (considering the buffer), false otherwise.
 */
internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    return lastVisibleItemIndex != 0 && lastVisibleItemIndex == layoutInfo.totalItemsCount - buffer
}