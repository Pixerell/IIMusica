package com.example.iimusica.utils


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.R
import com.example.iimusica.utils.fetchers.getAlbumArtBitmap
import com.example.iimusica.utils.fetchers.getMusicFileFromPath
import kotlin.jvm.java

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

    @OptIn(UnstableApi::class)
    fun showPlaybackNotification(
        context: Context,
        isPlaying: Boolean,
        path : String
    ) {


        val musicFile = getMusicFileFromPath(context, path)

        val title = musicFile?.name ?: "Unknown Title"
        val artist = musicFile?.artist ?: "Unknown Artist"
        val albumArt = musicFile?.let{ getAlbumArtBitmap(context, it) }

        init(context)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val sendMessageIntent  = Intent(context, NotificationBroadcastReceiver::class.java)
        sendMessageIntent.action = "SEND_MESSAGE"
        val pendingIntent = PendingIntent.getBroadcast(context, 0, sendMessageIntent , PendingIntent.FLAG_MUTABLE)

        val playPauseIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        val playPausePendingIntent = PendingIntent.getBroadcast(
            context, 1, playPauseIntent, PendingIntent.FLAG_MUTABLE
        )


        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.default_image)
            .setContentTitle(title)
            .setContentText(artist)
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_foreground, "Log Message", pendingIntent)
            .addAction(R.drawable.pauseico, "PLAY/PAUSE", playPausePendingIntent)

            .build()

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
