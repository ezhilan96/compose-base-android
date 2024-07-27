package com.compose.base.presentation.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.domain.useCases.user.UserActionsUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Service class responsible for tracking user location and updating it to the server.
 *
 * This service is annotated with `@SuppressLint("MissingPermission")` as it requires location permission.
 * It's also marked with `@AndroidEntryPoint` for dependency injection.
 * The service injects the `UserActionsUseCase` to update user location and interacts with the local broadcast manager.
 */
@SuppressLint("MissingPermission")
@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var userActions: UserActionsUseCase

    /**
     * Lazy-initialized instance of FusedLocationProviderClient for requesting location updates.
     */
    private val locationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Callback class for receiving location updates from the FusedLocationProviderClient.
     *
     * This callback overrides `onLocationResult` to update the user's location with the latest received location.
     */
    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
//            userActions.updateLocation(locationResult.lastLocation)
        }
    }

    /**
     * Broadcast receiver for handling location update frequency changes.
     *
     * This receiver listens for the `ACTION_UPDATE_LOCATION_FREQUENCY` broadcast and updates the location
     * update interval based on the received `isFrequent` flag.
     */
    private val locationFrequencyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isFrequent = intent.getBooleanExtra(IS_FREQUENT, false)
            startLocationUpdates(if (isFrequent) 1000 else 10000)
        }
    }

    /**
     * Lazy-initialized instance of LocalBroadcastManager for sending and receiving location update frequency changes.
     */
    private val localBroadcastManager: LocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    /**
     * Service lifecycle methods (onBind, onCreate, onStartCommand, onDestroy)
     */
    override fun onBind(intent: Intent?): IBinder? = null // Service doesn't bind to activities

    override fun onCreate() {
        super.onCreate()
        localBroadcastManager.registerReceiver(
            locationFrequencyReceiver,
            IntentFilter(ACTION_UPDATE_LOCATION_FREQUENCY),
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf() // Stop the service when receiving the ACTION_STOP intent
            }

            ACTION_START -> {
                startForeground() // Start the service in the foreground
                startLocationUpdates(interval = 10000) // Start location updates (initial interval)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClient.removeLocationUpdates(locationCallBack)
//        userActions.stopLocationUpdate()
        localBroadcastManager.unregisterReceiver(locationFrequencyReceiver)
    }

    /**
     * Helper method to start the service in the foreground with a notification.
     */
    private fun startForeground() {

        // Create a notification channel for the foreground service if running on Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val locationTrackingChannel = NotificationChannel(
                getString(R.string.notification_channel_id_live_tracking),
                getString(R.string.notification_channel_name_live_tracking),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(locationTrackingChannel)
        }

        // Build the foreground service notification
        val notificationBuilder = NotificationCompat.Builder(
            this, getString(R.string.notification_channel_id_live_tracking)
        ).setContentTitle(getString(R.string.notification_channel_name_live_tracking))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setSilent(true)
        val notification = notificationBuilder.build()

        // Start the service in the foreground with the notification with ForegroundServiceType if running on Android 13 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                Constants.locationNotificationId,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                } else {
                    0
                },
            )
        } else {
            startForeground(Constants.locationNotificationId, notification)
        }
    }

    /**
     * Helper method to start location updates with the specified interval.
     *
     * @param interval The time interval in milliseconds between location updates.
     */
    private fun startLocationUpdates(interval: Long) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            interval,
        ).build()
        locationClient.removeLocationUpdates(locationCallBack)
        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.myLooper() ?: Looper.getMainLooper(),
        )
    }

    /**
     * Companion object containing constants and methods related to the LocationTrackingService.
     */
    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_UPDATE_LOCATION_FREQUENCY = "ACTION_UPDATE_LOCATION_FREQUENCY"
        const val IS_FREQUENT = "IS_FREQUENT"
        private var isActive = false

        /**
         * Starts the LocationTrackingService if it's not already active.
         */
        fun start(context: Context) {
            if (!isActive) {
                isActive = true
                try {
                    val intent = Intent(context, LocationTrackingService::class.java)
                    intent.action = ACTION_START
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(context, intent)
                    } else {
                        context.startService(intent)
                    }
                } catch (e: Exception) {
                    Firebase.crashlytics.recordException(e)
                }
            }
        }

        /**
         * Updates the location update frequency of the service.
         */
        fun updateLocationUpdateFrequency(context: Context, isFrequent: Boolean) {
            val intent = Intent().setAction(ACTION_UPDATE_LOCATION_FREQUENCY)
                .putExtra(IS_FREQUENT, isFrequent)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        /**
         * Stops the LocationTrackingService if it's active.
         */
        fun stop(context: Context) {
            if (isActive) {
                isActive = false
                val intent = Intent(context, LocationTrackingService::class.java)
                intent.action = ACTION_STOP
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
            }
        }
    }
}