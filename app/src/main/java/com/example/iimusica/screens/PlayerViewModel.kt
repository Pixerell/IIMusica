package com.example.iimusica.screens


import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.media3.exoplayer.ExoPlayer

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> get() = _isPlaying

    private val _isShuffleEnabled = mutableStateOf(false)
    val isShuffleEnabled: State<Boolean> get() = _isShuffleEnabled

    private val _currentPath = mutableStateOf<String?>(null)
    val currentPath: State<String?> = _currentPath

    private val _repeatMode = mutableIntStateOf(ExoPlayer.REPEAT_MODE_OFF)
    val repeatMode: State<Int> get() = _repeatMode

    init {
        PlaybackController.init(
            application,
            _isPlaying,
            _currentPath,
            _repeatMode
        )
        PlaybackController.setQueueUpdateCallback { path ->
            queueManager.updateIndexes(path)
        }
    }

    val queueManager = QueueManager(
        _isShuffleEnabled = _isShuffleEnabled,
        _currentPath = _currentPath,
        _repeatMode = _repeatMode,
        exoPlayer = PlaybackController.getExoPlayer()
    )


    fun playMusic(path: String, shouldPlay : Boolean = true ) {
        PlaybackController.playMusic(path, shouldPlay)
    }

    fun playNext() {
        val (queuer, indexer) = queueManager.getNextTrack()
        PlaybackController.playNext(queuer, indexer)
    }

    fun playPrevious() {
        val (queuer, indexer) = queueManager.getNextTrack()
        PlaybackController.playPrevious(queuer, indexer)
    }


    fun togglePlayPause() {
        PlaybackController.togglePlayPause()
    }

    fun stopPlay() {
        PlaybackController.stopPlay()
    }

    fun toggleShuffle() {
        queueManager.toggleShuffle()
    }

    fun toggleRepeat() {
        queueManager.toggleRepeat()
    }


    fun setCurrentPath(path: String, isReplacing : Boolean) {
        if (isReplacing) {
            PlaybackController.replaceMediaItems(path)
        }
        _currentPath.value = path
    }


    override fun onCleared() {
        super.onCleared()
        // We don't release the player here as it is managed across multiple screens
    }


}