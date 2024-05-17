package com.compose.base.domain.useCases.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NetworkStatus {
    Connected, Losing, Lost, Unknown
}

class NetworkStatusObserver @Inject constructor(@ApplicationContext context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun getNetworkStatusCallback(onConnectionChange: (NetworkStatus) -> Unit) =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                onConnectionChange(NetworkStatus.Connected)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                onConnectionChange(NetworkStatus.Unknown)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                onConnectionChange(NetworkStatus.Losing)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                onConnectionChange(NetworkStatus.Lost)
            }
        }

    fun getNetworkStatus(): Flow<NetworkStatus> = callbackFlow {

        val networkStatusCallback = getNetworkStatusCallback { launch { send(it) } }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkStatusCallback)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        awaitClose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.unregisterNetworkCallback(networkStatusCallback)
            }
        }
    }.distinctUntilChanged()
}