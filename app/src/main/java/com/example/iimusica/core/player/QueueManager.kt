package com.example.iimusica.core.player

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.types.DEFAULT_QUEUE_NAME
import com.example.iimusica.types.MusicFile
import com.example.iimusica.types.QUEUE_DIRECTION_FORWARD
import com.example.iimusica.types.QueuedMusicFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class QueueManager(
    private val _isShuffleEnabled: MutableState<Boolean>,
    private val _currentPath: MutableState<String?>,
    private var _repeatMode: MutableState<Int>,
    private val onRepeatModeChanged: (Int) -> Unit
) {

    private val queue = mutableListOf<QueuedMusicFile>()
    private val _currentIndex: MutableState<Int> = mutableIntStateOf(0)
    private var nextQueueId = 0L
    private val _currentQueueId = mutableStateOf<Long?>(null)

    private val shuffleOrder = mutableListOf<Int>()
    private var shuffledIndex = 0
    private var queueSearchDirection = QUEUE_DIRECTION_FORWARD

    val queueName: MutableState<String> =
        mutableStateOf("NoQueue")// This changes when playing albums/playlists etc.


    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
        if (_isShuffleEnabled.value) {
            if (shuffleOrder.size != queue.size) {
                regenerateShuffleOrder()
            } else {
                val currentQueueId = _currentPath.value?.let { path ->
                    queue.find { it.musicFile.path == path }?.queueId
                }
                shuffledIndex = currentQueueId?.let { getShuffledIndexByQueueId(it) } ?: 0
            }
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

    fun regenerateShuffleOrder() {
        CoroutineScope(Dispatchers.Default).launch {
            shuffleOrder.clear()
            shuffleOrder.addAll(queue.indices.shuffled())
            val currentQueueId = _currentPath.value?.let { path ->
                queue.find { it.musicFile.path == path }?.queueId
            }
            shuffledIndex = currentQueueId?.let { getShuffledIndexByQueueId(it) } ?: 0
        }
    }

    private fun getShuffledIndexByQueueId(queueId: Long): Int? {
        return shuffleOrder.indexOfFirst { queue[it].queueId == queueId }.takeIf { it >= 0 }
    }


    fun clearQueue() {
        queue.clear()
        shuffleOrder.clear()
    }

    fun setQueue(newQueue: List<MusicFile>, newQueueName: String = "", startIndex: Int = 0) {
        if (newQueue.isEmpty()) {
            Log.d("queuemanager", "Empty queue brother")
            return
        }
        clearQueue()
        queue.addAll(createQueuedMusicFiles(newQueue))
        if (newQueueName.isNotEmpty()) {
            updateQueueName(newQueueName)
        }
        val currentTrackId = _currentPath.value?.let { findQueuedIndexByPath(it) } ?: startIndex
        _currentIndex.value = currentTrackId
        regenerateShuffleOrder()
    }


    fun removeFromQueue(songsToRemove: List<MusicFile>) {
        queue.removeAll { queuedMusic -> songsToRemove.any { it.path == queuedMusic.musicFile.path } }
        regenerateShuffleOrder()
    }

    fun addToQueue(songsToAdd: List<MusicFile>) {
        queue.addAll(createQueuedMusicFiles(songsToAdd))
        regenerateShuffleOrder()
    }


    fun resetQueue(defaultQueue: List<MusicFile>) {
        setQueue(defaultQueue, DEFAULT_QUEUE_NAME)
    }

    fun updateQueueName(newQueueName: String) {
        queueName.value = newQueueName
    }

    fun updateIndex(
        path: String,
        queue: List<QueuedMusicFile>,
        currentIndex: Int,
        modifier: Int = queueSearchDirection
    ): Int {
        val size = queue.size
        if (size == 0) return currentIndex

        for (i in 0 until size) {
            val index = (currentIndex + i * modifier + size) % size
            Log.d("queuemanage", "Counting index? $index")
            if (queue[index].musicFile.path == path) {
                return index
            }
        }
        return currentIndex
    }

    fun updateIndexes(path: String) {
        _currentIndex.value = updateIndex(path, queue, _currentIndex.value, queueSearchDirection)
        shuffledIndex =
            queue.find { it.musicFile.path == path }?.queueId?.let { getShuffledIndexByQueueId(it) }
                ?: shuffledIndex
        val currentItem = queue.find { it.musicFile.path == path }
        _currentQueueId.value = currentItem?.queueId
    }

    fun getQueueWithIDs(): List<QueuedMusicFile> = queue.toList()
    fun getQueue(): List<MusicFile> = queue.map { it.musicFile }
    fun getShuffledViewWithIDs(): List<QueuedMusicFile> = shuffleOrder.map { queue[it] }

    fun getCurrentIndex(): Int = _currentIndex.value
    fun setCurrentIndex(ind: Int) {
        _currentIndex.value = ind
    }

    fun getShuffledIndex(): Int = shuffledIndex
    fun setShuffledIndex(ind: Int) {
        shuffledIndex = ind
    }

    fun getNextTrack(searchDirection: Int): Pair<List<MusicFile>, Int> {
        queueSearchDirection = searchDirection
        return if (_isShuffleEnabled.value) {
            val shuffledQueue = shuffleOrder.map { queue[it].musicFile }
            shuffledQueue to shuffledIndex
        } else {
            queue.map { it.musicFile } to _currentIndex.value
        }
    }

    fun findIndexByPath(path: String): Int {
        return queue.indexOfFirst { it.musicFile.path == path }.takeIf { it >= 0 } ?: -1
    }

    fun findQueuedIndexByPath(path: String): Int =
        queue.indexOfFirst { it.musicFile.path == path }


    private fun createQueuedMusicFiles(musicFiles: List<MusicFile>): List<QueuedMusicFile> {
        return musicFiles.map { musicFile ->
            QueuedMusicFile(musicFile, nextQueueId++)
        }
    }
}