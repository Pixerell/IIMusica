package com.example.iimusica.player


import android.app.NotificationManager
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.example.iimusica.player.PlaybackCommandBus.BUS_NEXT
import com.example.iimusica.player.PlaybackCommandBus.BUS_PREV
import com.example.iimusica.player.PlaybackCommandBus.BUS_TOGGLE_REPEAT
import com.example.iimusica.player.notifications.CUSTOM_COMMAND_PLAY_PAUSE
import com.example.iimusica.player.notifications.CUSTOM_COMMAND_SKIP_NEXT
import com.example.iimusica.player.notifications.CUSTOM_COMMAND_SKIP_PREV
import com.example.iimusica.player.notifications.CUSTOM_COMMAND_TOGGLE_REPEAT
import com.example.iimusica.player.notifications.CustomNotificationProvider
import com.example.iimusica.player.notifications.buildPlaybackNotification
import com.example.iimusica.player.notifications.createNotificationChannel
import com.example.iimusica.player.notifications.getCustomActionButton
import com.example.iimusica.player.notifications.getIconResIdForAction
import com.example.iimusica.player.notifications.orderedCustomActions
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture


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
    private var isForegroundStarted: Boolean = false


    inner class LocalBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("notifz", "Binder intents? $intent")
        // Let MediaBrowserService (i.e. MediaLibraryService) handle its own binds...
        val superBinder = super.onBind(intent)
        // ...but when an app explicitly binds for your LocalBinder, give that instead.
        return if (intent?.action == SERVICE_INTERFACE) {
            superBinder
        } else {
            binder
        }
    }


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this, CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION)

        if (!::mediaLibrarySession.isInitialized) {
            mediaLibrarySession =
                MediaLibrarySession.Builder(this, exoPlayer, librarySessionCallback)
                    .setId("IIMusicaSession")
                    .build()
        }

        setMediaNotificationProvider(CustomNotificationProvider(this, mediaLibrarySession))

        Log.d("notifz", "media lib session $mediaLibrarySession")
        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val path = mediaItem?.mediaId
                if (path != null) {
                    playbackController?.onTrackChange?.invoke(path)
                    showNotification(mediaItem)
                }
            }
        })
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateCustomLayout()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                updateCustomLayout()
            }
        })


    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        Log.d("notifz", "onGetSession called")
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
            val customCommandButtons = orderedCustomActions.map { action ->
                getCustomActionButton(action, getIconResIdForAction(action, exoPlayer))
            }

            Log.d("notifz", "adding buttons $customCommandButtons")

            updateCustomLayout()
            val defaultResult = super.onConnect(session, controller)
            val commands = defaultResult.availableSessionCommands.buildUpon()

            customCommandButtons.forEach { cmd ->
                cmd.sessionCommand?.let(commands::add)
            }

            // This removes the default media3 button and default prevbutton
            val playerCommands = defaultResult.availablePlayerCommands.buildUpon()
                .remove(Player.COMMAND_PLAY_PAUSE)
                .remove(Player.COMMAND_SEEK_TO_PREVIOUS)
                .remove(Player.COMMAND_SEEK_BACK)
                .remove(Player.COMMAND_SEEK_TO_NEXT)
                .remove(Player.COMMAND_SEEK_FORWARD)
                .remove(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                .remove(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                .build()


            return MediaSession.ConnectionResult.accept(
                commands.build(),
                playerCommands
            )
        }

        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
            Log.d("notifz", "post connect")
            super.onPostConnect(session, controller)
            updateCustomLayout()

        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            when (customCommand.customAction) {
                CUSTOM_COMMAND_SKIP_PREV -> PlaybackCommandBus.sendCommand(BUS_PREV)
                CUSTOM_COMMAND_SKIP_NEXT -> PlaybackCommandBus.sendCommand(BUS_NEXT)
                CUSTOM_COMMAND_PLAY_PAUSE -> playbackController!!.togglePlayPause()
                CUSTOM_COMMAND_TOGGLE_REPEAT -> PlaybackCommandBus.sendCommand(BUS_TOGGLE_REPEAT)
            }
            updateCustomLayout()
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val path = intent?.getStringExtra("path")
        val shouldPlay = intent?.getBooleanExtra("shouldPlay", true) != false

        if (intent?.action == null) {
            return START_NOT_STICKY
        } else {
            Log.d("notifz", "onStartCommand received with action: ${intent.action}")
            when (intent.action) {
                ACTION_PLAY -> {
                    if (path != null) {
                        if (exoPlayer.currentMediaItem?.mediaId != path) {
                            exoPlayer.setMediaItem(buildMediaItem(path))
                            exoPlayer.prepare()
                        }
                        exoPlayer.seekTo(0)
                        exoPlayer.playWhenReady = shouldPlay

                    } else {
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
        val notification =
            buildPlaybackNotification(this, mediaItem, mediaLibrarySession, CHANNEL_ID)
                .build()
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (!isForegroundStarted) {
            startForeground(NOTIFICATION_ID, notification)
            isForegroundStarted = true
        } else {
            manager.notify(NOTIFICATION_ID, notification)
        }

    }


    private fun updateCustomLayout() {
        val customActionButtons = orderedCustomActions.map { action ->
            val icon = getIconResIdForAction(action, exoPlayer)
            getCustomActionButton(action, icon)
        }
        mediaLibrarySession.setCustomLayout(customActionButtons)
    }
}
