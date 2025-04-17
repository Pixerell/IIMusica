package com.example.iimusica.player.notifications


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.example.iimusica.player.PlaybackService.Companion.CHANNEL_ID
import com.example.iimusica.player.PlaybackService.Companion.NOTIFICATION_ID


@UnstableApi
object NotificationUtils {
    private var isChannelInitialized = false
    private fun initNotificationChannel(context: Context) {
        if (!isChannelInitialized) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Music Playback", NotificationManager.IMPORTANCE_LOW
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            isChannelInitialized = true
        }
    }

    @OptIn(UnstableApi::class)
    fun showPlaybackNotification(
        context: Context,
    ): PlayerNotificationManager {
        initNotificationChannel(context)

        return PlayerNotificationFactory.create(
            context,
            NOTIFICATION_ID,
            CHANNEL_ID,
        )
    }

    fun cancelNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }


}
