package com.example.iimusica.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SharedSearchViewModelFactory(
    private val sharedSearchViewModel: SharedSearchViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicViewModel(sharedSearchViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}