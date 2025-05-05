package com.example.iimusica.player


import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
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
import java.io.File


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
    private val librarySessionCallback = MusicaSessionCallback()
    private var playbackController: PlaybackController? = null
    private var isForegroundStarted: Boolean = false
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Let MediaBrowserService (i.e. MediaLibraryService) handle its own binds...
        // ...but when an app explicitly binds for LocalBinder, give that instead.
        val superBinder = super.onBind(intent)
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
                    .setId("IIMusicaSession").build()
        }

        setMediaNotificationProvider(CustomNotificationProvider(this, mediaLibrarySession))
        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val path = mediaItem?.mediaId
                if (path != null) {
                    playbackController?.onTrackChange?.invoke(path)
                    showNotification(mediaItem)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateCustomLayout()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                updateCustomLayout()
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(
                    application.applicationContext,
                    "Playback error: ${error.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
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

    private inner class MusicaSessionCallback : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession, controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {

            val customCommandButtons = generateCustomActionButtons()
            updateCustomLayout()

            val defaultResult = super.onConnect(session, controller)
            val commands = defaultResult.availableSessionCommands.buildUpon()
            customCommandButtons.forEach { cmd ->
                cmd.sessionCommand?.let(commands::add)
            }

            // This removes the default media3 button and default prevbutton
            val playerCommands =
                defaultResult.availablePlayerCommands.buildUpon().remove(Player.COMMAND_PLAY_PAUSE)
                    .remove(Player.COMMAND_SEEK_TO_PREVIOUS).remove(Player.COMMAND_SEEK_BACK)
                    .remove(Player.COMMAND_SEEK_TO_NEXT).remove(Player.COMMAND_SEEK_FORWARD)
                    .remove(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .remove(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM).build()
            return MediaSession.ConnectionResult.accept(
                commands.build(), playerCommands
            )
        }

        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
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
                CUSTOM_COMMAND_PLAY_PAUSE -> requireNotNull(playbackController) {
                    "PlaybackController must be set before handling play/pause"
                }.togglePlayPause()

                CUSTOM_COMMAND_TOGGLE_REPEAT -> PlaybackCommandBus.sendCommand(BUS_TOGGLE_REPEAT)
            }
            updateCustomLayout()
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            handleIntentAction(intent)
        } ?: return START_NOT_STICKY
        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleIntentAction(intent: Intent) {
        val path = intent.getStringExtra("path")
        val shouldPlay = intent.getBooleanExtra("shouldPlay", true)

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

    private fun buildMediaItem(path: String) = MediaItem.Builder().apply {
        setUri(path)
        setMediaId(path)
    }.build()

    override fun onDestroy() {
        mediaLibrarySession.release()
        exoPlayer.release()
        super.onDestroy()
    }

    private fun startOrUpdateForeground(notification: Notification) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (!isForegroundStarted) {
            startForeground(NOTIFICATION_ID, notification)
            isForegroundStarted = true
        } else {
            manager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun showNotification(mediaItem: MediaItem) {
        val notification =
            buildPlaybackNotification(this, mediaItem, mediaLibrarySession, CHANNEL_ID).build()
        startOrUpdateForeground(notification)
    }

    private fun generateCustomActionButtons(): List<CommandButton> {
        return orderedCustomActions.map { action ->
            val icon = getIconResIdForAction(action, exoPlayer)
            getCustomActionButton(action, icon)
        }
    }

    private fun updateCustomLayout() {
        val customActionButtons = generateCustomActionButtons()
        mediaLibrarySession.setCustomLayout(customActionButtons)
    }
}
