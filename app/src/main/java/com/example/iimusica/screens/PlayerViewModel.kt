package com.example.iimusica.screens


import android.app.Application
import androidx.annotation.OptIn
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import com.example.iimusica.utils.MusicFile
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.media3.common.util.UnstableApi

class PlayerViewModel(application: Application) : AndroidViewModel(application) {


    val exoPlayer: ExoPlayer = ExoPlayer.Builder(application.applicationContext).build()

    private val queue = mutableListOf<MusicFile>()
    private var currentIndex = 0
    private val shuffledQueue = mutableListOf<MusicFile>()
    private var shuffledIndex = 0

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> get() = _isPlaying

    private val _isShuffleEnabled = mutableStateOf(false)
    val isShuffleEnabled: State<Boolean> get() = _isShuffleEnabled

    private val _repeatMode = mutableIntStateOf(ExoPlayer.REPEAT_MODE_OFF)
    val repeatMode: State<Int> get() = _repeatMode

    private val _currentPath = mutableStateOf<String?>(null)
    val currentPath: State<String?> = _currentPath



    fun playMusic(path: String) {
        // Update currentIndex if the song is in the queue
        currentIndex = queue.indexOfFirst { it.path == path }.takeIf { it >= 0 } ?: currentIndex

        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        exoPlayer.setMediaItem(MediaItem.fromUri(path))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        exoPlayer.play()

        _isPlaying.value = true
        _currentPath.value = path

       // _songDuration.value = newDuration
    }


    @OptIn(UnstableApi::class)
    fun playNext() {
        when (_repeatMode.intValue) {
            ExoPlayer.REPEAT_MODE_ONE -> {
                playMusic(_currentPath.value ?: "")
            }
            ExoPlayer.REPEAT_MODE_ALL -> {
                if (_isShuffleEnabled.value) {
                    shuffledIndex = (shuffledIndex + 1) % shuffledQueue.size
                    playMusic(shuffledQueue[shuffledIndex].path)
                } else {
                    currentIndex = (currentIndex + 1) % queue.size
                    playMusic(queue[currentIndex].path)
                }
            }
            ExoPlayer.REPEAT_MODE_OFF -> {
                if (_isShuffleEnabled.value) {
                    if (shuffledIndex < shuffledQueue.size - 1) {
                        shuffledIndex++
                        playMusic(shuffledQueue[shuffledIndex].path)
                    } else {
                        endOfQueue()
                    }
                } else if (currentIndex < queue.size - 1) {
                    currentIndex++
                    playMusic(queue[currentIndex].path)
                } else {
                    endOfQueue()
                }
            }
        }
    }

    fun playPrevious() {
        when (_repeatMode.intValue) {
            ExoPlayer.REPEAT_MODE_ONE -> {
                playMusic(_currentPath.value ?: "")
            }
            ExoPlayer.REPEAT_MODE_ALL -> {
                if (_isShuffleEnabled.value) {
                    shuffledIndex = if (shuffledIndex > 0) shuffledIndex - 1 else shuffledQueue.size - 1
                    playMusic(shuffledQueue[shuffledIndex].path)
                } else {
                    currentIndex = if (currentIndex > 0) currentIndex - 1 else queue.size - 1
                    playMusic(queue[currentIndex].path)
                }
            }
            ExoPlayer.REPEAT_MODE_OFF -> {
                if (_isShuffleEnabled.value) {
                    if (shuffledIndex > 0) {
                        shuffledIndex--
                        playMusic(shuffledQueue[shuffledIndex].path)
                    } else {
                        shuffledIndex = shuffledQueue.size - 1
                        playMusic(shuffledQueue[shuffledIndex].path)
                    }
                } else if (currentIndex > 0) {
                    currentIndex--
                    playMusic(queue[currentIndex].path)
                } else {
                    playMusic(_currentPath.value ?: "")
                }
            }
        }
    }

    

    fun endOfQueue() {
        _isPlaying.value = false
        exoPlayer.pause()
        exoPlayer.seekTo(0)
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _isPlaying.value = false
        } else {
            exoPlayer.play()
            _isPlaying.value = true
        }
    }

    fun stopPlay() {
        setCurrentPath("")
        setCurrentIndex(0)
        shuffledIndex = 0
        clearQueue()
        _isPlaying.value = false
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.release()
    }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value

        if (_isShuffleEnabled.value) {
            if (shuffledQueue.isEmpty() || shuffledQueue.size != queue.size ||
                !shuffledQueue.map { it.path }.containsAll(queue.map { it.path })) {
                shuffledQueue.clear()
                shuffledQueue.addAll(queue.shuffled())
            }
            shuffledIndex = 0
        }
    }

    fun toggleRepeat() {
        _repeatMode.intValue = when (_repeatMode.intValue) {
            ExoPlayer.REPEAT_MODE_OFF -> ExoPlayer.REPEAT_MODE_ALL
            ExoPlayer.REPEAT_MODE_ALL -> ExoPlayer.REPEAT_MODE_ONE
            ExoPlayer.REPEAT_MODE_ONE -> ExoPlayer.REPEAT_MODE_OFF
            else -> ExoPlayer.REPEAT_MODE_OFF
        }

        exoPlayer.repeatMode = _repeatMode.intValue
    }



    /*
    fun addToQueue(musicFile: MusicFile) {
        queue.add(musicFile)
    }

    fun removeFromQueue(musicFile: MusicFile) {
        queue.remove(musicFile)
    }
    */
    fun clearQueue() {
        queue.clear()

    }

    fun setQueue(newQueue: List<MusicFile>, startIndex: Int = 0) {
        queue.clear()
        queue.addAll(newQueue)
        currentIndex = startIndex
    }

    fun setCurrentPath(path: String) {
        _currentPath.value = path
    }

    fun setCurrentIndex(ind : Int) {
        currentIndex = ind
    }

    fun setIsPlaying(bool : Boolean) {
        _isPlaying.value = bool
    }

    fun getQueue(): List<MusicFile> = queue.toList()
    // fun getCurrentIndex(): Int = currentIndex

    override fun onCleared() {
        super.onCleared()
        // We don't release the player here as it is managed across multiple screens
    }


}