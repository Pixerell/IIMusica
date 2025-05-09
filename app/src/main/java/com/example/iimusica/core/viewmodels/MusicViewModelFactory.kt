package com.example.iimusica.core.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi

@Suppress("UNCHECKED_CAST")
@RequiresApi(Build.VERSION_CODES.R)
class MusicViewModelFactory(
    private val application: Application,
    private val musicViewModel: MusicViewModel,
) : ViewModelProvider.Factory {
    @OptIn(UnstableApi::class)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AlbumViewModel::class.java) -> {
                AlbumViewModel(application, musicViewModel) as T
            }
            modelClass.isAssignableFrom(PlaylistViewModel::class.java) -> {
                PlaylistViewModel(musicViewModel) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
