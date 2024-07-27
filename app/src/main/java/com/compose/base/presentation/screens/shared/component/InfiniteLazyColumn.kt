package com.compose.base.presentation.screens.shared.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.compose.base.presentation.util.reachedBottom

/**
 * Composable function for creating a LazyColumn with automatic loading of more items when the user reaches the bottom.
 *
 * This composable simplifies the creation of LazyColumns that automatically load more items as the
 * user scrolls down and reaches the end of the visible content. It provides a convenient way to
 * implement infinite scrolling behavior.
 *
 * - `modifier`: (Optional) A modifier to be applied to the LazyColumn.
 * - `lazyListState`: (Optional) A LazyListState object to control the scroll state of the list.
Defaults to a new instance created using `rememberLazyListState()`.
 * - `onLoadMore`: A callback function to be called when the user reaches the bottom of the list.
This function is responsible for fetching and adding more items to the data source.
 * - `content`: A lambda function that defines the content of the LazyColumn items.
This lambda receives a `LazyListScope` instance and allows you to define the layout for each item.
 *
 * This composable uses the following functionalities:
- `LazyColumn`: This provides the underlying list structure with scrolling functionality.
- `rememberLazyListState`: This creates a new LazyListState object to manage the scroll position
and visibility of items.
- `derivedStateOf`: This observes the `reachedBottom` property of the LazyListState and recomposes
the composable whenever the user reaches the bottom.
- `LaunchedEffect`: This triggers a side effect (calling `onLoadMore`) when the `reachedBottom` state
changes and the user reaches the bottom of the list.
 */
@Composable
fun InfiniteLazyColumn(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onLoadMore: () -> Unit,
    content: LazyListScope.() -> Unit,
) {
    val reachedBottom: Boolean by remember {
        derivedStateOf {
            lazyListState.reachedBottom()
        }
    }
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        content = content,
    )
    LaunchedEffect(reachedBottom) {
        if (reachedBottom) onLoadMore()
    }
}