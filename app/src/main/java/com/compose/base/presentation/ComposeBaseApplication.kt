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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val alphaChannel = getAlphaNotificationChannel()
        val betaChannel = getBetaNotificationChannel()
        notificationManager.createNotificationChannels(
            listOf(alphaChannel, betaChannel)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAlphaNotificationChannel() = NotificationChannel(
        getString(R.string.notification_channel_id_alpha),
        getString(R.string.notification_channel_name_alpha),
        NotificationManager.IMPORTANCE_DEFAULT,
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBetaNotificationChannel(): NotificationChannel {
        val betaChannel = NotificationChannel(
            getString(R.string.notification_channel_id_beta),
            getString(R.string.notification_channel_name_beta),
            NotificationManager.IMPORTANCE_HIGH,
        )
        val betaNotificationSound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.bookings)
        val attributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        betaChannel.setSound(betaNotificationSound, attributes)
        return betaChannel
    }
}