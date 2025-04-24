package com.example.iimusica.player

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.types.MusicFile

class QueueManager(
    private val _isShuffleEnabled: MutableState<Boolean>,
    private val _currentPath: MutableState<String?>,
    private var _repeatMode: MutableState<Int>,
    private val onRepeatModeChanged: (Int) -> Unit
) {

    private val queue = mutableListOf<MusicFile>()
    private var currentIndex = 0
    private val shuffleOrder = mutableListOf<Int>()
    private var shuffledIndex = 0

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value

        if (_isShuffleEnabled.value) {
            if (shuffleOrder.size != queue.size) {
                regenerateShuffleOrder()
            }
            shuffledIndex = getShuffledIndexByPath(_currentPath.value ?: "") ?: 0
        }
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            ExoPlayer.REPEAT_MODE_OFF -> ExoPlayer.REPEAT_MODE_ALL
            ExoPlayer.REPEAT_MODE_ALL -> ExoPlayer.REPEAT_MODE_ONE
            ExoPlayer.REPEAT_MODE_ONE -> ExoPlayer.REPEAT_MODE_OFF
            else -> ExoPlayer.REPEAT_MODE_OFF
        }
        onRepeatModeChanged(_repeatMode.value)
    }

    private fun regenerateShuffleOrder() {
        shuffleOrder.clear()
        shuffleOrder.addAll(queue.indices.shuffled())
    }

    private fun getShuffledIndexByPath(path: String): Int? {
        return shuffleOrder.indexOfFirst { queue[it].path == path }.takeIf { it >= 0 }
    }

    fun clearQueue() {
        queue.clear()
        shuffleOrder.clear()
    }

    @OptIn(UnstableApi::class)
    fun setQueue(newQueue: List<MusicFile>, startIndex: Int = 0) {
        if (newQueue.isEmpty()) {
            Log.d("queuemanager", "Empty queue brother")
            return
        } else if (newQueue != queue) {
            clearQueue()
            queue.addAll(newQueue)

            // If playing track exists in new queue - map index to it or set to 0 if it doesnt
            val currentTrack = _currentPath.value
            currentIndex = if (currentTrack != null) {
                newQueue.indexOfFirst { it.path == currentTrack }.takeIf { it >= 0 } ?: 0
            } else {
                startIndex
            }

            regenerateShuffleOrder()
            return
        }
        Log.d("queuemanager", "Queue already filled with the same items")
    }

    fun updateIndex(path: String, queue: List<MusicFile>, currentIndex: Int): Int {
        return queue.indexOfFirst { it.path == path }.takeIf { it >= 0 } ?: currentIndex
    }

    fun updateIndexes(path: String) {
        currentIndex = updateIndex(path, queue, currentIndex)
        shuffledIndex = getShuffledIndexByPath(path) ?: shuffledIndex
    }

    fun getQueue(): List<MusicFile> = queue.toList()
    fun getShuffledView(): List<MusicFile> {
        return shuffleOrder.map { queue[it] }
    }

    fun getCurrentIndex(): Int = currentIndex
    fun setCurrentIndex(ind: Int) {
        currentIndex = ind
    }

    fun getShuffledIndex(): Int = shuffledIndex
    fun setShuffledIndex(ind: Int) {
        shuffledIndex = ind
    }

    fun getNextTrack(): Pair<List<MusicFile>, Int> {
        return if (_isShuffleEnabled.value) {
            val shuffledQueue = shuffleOrder.map { queue[it] }
            shuffledQueue to shuffledIndex
        } else {
            queue to currentIndex
        }
    }

}