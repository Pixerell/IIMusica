package com.example.iimusica.screens


import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.player.PlaybackController
import com.example.iimusica.player.QueueManager

@androidx.media3.common.util.UnstableApi
class PlayerViewModel(application: Application, val playbackController: PlaybackController) :
    AndroidViewModel(application) {

    private val _isShuffleEnabled = mutableStateOf(false)
    val isShuffleEnabled: State<Boolean> get() = _isShuffleEnabled

    private val _isPlaying = mutableStateOf(false)
    private val _currentPath = mutableStateOf<String?>(null)
    private val _repeatMode = mutableIntStateOf(ExoPlayer.REPEAT_MODE_OFF)

    val isPlaying: State<Boolean> get() = _isPlaying
    val currentPath: State<String?> get() = _currentPath
    val repeatMode: State<Int> get() = _repeatMode

    init {
        playbackController.isPlaying = _isPlaying
        playbackController.pathState = _currentPath
        playbackController.repeatModeState = _repeatMode

        playbackController.setQueueUpdateCallback { path ->
            queueManager.updateIndexes(path)
        }
    }

    val queueManager = QueueManager(
        _isShuffleEnabled = _isShuffleEnabled,
        _currentPath = _currentPath,
        _repeatMode = _repeatMode,
        onRepeatModeChanged = { newMode ->
            playbackController.exoPlayer?.repeatMode = newMode
        })


    fun playMusic(path: String, shouldPlay: Boolean = true) {
        playbackController.playMusic(path, shouldPlay)
    }

    fun playNext() {
        val (queuer, indexer) = queueManager.getNextTrack()
        playbackController.playNext(queuer, indexer)
    }

    fun playPrevious() {
        val (queuer, indexer) = queueManager.getNextTrack()
        playbackController.playPrevious(queuer, indexer)
    }

    fun togglePlayPause() {
        playbackController.togglePlayPause()
    }

    fun stopPlay() {
        playbackController.stopPlay()
        _isPlaying.value = false
        _isShuffleEnabled.value = false
        _currentPath.value = ""
    }

    fun toggleShuffle() {
        queueManager.toggleShuffle()
    }

    fun toggleRepeat() {
        queueManager.toggleRepeat()
    }

    fun setCurrentPath(path: String, isReplacing: Boolean) {
        if (isReplacing) {
            playbackController.replaceMediaItems(path)
        }
        _currentPath.value = path
    }

    override fun onCleared() {
        super.onCleared()
        playbackController.onDestroy()
    }

}