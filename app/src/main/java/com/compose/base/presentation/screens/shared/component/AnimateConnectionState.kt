package com.compose.base.presentation.screens.shared.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.compose.base.data.repository.core.ConnectionState

/**
 * [AnimateConnectionState] composable animates the appearance and disappearance of its content, as
 * [connectionState] value changes.
 *
 * @param modifier modifier for the [Box] created to contain the [connectedContent], [noInternetContent] & [pendingContent].
 * @param connectionState defines which content should be visible.
 * @param connectedContent Content to appear if value of [connectionState] is [ConnectionState.Connected].
 * @param noInternetContent Content to appear if value of [connectionState] is [ConnectionState.Disconnected].
 * @param pendingContent Content to appear if value of [connectionState] is [ConnectionState.Pending].
 */
@Composable
fun AnimateConnectionState(
    modifier: Modifier = Modifier,
    connectionState: ConnectionState,
    connectedContent: @Composable () -> Unit,
    noInternetContent: @Composable () -> Unit,
    pendingContent: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {

        AnimatedVisibility(
            visible = connectionState != ConnectionState.Pending,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            connectedContent()
            /**
             * Display no internet screen on top of the connected screen. so, that the side effects
             * in the [connectedContent] are not triggered
             */
            AnimatedVisibility(
                visible = connectionState == ConnectionState.Disconnected,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                noInternetContent()
            }
        }

        AnimatedVisibility(
            visible = connectionState == ConnectionState.Pending,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            pendingContent()
        }
    }
}