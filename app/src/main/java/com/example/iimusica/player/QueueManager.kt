package com.example.iimusica.player

import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.types.MusicFile

class QueueManager(
    private val _isShuffleEnabled: MutableState<Boolean>,
    private val _currentPath: MutableState<String?>,
    private var _repeatMode: MutableState<Int>,
    private val onRepeatModeChanged: (Int) -> Unit
) {

    //TODO Make a unified queue while keeping the shuffled order index mapping. No need for 2 queue's
    private val queue = mutableListOf<MusicFile>()
    private var currentIndex = 0
    private val shuffledQueue = mutableListOf<MusicFile>()
    private var shuffledIndex = 0

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value

        if (_isShuffleEnabled.value) {
            if (shuffledQueue.isEmpty() || shuffledQueue.size != queue.size || shuffledQueue.map { it.path }
                    .toSet() != queue.map { it.path }.toSet()) {
                shuffledQueue.clear()
                shuffledQueue.addAll(queue.shuffled())
            }
            shuffledIndex = updateIndex(_currentPath.value ?: "", shuffledQueue, 0)
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

    fun clearQueue() {
        queue.clear()
        shuffledQueue.clear()
    }

    @OptIn(UnstableApi::class)
    fun setQueue(newQueue: List<MusicFile>, startIndex: Int = 0) {
        if (newQueue.isEmpty()) {
            Log.d("queuemanager", "Empty queue brother")
            return
        } else if (newQueue != queue) {
            clearQueue()
            queue.addAll(newQueue)
            currentIndex = startIndex
            shuffledQueue.addAll(newQueue.shuffled())
        }
    }

    fun updateIndex(path: String, queue: List<MusicFile>, currentIndex: Int): Int {
        return queue.indexOfFirst { it.path == path }.takeIf { it >= 0 } ?: currentIndex
    }

    fun updateIndexes(path: String) {
        currentIndex = updateIndex(path, queue, currentIndex)
        shuffledIndex = updateIndex(path, shuffledQueue, shuffledIndex)
    }

    fun getQueue(): List<MusicFile> = queue.toList()
    fun getShuffledQueue(): List<MusicFile> = shuffledQueue.toList()

    fun getCurrentIndex(): Int = currentIndex
    fun setCurrentIndex(ind: Int) {
        currentIndex = ind
    }

    fun getShuffledIndex(): Int = shuffledIndex
    fun setShuffledIndex(ind: Int) {
        shuffledIndex = ind
    }

    fun getNextTrack(): Pair<List<MusicFile>, Int> {
        var queuer = if (_isShuffleEnabled.value) getShuffledQueue() else getQueue()
        var indexer = if (_isShuffleEnabled.value) getShuffledIndex() else getCurrentIndex()
        return queuer to indexer
    }

}