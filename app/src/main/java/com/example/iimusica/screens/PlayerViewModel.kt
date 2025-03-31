package com.example.iimusica.screens


import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import com.example.iimusica.utils.MusicFile
import androidx.compose.runtime.State

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(application.applicationContext).build()
    private val queue = mutableListOf<MusicFile>()
    private var currentIndex = 0

    private val _currentPath = mutableStateOf<String?>(null)
    val currentPath: State<String?> = _currentPath

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> get() = _isPlaying

    fun playMusic(path: String) {

            exoPlayer.stop()
            exoPlayer.clearMediaItems()

            val mediaItem = MediaItem.fromUri(path)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            _isPlaying.value = true
            _currentPath.value = path

    }

    fun playNext() {
        if (currentIndex < queue.size - 1) {
            currentIndex++
            playMusic(queue[currentIndex].path)
        }
    }

    fun playPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            playMusic(queue[currentIndex].path)
        }
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
        clearQueue()
        _isPlaying.value = false
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.release()
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