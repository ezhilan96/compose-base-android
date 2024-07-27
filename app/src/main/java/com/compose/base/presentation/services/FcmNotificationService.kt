package com.compose.base.presentation.services

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.domain.useCases.user.UserActionsUseCase
import com.compose.base.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Service class responsible for handling Firebase Cloud Messaging (FCM) notifications.
 *
 * This service extends FirebaseMessagingService and is marked with @AndroidEntryPoint for dependency injection.
 * It injects the `UserActionsUseCase` to interact with user login status and update device token.
 */
@AndroidEntryPoint
class FcmNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var userActions: UserActionsUseCase

    /**
     * Lazy-initialized instance of NotificationManagerCompat for building and displaying notifications.
     */
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    /**
     * Called when a new FCM message is received on the device.
     *
     * This method checks if the app is in the background or foreground based on the `isAppInBackground` function.
     *  - If the app is in the background, it displays a notification using the `notify` function.
     *  - If the app is in the foreground, it sends a broadcast with an action (`Constants.KEY_REFRESH`) to potentially refresh the UI.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        if (isAppInBackground()) {
            notify(message)
        } else {
            sendBroadcast(Intent().setAction(Constants.KEY_REFRESH))
        }
        super.onMessageReceived(message)
    }

    /**
     * Called when a new FCM token is generated on the device.
     *
     * This method checks the user login status using the `userActions.loginStatusFlow` and updates the device
     * token with `userActions.updateDeviceToken` if the user is logged in. This ensures the user receives
     * notifications even after app restarts or token refreshes.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            if (userActions.loginStatusFlow.first()) {
//                userActions.updateDeviceToken(token)
            }
        }
    }

    /**
     * Creates and displays a notification based on the received FCM message data.
     *
     * This function requires permission to display notifications (granted during app installation).
     * It extracts notification details (title, text, type, booking type, id) from the message data.
     * It creates an Intent with notification details and a PendingIntent to launch the MainActivity
     * when the notification is tapped. Finally, it builds and displays the notification using NotificationManagerCompat.
     */
    @SuppressLint("MissingPermission")
    private fun notify(message: RemoteMessage) {
        val title = message.data[Constants.JSON_TITLE]
        val text = message.data[Constants.JSON_BODY]
        val type = message.data[Constants.JSON_TYPE]
        val bookingType = message.data[Constants.JSON_BOOKING_TYPE]
        val id = message.data[Constants.JSON_ID]
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.KEY_NOTIFICATION_TYPE, type)
        intent.putExtra(Constants.KEY_NOTIFICATION_BOOKING_TYPE, bookingType)
        intent.putExtra(Constants.KEY_NOTIFICATION_BOOKING_ID, id)
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notificationBuilder =
            NotificationCompat.Builder(this, getString(R.string.notification_channel_id_booking))
                .setContentTitle(title).setContentText(text).setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX).setAutoCancel(true)
                .setContentIntent(pendingIntent)
        notificationManager.notify(Constants.bookingNotificationId, notificationBuilder.build())
    }

    /**
     * Checks if the app is currently running in the background.
     *
     * This function iterates through running app processes and checks if the current app's process
     * is in the foreground based on the ActivityManager. It returns true if the app is in the background,
     * false otherwise.
     */
    private fun isAppInBackground(): Boolean {
        val mActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        mActivityManager.runningAppProcesses.forEach {
            if (it.uid == applicationInfo.uid && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return false
            }
        }
        return true
    }
}