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
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "PlayerViewModel"
    }


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


    @OptIn(UnstableApi::class)
    fun playMusic(path: String) {
        try {
            if (path.isEmpty()) {
                Log.e(TAG, "playMusic called with an empty path.")
                return
            }
            // Update currentIndex if the song is in the queue
            currentIndex = updateIndex(path, queue, currentIndex)
            shuffledIndex = updateIndex(path, shuffledQueue, shuffledIndex)

            exoPlayer.stop()
            exoPlayer.clearMediaItems()

            exoPlayer.setMediaItem(MediaItem.fromUri(path))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            exoPlayer.play()

            _isPlaying.value = true
            _currentPath.value = path
        }
        catch (e: Exception) {
            Log.e(TAG, "Error playing music: ${e.message}", e)
        }

    }

    fun playNext() {
        val nextIndex = getNextIndex(isNext = true)
        if (nextIndex != -1) {
            val path = if (_isShuffleEnabled.value) shuffledQueue[nextIndex].path else queue[nextIndex].path
            playMusic(path)
        } else {
            endOfQueue() // Stop when reaching the end of the queue
        }
    }

    fun playPrevious() {
        val prevIndex = getNextIndex(isNext = false)
        if (prevIndex != -1) {
            val path = if (_isShuffleEnabled.value) shuffledQueue[prevIndex].path else queue[prevIndex].path
            playMusic(path)
        } else {
            endOfQueue() // Stop when reaching the beginning of the queue
        }
    }


    @OptIn(UnstableApi::class)
    private fun getNextIndex(isNext: Boolean): Int {
        val isShuffle = _isShuffleEnabled.value
        val currentQueue = if (isShuffle) shuffledQueue else queue
        var currentIdx = if (isShuffle) shuffledIndex else currentIndex

        if (currentQueue.isEmpty()) {
            Log.e(TAG, "Queue is empty.")
            return -1
        }

        val newIndex: Int = when (_repeatMode.intValue) {
            ExoPlayer.REPEAT_MODE_ONE -> currentIdx // Stay on the same track for REPEAT_MODE_ONE
            ExoPlayer.REPEAT_MODE_ALL -> {
                if (isNext) {
                    (currentIdx + 1) % currentQueue.size
                } else {
                    (currentIdx - 1 + currentQueue.size) % currentQueue.size
                }
            }
            ExoPlayer.REPEAT_MODE_OFF -> {
                if (isNext) {
                    if (currentIdx < currentQueue.size - 1) {
                        currentIdx + 1
                    } else {
                        return -1 // End of queue, no more tracks
                    }
                } else {
                    if (currentIdx > 0) {
                        currentIdx - 1
                    } else {
                        return -1 // Beginning of queue, no more tracks
                    }
                }
            }
            else -> -1 // Default case if mode is undefined
        }
        return  newIndex
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
        currentIndex = 0
        _currentPath.value = null
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
            if (shuffledQueue.isEmpty() || shuffledQueue.size != queue.size || shuffledQueue.map { it.path }.toSet() != queue.map { it.path }.toSet()) {
                shuffledQueue.clear()
                shuffledQueue.addAll(queue.shuffled())
            }
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

    fun endOfQueue() {
        _isPlaying.value = false
        exoPlayer.pause()
        exoPlayer.seekTo(0)
    }


    fun clearQueue() {
        queue.clear()

    }

    fun setQueue(newQueue: List<MusicFile>, startIndex: Int = 0) {
        clearQueue()
        queue.addAll(newQueue)
        currentIndex = startIndex

        if (_isShuffleEnabled.value) {
            shuffledQueue.clear()
            shuffledQueue.addAll(newQueue.shuffled())
        }

    }

    fun setCurrentPath(path: String) {
        _currentPath.value = path
    }

    fun setCurrentIndex(ind : Int) {
        currentIndex = ind
    }

    private fun updateIndex(path: String, queue: List<MusicFile>, currentIndex: Int): Int {
        return queue.indexOfFirst { it.path == path }.takeIf { it >= 0 } ?: currentIndex
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