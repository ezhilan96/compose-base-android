package com.compose.base.presentation.screens.core.component

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
        FloatingActionButton(
            onClick = {
                localScope.launch {
                    lazyListState.animateScrollToItem(0)
                }
            }
        ) {
            Icon(imageVector = Icons.Rounded.KeyboardArrowUp, contentDescription = null)
        }
    }
}