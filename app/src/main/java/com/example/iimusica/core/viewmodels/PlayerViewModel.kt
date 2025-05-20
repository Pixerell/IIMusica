package com.example.iimusica.core.viewmodels


import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.core.player.PlaybackCommandBus
import com.example.iimusica.core.player.PlaybackController
import com.example.iimusica.core.player.QueueManager
import com.example.iimusica.types.MusicFile
import com.example.iimusica.types.SKIP_CHECK_CODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@UnstableApi
class PlayerViewModel(application: Application, val playbackController: PlaybackController) :
    AndroidViewModel(application) {

    private val _isShuffleEnabled = mutableStateOf(false)
    val isShuffleEnabled: State<Boolean> get() = _isShuffleEnabled

    private val _currentPath = mutableStateOf<String?>(null)
    private val _repeatMode = mutableIntStateOf(ExoPlayer.REPEAT_MODE_OFF)

    val isPlaying: Boolean get() = playbackController.isPlaying.value
    val currentPath: State<String?> get() = _currentPath
    val repeatMode: State<Int> get() = _repeatMode

    private val _currentCollectionID = MutableStateFlow(SKIP_CHECK_CODE)
    val currentCollectionID: StateFlow<Long> get() = _currentCollectionID

    init {
        playbackController.pathState = _currentPath
        playbackController.repeatModeState = _repeatMode

        playbackController.setQueueUpdateCallback { path ->
            queueManager.updateIndexes(path)
        }

        // Launch coroutine to collect playback commands
        observePlaybackCommands()
    }

    private fun observePlaybackCommands() {
        viewModelScope.launch {
            PlaybackCommandBus.commands.collectLatest { cmd ->
                handlePlaybackCommand(cmd)
            }
        }
    }

    private fun handlePlaybackCommand(cmd: String) {
        when (cmd) {
            PlaybackCommandBus.BUS_NEXT -> playNext()
            PlaybackCommandBus.BUS_PREV -> playPrevious()
            PlaybackCommandBus.BUS_TOGGLE_REPEAT -> toggleRepeat()
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

    fun playMusicAt(index: Int, path : String) {
        queueManager.setCurrentIndex(index)
        playbackController.playMusic(path, true)
    }


    fun playNext() {
        val (queuer, indexer) = queueManager.getNextTrack()
        Log.d("queuemanage", "check index $indexer")
        playbackController.playNext(queuer, indexer)
    }

    fun playPrevious() {
        val (queuer, indexer) = queueManager.getNextTrack()
        playbackController.playPrevious(queuer, indexer)
    }

    fun togglePlayPause() {
        playbackController.togglePlayPause()
    }

    fun pause() {
        playbackController.pause()
    }

    fun stopPlay() {
        playbackController.stopPlay()
        _isShuffleEnabled.value = false
        _currentPath.value = null
    }

    fun toggleShuffle() {
        queueManager.toggleShuffle()
    }

    fun toggleRepeat() {
        queueManager.toggleRepeat()
    }

    fun setCurrentPath(path: String, isReplacing: Boolean) {
        if (isReplacing || _currentPath.value == null) {
            playbackController.replaceMediaItems(path)
        }
        _currentPath.value = path
    }

    fun playCollection(collection: List<MusicFile>, collectionName: String, collectionId: Long) {
        _currentCollectionID.value = collectionId
        queueManager.setQueue(collection, collectionName)
        collection.firstOrNull()?.let {
            playMusic(it.path)
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackController.onDestroy()
    }

}