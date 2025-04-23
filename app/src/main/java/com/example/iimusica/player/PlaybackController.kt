package com.example.iimusica.player

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.IntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.iimusica.types.MusicFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@UnstableApi
class PlaybackController(
    application: Application,
) {

    var pathState: MutableState<String?> = mutableStateOf(null)
    var repeatModeState: IntState = mutableIntStateOf(0)

    var exoPlayer: ExoPlayer?
        get() = _exoPlayerState.value
        set(value) {
            _exoPlayerState.value = value
        }
    private val _exoPlayerState = MutableStateFlow<ExoPlayer?>(null)
    val exoPlayerState: StateFlow<ExoPlayer?> = _exoPlayerState
    val onPlayerReadyCallbacks = mutableListOf<(ExoPlayer) -> Unit>()

    private val tag = "notifz"
    private val appContext: Context = application.applicationContext
    private var boundService: PlaybackService? = null
    private var isBound = false
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: MutableState<Boolean> get() = _isPlaying

    fun setPlayer(player: ExoPlayer) {
        exoPlayer = player
        onPlayerReadyCallbacks.forEach { it(player) }
        onPlayerReadyCallbacks.clear()
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlaybackService.LocalBinder
            boundService = binder.getService()
            boundService?.let { playbackService ->
                playbackService.setPlaybackController(this@PlaybackController)
                setPlayer(playbackService.getPlayer())
                // This controller setup is specifically for starting medialibrary service
                // Possible optimization?
                val sessionToken =
                    SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
                val controllerFuture =
                    MediaController.Builder(appContext, sessionToken).buildAsync()
                controllerFuture.addListener({
                    // Surround with try/catch to avoid blocking main thread
                    try {
                        controllerFuture.get().release()
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to release MediaController", e)
                    }
                }, ContextCompat.getMainExecutor(appContext))
            }
            isBound = true
            Log.i(tag, "Service connected and ExoPlayer bound.")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = null
            isBound = false
            Log.w(tag, "Service disconnected.")
        }
    }

    init {
        val intent = Intent(appContext, PlaybackService::class.java)
        isBound = appContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (isBound) {
            Log.i(tag, "Service bound on init")
        } else {
            Log.e(tag, "Failed to bind service on init")
        }
    }

    fun playMusic(path: String, shouldPlay: Boolean = true) {
        try {
            if (path.isEmpty()) {
                Log.e(tag, "playMusic called with an empty path.")
                return
            }
            sendAction(PlaybackService.ACTION_PLAY, foreground = true) {
                putExtra("path", path)
                putExtra("shouldPlay", shouldPlay)
            }
            _isPlaying.value = true
            pathState.value = path
            onTrackChange?.invoke(path)

        } catch (e: Exception) {
            Log.e(tag, "Error playing music: ${e.message}", e)
        }
    }

    fun playNext(queue: List<MusicFile>, index: Int) {
        val nextIndex = navigateToIndex(isNext = true, queue.size, index, repeatModeState.intValue)
        if (nextIndex != -1) {
            val path = queue[nextIndex].path
            playMusic(path)
        } else {
            noMoreTracks()
        }
    }

    fun playPrevious(queue: List<MusicFile>, index: Int) {
        val prevIndex = navigateToIndex(isNext = false, queue.size, index, repeatModeState.intValue)
        if (prevIndex != -1) {
            val path = queue[prevIndex].path
            playMusic(path)
        } else {
            noMoreTracks()
        }
    }

    fun togglePlayPause() {
        val action = if (isPlaying.value) {
            PlaybackService.ACTION_PAUSE
        } else {
            PlaybackService.ACTION_CONTINUE
        }
        _isPlaying.value = !_isPlaying.value
        sendAction(action)
    }

    fun stopPlay() {
        _isPlaying.value = false
        pathState.value = null
        sendAction(PlaybackService.ACTION_STOP)
    }


    fun replaceMediaItems(path: String) {
        sendAction(PlaybackService.ACTION_REPLACE_MEDIA_ITEMS) {
            putExtra("path", path)
        }
    }

    fun noMoreTracks() {
        _isPlaying.value = false
        sendAction(PlaybackService.ACTION_NO_MORE_TRACKS)
    }

    var onTrackChange: ((String) -> Unit)? = null

    fun setQueueUpdateCallback(callback: (String) -> Unit) {
        onTrackChange = callback
    }

    private fun sendAction(
        action: String,
        foreground: Boolean = false,
        extras: Intent.() -> Unit = {}
    ) {
        val intent = Intent(appContext, PlaybackService::class.java).apply {
            this.action = action
            extras()
        }
        if (foreground) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }
    }

    fun onDestroy() {
        if (isBound) {
            appContext.unbindService(connection)
            isBound = false
            Log.i(tag, "Service unbound.")
        }
    }

}
