package com.compose.base.presentation.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.enableGesture(isEnabled: Boolean) = if (isEnabled) this else {
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach(
                    PointerInputChange::consume
                )
            }
        }
    }
}

internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    return lastVisibleItemIndex != 0 && lastVisibleItemIndex == layoutInfo.totalItemsCount - buffer
}