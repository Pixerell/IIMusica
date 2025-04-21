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
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.example.iimusica.player.notifications.CUSTOM_COMMAND_PLAY_PAUSE
import com.example.iimusica.player.notifications.CUSTOM_COMMAND_SKIP_NEXT
import com.example.iimusica.player.notifications.CUSTOM_COMMAND_SKIP_PREV
import com.example.iimusica.player.notifications.CUSTOM_COMMAND_TOGGLE_REPEAT
import com.example.iimusica.player.notifications.CustomNotificationCommand
import com.example.iimusica.player.notifications.CustomNotificationProvider
import com.example.iimusica.player.notifications.buildPlaybackNotification
import com.example.iimusica.player.notifications.createNotificationChannel
import com.example.iimusica.player.notifications.getPlayPauseCommandButton
import com.example.iimusica.player.notifications.getRepeatCommandButton
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
        const val ACTION_SKIP = "com.example.iimusica.SKIP"
        const val ACTION_NEXT = "com.example.iimusica.NEXT"
    }

    private val exoPlayer by lazy {
        ExoPlayer.Builder(this).build()
    }
    private lateinit var mediaLibrarySession: MediaLibrarySession
    private var playbackController: PlaybackController? = null
    private val customCommandButtons =
        CustomNotificationCommand.entries.map { it.button }
    private var isForegroundStarted: Boolean = false

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


        mediaLibrarySession.setCustomLayout(customCommandButtons)
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
            Log.d("notifz", "adding buttons $customCommandButtons")
            val allButtons = mutableListOf<CommandButton>().apply {
                addAll(customCommandButtons)  // Adds PREVIOUS and NEXT buttons from the enum
            }

            // Add dynamic buttons (Play/Pause, Repeat)
            val isPlaying = exoPlayer.isPlaying
            val repeatMode = exoPlayer.repeatMode

            // Add dynamic buttons to the list
            allButtons.apply {
                add(getPlayPauseCommandButton(isPlaying))
                add(getRepeatCommandButton(repeatMode))
            }

            mediaLibrarySession.setCustomLayout(allButtons)
            val defaultResult = super.onConnect(session, controller)
            val commands = defaultResult.availableSessionCommands.buildUpon()

            allButtons.forEach { cmd ->
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
                CUSTOM_COMMAND_SKIP_PREV -> exoPlayer.seekToPrevious()
                CUSTOM_COMMAND_SKIP_NEXT -> exoPlayer.seekToNext()
                CUSTOM_COMMAND_PLAY_PAUSE -> if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                }
                else {
                    exoPlayer.play()
                }
                CUSTOM_COMMAND_TOGGLE_REPEAT -> {
                    val newRepeatMode = when (exoPlayer.repeatMode) {
                        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                        Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                        else -> Player.REPEAT_MODE_OFF
                    }
                    exoPlayer.repeatMode = newRepeatMode
                }
            }
            updateCustomLayout()

            Log.d("notifz", "inside custom command")

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

                ACTION_NEXT -> exoPlayer.seekToNext()
                ACTION_SKIP -> exoPlayer.seekToPrevious()


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
        val repeatButton = getRepeatCommandButton(exoPlayer.repeatMode)
        val playPauseButton = getPlayPauseCommandButton(exoPlayer.isPlaying)

        mediaLibrarySession.setCustomLayout(
            listOf(
                repeatButton,
                CustomNotificationCommand.PREVIOUS.button,
                playPauseButton,
                CustomNotificationCommand.NEXT.button
            )
        )
    }
}
