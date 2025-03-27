package com.example.iimusica.screens


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(application.applicationContext).build()
    private var currentPath: String? = null

    fun playMusic(path: String) {
        if (currentPath != path) {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            val mediaItem = MediaItem.fromUri(path)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            currentPath = path // Update the current path
        }
    }


    override fun onCleared() {
        super.onCleared()
        // We don't release the player here as it is managed across multiple screens
    }
}