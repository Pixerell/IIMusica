package com.example.iimusica.screens

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.player.PlaybackController

@OptIn(UnstableApi::class)
class PlayerViewModelFactory(
    private val application: Application,
    private val playbackController: PlaybackController
) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(application, playbackController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
