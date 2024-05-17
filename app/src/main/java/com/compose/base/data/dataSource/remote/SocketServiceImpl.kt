package com.compose.base.data.dataSource.remote

import android.content.Context
import com.compose.base.R
import com.compose.base.data.dataSource.local.dataStore.UserPreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

interface SocketService {

    fun isConnected(): Boolean

    fun connect()

    fun sendMessage(data: JSONObject)

    fun disconnect()
}

@Singleton
class SocketServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: UserPreferencesDataStore,
) : SocketService {

    private val eventLocation = "update-location"

    private var socket: Socket? = null
//    private val onConnect = Emitter.Listener { args ->
//        Log.i(TAG, "Socket EVENT_CONNECT: ${JSONObject.wrap(args)}")
//    }
//    private val onError = Emitter.Listener { args ->
//        Log.e(TAG, "Socket EVENT_CONNECT_ERROR: ${JSONObject.wrap(args)};")
//    }

    override fun isConnected(): Boolean = socket?.connected() ?: false

    override fun connect() {
        val token: String? = runBlocking {
            dataStore.userDetails.first()?.token
        }
        val options = IO.Options()
        options.extraHeaders = mapOf(Pair("token", listOf(token)))
        val baseUrl = context.getString(R.string.base_url)
        socket = IO.socket("$baseUrl/drivers", options)
//        socket.on(Socket.EVENT_CONNECT, onConnect,)
//        socket.on(Socket.EVENT_CONNECT_ERROR, onError)
        socket?.connect()
    }

    override fun sendMessage(data: JSONObject) {
        if (socket?.isActive != true) connect()
        socket?.emit(eventLocation, data)
    }

    override fun disconnect() {
        socket?.disconnect()
//        socket.off(Socket.EVENT_CONNECT, onConnect,)
//        socket.off(Socket.EVENT_CONNECT_ERROR, onError)
    }
}