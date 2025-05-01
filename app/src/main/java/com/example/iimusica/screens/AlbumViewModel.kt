package com.example.iimusica.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iimusica.types.AlbumSummary
import com.example.iimusica.types.MusicFile
import kotlinx.coroutines.launch

@androidx.media3.common.util.UnstableApi
class AlbumViewModel(
    private val musicViewModel: MusicViewModel
) : ViewModel() {

    private val _albums = mutableStateOf<List<AlbumSummary>>(emptyList())
    val albums: State<List<AlbumSummary>> = _albums
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String get() = _errorMessage.value ?: ""


    init {
        viewModelScope.launch {
            musicViewModel.filesLoading.collect { state ->
                when (state) {
                    is MusicViewModel.FilesLoadingState.Loading -> {
                        _albums.value = emptyList()
                        _isLoading.value = true
                    }

                    is MusicViewModel.FilesLoadingState.Loaded -> {
                        updateAlbums(musicViewModel.mFiles.value)
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

    fun getSongsForAlbum(name: String, artist: String): List<MusicFile> {
        return musicViewModel.mFiles.value.filter {
            it.album == name && it.artist == artist
        }
    }

    private fun updateAlbums(files: List<MusicFile>) {
        _albums.value = groupIntoAlbumSummaries(files)
    }

    private fun groupIntoAlbumSummaries(files: List<MusicFile>): List<AlbumSummary> {
        return files
            .groupBy { it.albumId } // Use albumId to group
            .map { (_, songs) ->
                val representativeSong = songs.firstOrNull { it.albumArtBitmap != null }
                    ?: songs.firstOrNull() // Fallback to the first song if no valid album art exists
                AlbumSummary(
                    name = songs.first().album,
                    artist = songs.first().artist,
                    representativeSong = representativeSong
                )
            }
            .sortedBy { it.name } // Sort albums by name
    }

}
