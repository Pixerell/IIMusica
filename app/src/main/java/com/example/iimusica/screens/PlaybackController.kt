package com.example.iimusica.screens

import android.app.Application
import androidx.annotation.OptIn
import androidx.compose.runtime.IntState
import androidx.compose.runtime.MutableState
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.utils.MusicFile

object PlaybackController {

    private const val TAG = "PlaybackController"

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var isPlayingState: MutableState<Boolean>
    private lateinit var currentPath: MutableState<String?>
    private lateinit var repeatMode: IntState

    fun init(
        application: Application,
        isPlaying: MutableState<Boolean>,
        pathState: MutableState<String?>,
        repeatModeState: IntState
    ) {
        exoPlayer = ExoPlayer.Builder(application).build()
        isPlayingState = isPlaying
        currentPath = pathState
        repeatMode = repeatModeState
    }

    fun getExoPlayer(): ExoPlayer = exoPlayer

    @OptIn(UnstableApi::class)
    fun playMusic(path: String, shouldPlay: Boolean = true) {
        try {
            if (path.isEmpty()) {
                Log.e(TAG, "playMusic called with an empty path.")
                return
            }

            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.setMediaItem(MediaItem.fromUri(path))
            exoPlayer.prepare()

            if (shouldPlay) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            } else {
                exoPlayer.playWhenReady = false
            }
            onTrackChange?.invoke(path)
            isPlayingState.value = shouldPlay
            currentPath.value = path
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
    fun playPrevious(queue: List<MusicFile>, index : Int) {
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

    fun replaceMediaItems(path : String) {
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
