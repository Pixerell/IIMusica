package com.example.iimusica.player


import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.example.iimusica.notification.buildPlaybackNotification
import com.example.iimusica.player.notifications.createNotificationChannel


@UnstableApi
class PlaybackService : MediaLibraryService() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "musica_channel"
        const val CHANNEL_NAME = "IIMusica"
        const val CHANNEL_DESCRIPTION = "Main Music Player Channel"
        const val ACTION_PLAY = "com.example.iimusica.PLAY"
        const val ACTION_PAUSE = "com.example.iimusica.PAUSE"
        const val ACTION_STOP = "com.example.iimusica.STOP"
        const val ACTION_REPLACE_MEDIA_ITEMS = "com.example.iimusica.REPLACE"
        const val ACTION_NO_MORE_TRACKS = "com.example.iimusica.NOTRACKS"
        const val ACTION_CONTINUE = "com.example.iimusica.CONTINUE"
    }

    private val exoPlayer by lazy {
        ExoPlayer.Builder(this).build()
    }


    private lateinit var mediaLibrarySession: MediaLibrarySession

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
        createNotificationChannel(this, CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION)

        mediaLibrarySession = MediaLibrarySession.Builder(this, exoPlayer, librarySessionCallback)
            .setId("IIMusicaSession")
            .build()

        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val path = mediaItem?.mediaId
                if (path != null) {
                    showNotification(mediaItem)
                    playbackController?.onTrackChange?.invoke(path)
                }
            }
        })
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    fun setPlaybackController(controller: PlaybackController) {
        playbackController = controller
    }

    fun getPlayer(): ExoPlayer = exoPlayer

    private val librarySessionCallback = object : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val defaultResult = super.onConnect(session, controller)
            val availableSessionCommands = defaultResult.availableSessionCommands
                .buildUpon()
            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                defaultResult.availablePlayerCommands
            )
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val path = intent?.getStringExtra("path")
        val shouldPlay = intent?.getBooleanExtra("shouldPlay", true) != false

        when (intent?.action) {

            ACTION_PLAY, null -> {
                if (path != null) {
                    exoPlayer.seekTo(0)
                    exoPlayer.clearMediaItems()
                    exoPlayer.setMediaItem(buildMediaItem(path))
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
                    exoPlayer.setMediaItem(buildMediaItem(path))
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

    private fun buildMediaItem(path: String) =
        MediaItem.Builder().setUri(path).setMediaId(path).build()


    override fun onDestroy() {
        mediaLibrarySession.release()
        exoPlayer.release()
        super.onDestroy()
    }

    private fun showNotification(mediaItem: MediaItem) {
        val notification = buildPlaybackNotification(this, mediaItem, mediaLibrarySession, CHANNEL_ID)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }
}
