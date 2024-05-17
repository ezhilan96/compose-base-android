package com.compose.base.presentation.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.domain.repository.HomeRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class LocationTrackingStatus { Active, InActive }

@SuppressLint("MissingPermission")
@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var driverRepo: HomeRepository

    private lateinit var locationClient: FusedLocationProviderClient
    private var serviceStatus: LocationTrackingStatus = LocationTrackingStatus.InActive

    private val locationCallBack = object : LocationCallback() {
        @SuppressLint("HardwareIds")
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.locations.lastOrNull()?.let { location ->
                driverRepo.updateLocation(
                    location = location,
                    deviceId = Settings.Secure.getString(
                        contentResolver,
                        Settings.Secure.ANDROID_ID,
                    ),
                )
            }
        }
    }

    private val locationFrequencyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isFrequent = intent.getBooleanExtra(IS_FREQUENT, false)
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                if (isFrequent) 1000 else 10000,
            ).build()
            locationClient.removeLocationUpdates(locationCallBack)
            locationClient.requestLocationUpdates(
                locationRequest, locationCallBack, Looper.getMainLooper()
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        ContextCompat.registerReceiver(
            this,
            locationFrequencyReceiver,
            IntentFilter(ACTION_UPDATE_LOCATION_FREQUENCY),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> if (serviceStatus != LocationTrackingStatus.Active) {
                startForeground()
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    10000,
                ).build()
                locationClient.removeLocationUpdates(locationCallBack)
                locationClient.requestLocationUpdates(
                    locationRequest, locationCallBack, Looper.getMainLooper()
                )
                serviceStatus = LocationTrackingStatus.Active
            }

            ACTION_STOP -> if (serviceStatus != LocationTrackingStatus.InActive) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                } else {
                    stopForeground(true)
                }
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startForeground() {
        try {
            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
                this, getString(R.string.notification_channel_id_alpha)
            ).setContentTitle(getString(R.string.notification_channel_name_alpha))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSilent(true)
            val notification = notificationBuilder.build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceCompat.startForeground(
                    this,
                    Constants.ALPHA_NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION,
                )
            } else {
                startForeground(Constants.ALPHA_NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClient.removeLocationUpdates(locationCallBack)
        driverRepo.onLocationUpdateStop()
        serviceStatus = LocationTrackingStatus.InActive
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_UPDATE_LOCATION_FREQUENCY = "ACTION_UPDATE_LOCATION_FREQUENCY"
        const val IS_FREQUENT = "IS_FREQUENT"
        private var isEnabled = false
        fun start(context: Context) {
            isEnabled = true
            val intent = Intent(context, LocationTrackingService::class.java)
            intent.action = ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
        }

        fun updateLocationUpdateFrequency(context: Context, isFrequent: Boolean) {
            val intent = Intent().setAction(ACTION_UPDATE_LOCATION_FREQUENCY)
                .putExtra(IS_FREQUENT, isFrequent)
            context.sendBroadcast(intent)
        }

        fun stop(context: Context) {
            if (isEnabled) {
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