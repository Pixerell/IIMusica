package com.example.iimusica.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val musicViewModel: MusicViewModel
) : ViewModel() {

    private val _playlists = mutableStateOf<String>("")
    val playlists: State<String> = _playlists
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String get() = _errorMessage.value ?: ""

    init {
        viewModelScope.launch {
            musicViewModel.filesLoading.collect { state ->
                when (state) {
                    is MusicViewModel.FilesLoadingState.Loading -> {
                        _playlists.value = "loading"
                        _isLoading.value = true
                    }

                    is MusicViewModel.FilesLoadingState.Loaded -> {
                        _playlists.value = "loaded"
                        _isLoading.value = false
                    }

                    is MusicViewModel.FilesLoadingState.Error -> {
                        _errorMessage.value = state.message
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun updateFilteredPlaylists(state: SearchSortState) {

    }
}