package com.example.iimusica.utils.notifications

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.iimusica.R


@UnstableApi
class PlaybackService : MediaSessionService() {

    private lateinit var mediaSession: MediaSession
    private lateinit var notificationManager: PlayerNotificationManager
    private lateinit var exoPlayer: ExoPlayer

    // Binder
    inner class LocalBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return binder
    }

    fun getExoPlayer(): ExoPlayer = exoPlayer

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setId("IIMusicaSession")
            .build()

        notificationManager = NotificationUtils.showPlaybackNotification(this)
        notificationManager.setPlayer(exoPlayer)
        Log.d("notifz", "oncreation")

        val notification = NotificationCompat.Builder(this, NotificationUtils.CHANNEL_ID) // Use CHANNEL_ID from NotificationUtils
            .setContentTitle("Musica")
            .setContentText("Preparing playback...")
            .setSmallIcon(R.drawable.default_image)
            .build()

        startForeground(NotificationUtils.NOTIFICATION_ID, notification) // Use NOTIFICATION_ID from NotificationUtils


        // Set up an event listener for playback state changes
        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                Log.d("notifz", "Metadata changed: ${mediaMetadata.title}")
            }

        })
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        val action = intent?.action
        when (action) {
            ACTION_PLAY -> exoPlayer.play()
            ACTION_PAUSE -> exoPlayer.pause()
            ACTION_STOP -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        notificationManager.setPlayer(null)
        mediaSession.release()
       // exoPlayer.release()
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "musica_channel"
        const val ACTION_PLAY = "com.example.iimusica.PLAY"
        const val ACTION_PAUSE = "com.example.iimusica.PAUSE"
        const val ACTION_STOP = "com.example.iimusica.STOP"
    }
}
