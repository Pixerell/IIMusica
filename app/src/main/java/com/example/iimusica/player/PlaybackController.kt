package com.example.iimusica.player

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.compose.runtime.IntState
import androidx.compose.runtime.MutableState
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.types.MusicFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@UnstableApi
class PlaybackController(
    application: Application,
) {

    lateinit var isPlaying: MutableState<Boolean>
    lateinit var pathState: MutableState<String?>
    lateinit var repeatModeState: IntState

    private val tag = "notifz"
    private val appContext: Context = application.applicationContext

    private var boundService: PlaybackService? = null
    private var isBound = false

    private val _exoPlayerState = MutableStateFlow<ExoPlayer?>(null)
    val exoPlayerState: StateFlow<ExoPlayer?> = _exoPlayerState

    var exoPlayer: ExoPlayer?
        get() = _exoPlayerState.value
        set(value) {
            _exoPlayerState.value = value
        }

    val onPlayerReadyCallbacks = mutableListOf<(ExoPlayer) -> Unit>()

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

    @OptIn(UnstableApi::class)
    fun playMusic(path: String, shouldPlay: Boolean = true) {
        try {
            if (path.isEmpty()) {
                Log.e(tag, "playMusic called with an empty path.")
                return
            }

            val intent = Intent(appContext, PlaybackService::class.java)
            intent.putExtra("path", path)
            intent.putExtra("shouldPlay", shouldPlay)
            intent.action = PlaybackService.ACTION_PLAY
            appContext.startForegroundService(intent)

            isPlaying.value = true
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

    @OptIn(UnstableApi::class)
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
        val intent = Intent(appContext, PlaybackService::class.java)
        if (isPlaying.value) {
            intent.action = PlaybackService.ACTION_PAUSE
        } else {
            intent.action = PlaybackService.ACTION_CONTINUE
        }
        isPlaying.value = !isPlaying.value
        appContext.startService(intent)

    }

    fun stopPlay() {
        pathState.value = null
        isPlaying.value = false

        val intent = Intent(appContext, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_STOP
        }
        appContext.startService(intent)
    }


    fun replaceMediaItems(path: String) {
        val intent = Intent(appContext, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_REPLACE_MEDIA_ITEMS
            putExtra("path", path)
        }
        appContext.startService(intent)
    }

    fun noMoreTracks() {
        isPlaying.value = false

        val intent = Intent(appContext, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_NO_MORE_TRACKS
        }
        appContext.startService(intent)
    }

    var onTrackChange: ((String) -> Unit)? = null

    fun setQueueUpdateCallback(callback: (String) -> Unit) {
        onTrackChange = callback
    }

     fun onDestroy(){
        if (isBound) {
            appContext.unbindService(connection)
            isBound = false
            Log.i(tag, "Service unbound.")
        }
    }

}
