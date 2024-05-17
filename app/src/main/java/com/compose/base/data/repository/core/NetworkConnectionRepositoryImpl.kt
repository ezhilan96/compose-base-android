package com.compose.base.data.repository.core

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

@Singleton
class NetworkConnectionRepositoryImpl @Inject constructor(
    private val networkStatusObserver: NetworkStatusObserver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : NetworkConnectionRepository {

    private val _connectionState: MutableStateFlow<ConnectionState> =
        MutableStateFlow(ConnectionState.Pending)
    override val connectionState: Flow<ConnectionState>
        get() = _connectionState

    init {
        observeConnectionChange()
        checkConnection()
    }

    override fun checkConnection() {
        CoroutineScope(ioDispatcher).launch {
            val newConnectionState = try {
                Socket().apply {
                    connect(InetSocketAddress(Constants.GOOGLE_DNS, 53), 3000)
                    close()
                }
                ConnectionState.Connected
            } catch (e: Exception) {
                e.printStackTrace()
                ConnectionState.Disconnected
            }
            _connectionState.update { newConnectionState }
        }
    }

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