package com.example.iimusica.screens


import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.player.PlaybackCommandBus
import com.example.iimusica.player.PlaybackController
import com.example.iimusica.player.QueueManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@androidx.media3.common.util.UnstableApi
class PlayerViewModel(application: Application, val playbackController: PlaybackController) :
    AndroidViewModel(application) {

    private val _isShuffleEnabled = mutableStateOf(false)
    val isShuffleEnabled: State<Boolean> get() = _isShuffleEnabled

    private val _currentPath = mutableStateOf<String?>(null)
    private val _repeatMode = mutableIntStateOf(ExoPlayer.REPEAT_MODE_OFF)

    val isPlaying: Boolean get() = playbackController.isPlaying.value

    val currentPath: State<String?> get() = _currentPath
    val repeatMode: State<Int> get() = _repeatMode

    init {
        playbackController.pathState = _currentPath
        playbackController.repeatModeState = _repeatMode

        playbackController.setQueueUpdateCallback { path ->
            queueManager.updateIndexes(path)
        }

        viewModelScope.launch {
            PlaybackCommandBus.commands.collectLatest { cmd ->
                when (cmd) {
                    PlaybackCommandBus.BUS_NEXT -> playNext()
                    PlaybackCommandBus.BUS_PREV -> playPrevious()
                    PlaybackCommandBus.BUS_TOGGLE_REPEAT -> toggleRepeat()
                }
            }
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