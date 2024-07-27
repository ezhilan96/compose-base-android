package com.compose.base.domain.repository.core

import com.compose.base.data.repository.core.ConnectionState
import kotlinx.coroutines.flow.Flow

/**
 * Interface for network connectivity-related operations.
 */
interface NetworkConnectionRepository {

    /**
     * Flow of `ConnectionState` objects representing the current network state
     * (e.g., connected, disconnected, wifi, mobile data).
     * Emits updates whenever the network connectivity changes.
     */
    val connectionState: Flow<ConnectionState>

    /**
     * Triggers a network connectivity check. Manually initiate a check or refresh the current state.
     */
    fun checkConnection()
}