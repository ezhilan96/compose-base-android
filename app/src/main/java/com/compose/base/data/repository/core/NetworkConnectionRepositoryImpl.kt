package com.compose.base.data.repository.core

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.compose.base.core.Constants
import com.compose.base.core.di.IoDispatcher
import com.compose.base.domain.repository.core.NetworkConnectionRepository
import com.compose.base.domain.useCases.core.NetworkStatus
import com.compose.base.domain.useCases.core.NetworkStatusObserver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

enum class ConnectionState { Pending, Connected, Disconnected }

/**
 * Implementation class for NetworkConnectionRepository responsible for monitoring and
 * reporting network connectivity status.
 *
 * This class injects dependencies for `NetworkStatusObserver` (interface for observing network status changes)
 * and an I/O dispatcher for coroutines. It maintains an internal mutable state flow (`_connectionState`)
 * to store the current network connection state and provides a public read-only flow (`connectionState`)
 * to expose the state to other parts of the application.
 */
@Singleton
class NetworkConnectionRepositoryImpl @Inject constructor(
    private val networkStatusObserver: NetworkStatusObserver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : NetworkConnectionRepository {

    /**
     * Internal mutable state flow representing the current network connection state.
     *
     * This state flow is updated with `ConnectionState` values (Connected, Disconnected, Pending).
     */
    private val _connectionState: MutableStateFlow<ConnectionState> =
        MutableStateFlow(ConnectionState.Pending)
    override val connectionState: Flow<ConnectionState>
        get() = _connectionState

    // Call checkConnection() to initialize the flow and start observing network status changes
    init {
        observeConnectionChange()
        checkConnection()
    }

    /**
     * Initiates a network check to determine the current connection state.
     *
     * This method attempts to connect to a well-known host (Google DNS) on a background
     * coroutine using the provided I/O dispatcher. Based on the success or failure of the
     * connection attempt, it updates the internal `_connectionState` with either
     * `ConnectionState.Connected` or `ConnectionState.Disconnected`. Any exceptions are
     * caught and logged using Firebase Crashlytics.
     */
    override fun checkConnection() {
        CoroutineScope(ioDispatcher).launch {
            val newConnectionState = try {
                Socket().apply {
                    connect(InetSocketAddress(Constants.GOOGLE_DNS, 53), 3000)
                    close()
                }
                ConnectionState.Connected
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
                ConnectionState.Disconnected
            }
            _connectionState.update { newConnectionState }
        }
    }

    /**
     * Launches a coroutine to observe network status changes from the injected
     * `NetworkStatusObserver`.
     *
     * This method collects updates from the `networkStatusObserver` on a background
     * coroutine using the provided I/O dispatcher. Based on the received `NetworkStatus`,
     * it updates the internal `_connectionState` accordingly:
     *  - Connected: Updates to `ConnectionState.Connected`.
     *  - Lost: Updates to `ConnectionState.Disconnected`.
     *  - Losing/Unknown: Triggers another `checkConnection` to obtain a more accurate state.
     */
    private fun observeConnectionChange() {
        CoroutineScope(ioDispatcher).launch {
            networkStatusObserver.getNetworkStatus().collect { networkStatus ->
                when (networkStatus) {
                    NetworkStatus.Connected -> _connectionState.update { ConnectionState.Connected }
                    NetworkStatus.Lost -> _connectionState.update { ConnectionState.Disconnected }
                    NetworkStatus.Losing, NetworkStatus.Unknown -> checkConnection()
                }
            }
        }
    }
}