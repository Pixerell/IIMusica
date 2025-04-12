package com.example.iimusica.utils


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.iimusica.R

object NotificationUtils {
    private const val CHANNEL_ID = "music_status_channel"
    private const val NOTIFICATION_ID = 1
    private var isChannelInitialized = false

    fun init(context: Context) {
        if (!isChannelInitialized) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            isChannelInitialized = true
        }
    }

    fun showPlaybackNotification(context: Context, isPlaying: Boolean) {

        init(context)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val statusText = if (isPlaying) "Music is playing" else "Playback paused"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Musica")
            .setContentText(statusText)
            .setOngoing(isPlaying) // Make notification sticky if playing
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
