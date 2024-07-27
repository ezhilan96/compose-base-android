package com.compose.base.domain.useCases.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
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

/**
 * Class responsible for observing network connectivity changes and emitting the status as a Flow.
 *
 * This class is injected with the ApplicationContext to access system services. It utilizes
 * ConnectivityManager to register a NetworkCallback and observe network state changes.
 */
class NetworkStatusObserver @Inject constructor(@ApplicationContext context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Creates a NetworkCallback instance that calls the provided onConnectionChange function
     * with the corresponding NetworkStatus update.
     */
    private fun getNetworkStatusCallback(onConnectionChange: (NetworkStatus) -> Unit): ConnectivityManager.NetworkCallback =
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

    /**
     * Returns a Flow that emits the current network status and subsequent changes.
     *
     * This method utilizes callbackFlow to establish a bi-directional communication channel.
     * It creates a NetworkCallback and registers it with the ConnectivityManager. The callback
     * updates the Flow with the new NetworkStatus whenever a change occurs. The flow is also
     * configured to emit only distinct values (avoiding duplicates) using distinctUntilChanged.
     */
    fun getNetworkStatus(): Flow<NetworkStatus> = callbackFlow {

        // Create NetworkCallback and link it to the emitter
        val networkStatusCallback: ConnectivityManager.NetworkCallback =
            getNetworkStatusCallback { launch { send(it) } }

        try {
            // Register the callback based on API level
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkStatusCallback)
            }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }

        // Cleanup callback on cancellation
        awaitClose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.unregisterNetworkCallback(networkStatusCallback)
            }
        }
    }.distinctUntilChanged()
}