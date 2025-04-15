package com.example.iimusica.screens

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.compose.runtime.IntState
import androidx.compose.runtime.MutableState
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.types.MusicFile
import com.example.iimusica.utils.notifications.PlaybackService

@UnstableApi
object PlaybackController {

    private const val TAG = "notifz"

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var isPlayingState: MutableState<Boolean>
    private lateinit var currentPath: MutableState<String?>
    private lateinit var repeatMode: IntState
    private lateinit var appContext: Context

    private var boundService: PlaybackService? = null
    private var isBound = false


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlaybackService.LocalBinder
            boundService = binder.getService()
            exoPlayer = boundService!!.getExoPlayer()
            isBound = true
            Log.i(TAG, "Service connected and ExoPlayer bound.")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = null
            exoPlayer.release()
            isBound = false
            Log.w(TAG, "Service disconnected.")
        }
    }

    fun init(
        application: Application,
        isPlaying: MutableState<Boolean>,
        pathState: MutableState<String?>,
        repeatModeState: IntState,
    ) {
        appContext = application.applicationContext
        isPlayingState = isPlaying
        currentPath = pathState
        repeatMode = repeatModeState
        exoPlayer = ExoPlayer.Builder(application).build()

    }

    fun getExoPlayer(): ExoPlayer = exoPlayer

    @OptIn(UnstableApi::class)
    fun playMusic(path: String, shouldPlay: Boolean = true) {
        try {
            if (path.isEmpty()) {
                Log.e(TAG, "playMusic called with an empty path.")
                return
            }
            val intent = Intent(appContext, PlaybackService::class.java)
            appContext.startForegroundService(intent)

            if (!isBound) {
                appContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }


            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.setMediaItem(MediaItem.fromUri(path))
            exoPlayer.prepare()

            if (shouldPlay) {
                exoPlayer.play()
            } else {
                exoPlayer.playWhenReady = false
            }
            onTrackChange?.invoke(path)
            isPlayingState.value = shouldPlay
            currentPath.value = path


            if (isBound) {
                boundService?.getExoPlayer()?.play()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error playing music: ${e.message}", e)
        }
    }

    fun playNext(queue: List<MusicFile>, index: Int) {
        val nextIndex = navigateToIndex(isNext = true, queue.size, index, repeatMode.intValue)
        if (nextIndex != -1) {
            val path = queue[nextIndex].path
            playMusic(path)
        } else {
            noMoreTracks()
        }
    }

    @OptIn(UnstableApi::class)
    fun playPrevious(queue: List<MusicFile>, index: Int) {
        val prevIndex = navigateToIndex(isNext = false, queue.size, index, repeatMode.intValue)
        if (prevIndex != -1) {
            val path = queue[prevIndex].path
            playMusic(path)
        } else {
            noMoreTracks()
        }
    }


    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            isPlayingState.value = false
        } else {
            exoPlayer.play()
            isPlayingState.value = true
        }
    }

    fun stopPlay() {
        currentPath.value = null
        isPlayingState.value = false
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }

    fun replaceMediaItems(path: String) {
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItem(MediaItem.fromUri(path))
    }

    fun noMoreTracks() {
        isPlayingState.value = false
        exoPlayer.pause()
        exoPlayer.seekTo(0)
    }

    private var onTrackChange: ((String) -> Unit)? = null

    fun setQueueUpdateCallback(callback: (String) -> Unit) {
        onTrackChange = callback
    }

}
