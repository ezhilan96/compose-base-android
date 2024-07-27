package com.compose.base.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.compose.base.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ComposeBaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Other notification channels are created before creating the notification.
        // But, since booking notifications are handled by FCM service, we need to create them on Applicaton onCreate.
        // This will be called every time the app is launched. but the channel is created only if it does not exist.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createBookingNotificationChannel()
        }
    }

    /**
     * Create Notification Channel and notification sound for Booking Updates notification (API Level 26+)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBookingNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Configure notification channel for booking updates
        val bookingChannel = NotificationChannel(
            getString(R.string.notification_channel_id_booking),
            getString(R.string.notification_channel_name_booking),
            NotificationManager.IMPORTANCE_HIGH,
        )

        // Configure notification sound for booking updates
        val bookingNotificationSound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.bookings)
        val attributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        bookingChannel.setSound(bookingNotificationSound, attributes)

        // Create the notification channel
        notificationManager.createNotificationChannel(bookingChannel)
    }
}