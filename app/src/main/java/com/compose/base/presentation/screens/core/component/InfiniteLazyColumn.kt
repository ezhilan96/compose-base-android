package com.compose.base.presentation.screens.core.component

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