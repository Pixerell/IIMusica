package com.example.iimusica.player

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.iimusica.R
import com.example.iimusica.player.notifications.NotificationUtils


@UnstableApi
class PlaybackService : MediaSessionService() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "musica_channel"
        const val ACTION_PLAY = "com.example.iimusica.PLAY"
        const val ACTION_PAUSE = "com.example.iimusica.PAUSE"
        const val ACTION_STOP = "com.example.iimusica.STOP"
        const val ACTION_REPLACE_MEDIA_ITEMS = "com.example.iimusica.REPLACE"
        const val ACTION_NO_MORE_TRACKS = "com.example.iimusica.NOTRACKS"
        const val ACTION_CONTINUE = "com.example.iimusica.CONTINUE"
    }

    private var notificationManager: PlayerNotificationManager? = null
    private val exoPlayer by lazy {
        ExoPlayer.Builder(this).build()
    }
    private val mediaSession by lazy {
        MediaSession.Builder(this, exoPlayer)
            .setId("IIMusicaSession")
            .build()
    }



    private var playbackController: PlaybackController? = null

    inner class LocalBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        // Set up an event listener for playback state changes
        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val path = mediaItem?.mediaId
                if (path != null) {
                    playbackController?.onTrackChange?.invoke(path)
                }
            }
        })
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    fun setPlaybackController(controller: PlaybackController) {
        playbackController = controller
    }

    fun getPlayer(): ExoPlayer = exoPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val path = intent?.getStringExtra("path")
        val shouldPlay = intent?.getBooleanExtra("shouldPlay", true) != false

        when (intent?.action) {

            ACTION_PLAY, null -> {
                if (path != null) {
                    exoPlayer.seekTo(0)
                    exoPlayer.stop()
                    exoPlayer.clearMediaItems()
                    val mediaItem = MediaItem.Builder().setUri(path).setMediaId(path).build()
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    if (shouldPlay) {
                        exoPlayer.play()
                    } else {
                        exoPlayer.playWhenReady = false
                    }
                }
                else {
                    Log.i("PlaybackService", "The path was null")
                }
            }

            ACTION_PAUSE -> exoPlayer.pause()
            ACTION_CONTINUE -> exoPlayer.play()
            ACTION_REPLACE_MEDIA_ITEMS -> {
                if (path != null) {
                    exoPlayer.clearMediaItems()
                    exoPlayer.setMediaItem(MediaItem.fromUri(path))
                    exoPlayer.prepare()
                }
            }

            ACTION_NO_MORE_TRACKS -> {
                exoPlayer.pause()
                exoPlayer.seekTo(0)
            }

            ACTION_STOP -> {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                stopSelf()
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        notificationManager?.setPlayer(null)
        mediaSession.release()
        exoPlayer.release()
        super.onDestroy()
    }

}
