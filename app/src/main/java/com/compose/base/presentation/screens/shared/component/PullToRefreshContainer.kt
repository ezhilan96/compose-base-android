@file:OptIn(ExperimentalMaterial3Api::class)

package com.compose.base.presentation.screens.shared.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import com.compose.base.presentation.config.ComposeBaseTheme

/**
 * Composable function for implementing a pull-to-refresh functionality.
 *
 * This composable provides a convenient way to add pull-to-refresh functionality to your screens
 * using Material Design 3's `PullToRefreshContainer` component.
 *
 * - `modifier`: (Optional) A modifier to be applied to the entire `PullToRefreshContainer`.
 * - `isLoading`: A boolean indicating whether a refresh is currently in progress.
When set to `true`, the refresh indicator will be displayed.
 * - `onRefresh`: A callback function to be executed when the user refreshes the content.
 * - `content`: A lambda function that defines the content to be displayed within the container.
This lambda receives a `BoxScope` instance.
 *
 * This composable utilizes the following functionalities:
- `Modifier.nestedScroll`: This modifier allows the `PullToRefreshContainer` to handle nested
scrolling within the content.
- `rememberPullToRefreshState`: This creates a new `PullToRefreshState` object to manage
the refresh state and interaction.
- `Box`: This serves as the root container for both the content and the `PullToRefreshContainer`.
- `androidx.compose.material3.pulltorefresh.PullToRefreshContainer`: This is the Material Design 3
component that provides the visual pull-to-refresh indicator and interaction handling.
- `LaunchedEffect`: This triggers side effects based on changes in `pullToRefreshState.isRefreshing`
and `isLoading`.
- When `pullToRefreshState.isRefreshing` becomes `true`, the `onRefresh` callback is triggered
to perform the refresh action.
- When `isLoading` becomes `false` (refresh completes), the `PullToRefreshState.endRefresh`
function is called to end the visual refresh indication.

 * This composable offers a well-structured and integrated approach to implementing pull-to-refresh
functionality with Material Design 3 aesthetics.
 */
@Composable
fun PullToRefreshContainer(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val localModifier = Modifier
    val pullToRefreshState = rememberPullToRefreshState()
    Box(modifier = modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        content()
        androidx.compose.material3.pulltorefresh.PullToRefreshContainer(
            modifier = localModifier
                .align(Alignment.TopCenter),
            state = pullToRefreshState,
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.scrim,
        )
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            onRefresh()
        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            pullToRefreshState.endRefresh()
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview
@Composable
private fun PullToRefreshContainerPreview() {
    ComposeBaseTheme {
        Scaffold { paddingValues ->
            PullToRefreshContainer(
                Modifier.padding(paddingValues),
                isLoading = true,
                onRefresh = { },
            ) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(100) {
                            Text(text = "$it")
                        }
                    }
                }
            }
        }
    }
}