package com.example.iimusica.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AlbumViewModelFactory(
    private val musicViewModel: MusicViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumViewModel(musicViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
