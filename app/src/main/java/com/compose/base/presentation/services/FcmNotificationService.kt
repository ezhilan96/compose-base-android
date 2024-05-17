package com.compose.base.presentation.services

import android.Manifest
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.data.dataSource.local.dataStore.UserPreferencesDataStore
import com.compose.base.domain.useCases.core.SubmitDeviceDataUseCase
import com.compose.base.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FcmNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var updateToken: SubmitDeviceDataUseCase

    @Inject
    lateinit var dataStore: UserPreferencesDataStore

    override fun onMessageReceived(message: RemoteMessage) {
        if (isAppInBackground()) notify(message)
        else sendBroadcast(Intent().setAction(Constants.KEY_REFRESH))
        super.onMessageReceived(message)
    }

    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    private fun notify(message: RemoteMessage) {
        val title = message.data[Constants.JSON_TITLE]
        val text = message.data[Constants.JSON_BODY]
        val type = message.data[Constants.JSON_TYPE]
        val id = message.data[Constants.JSON_ID]
        val bookingNotificationSound =
            Uri.parse("android.resource://" + this.packageName + "/" + R.raw.bookings)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.KEY_NOTIFICATION_ID, id)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        val notificationBuilder =
            NotificationCompat.Builder(this, getString(R.string.notification_channel_id_beta))
                .setPriority(NotificationCompat.PRIORITY_MAX).setSound(bookingNotificationSound)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(text)
                .setAutoCancel(true)
        notificationBuilder.setContentIntent(pendingIntent)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(Constants.BETA_NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun isAppInBackground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.runningAppProcesses.forEach {
            if (it.uid == applicationInfo.uid && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return false
            }
        }
        return true
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            if (dataStore.isLoggedIn.first()) {
                updateToken(token)
            }
        }
    }
}