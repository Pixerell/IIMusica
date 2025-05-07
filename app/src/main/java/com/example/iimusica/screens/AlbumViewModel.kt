package com.example.iimusica.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iimusica.types.Album
import com.example.iimusica.types.AlbumSummary
import com.example.iimusica.types.MusicFile
import com.example.iimusica.types.SortOption
import com.example.iimusica.utils.parseDuration
import com.example.iimusica.utils.sortByOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@androidx.media3.common.util.UnstableApi
class AlbumViewModel(
    private val musicViewModel: MusicViewModel
) : ViewModel() {

    private val _albums = mutableStateOf<List<AlbumSummary>>(emptyList())
    val albums: State<List<AlbumSummary>> = _albums
    private val _filteredAlbums = mutableStateOf<List<AlbumSummary>>(emptyList())
    val filteredAlbums: MutableState<List<AlbumSummary>> get() = _filteredAlbums
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
                        updateAlbumSummaries(musicViewModel.mFiles.value)
                    }

                    is MusicViewModel.FilesLoadingState.Error -> {
                        _errorMessage.value = state.message
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    private fun updateAlbumSummaries(files: List<MusicFile>) {
        _albums.value = groupIntoAlbumSummaries(files)
    }

    private fun groupIntoAlbumSummaries(files: List<MusicFile>): List<AlbumSummary> {
        return files
            .groupBy { it.albumId }
            .mapNotNull { (_, songs) ->
                if (songs.isEmpty()) return@mapNotNull null
                val representativeSong = songs.firstOrNull { it.albumArtBitmap != null }
                    ?: songs.first()
                AlbumSummary(
                    name = representativeSong.album,
                    artist = representativeSong.artist,
                    representativeSong = representativeSong
                )
            }
            .sortedBy { it.name }
    }


    fun getAlbumById(albumId: Long): Album? {
        val songs = getSongsForAlbum(albumId)
        if (songs.isEmpty()) return null
        val representativeSong = songs.firstOrNull { it.albumArtBitmap != null } ?: songs.first()
        return Album(
            albumId = albumId,
            name = representativeSong.album,
            artist = representativeSong.artist,
            songs = songs,
            albumArtBitmap = representativeSong.albumArtBitmap
        )
    }

    fun getSongsForAlbum(albumId: Long): List<MusicFile> {
        return musicViewModel.mFiles.value.filter { it.albumId == albumId }
    }

    fun updateFilteredAlbums(state: SearchSortState) {
        if (_albums.value.isEmpty()) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            val query = state.query
            val filtered = if (query.isEmpty()) {
                _albums.value
            } else {
                _albums.value.filter {
                    val albumName = it.name ?: "Unknown Album"
                    albumName.contains(query, ignoreCase = true)
                }
            }
            val sorted = filtered.sortByOption(
                sortOption = state.sortOption,
                isDescending = state.isDescending,
                selector = {
                    when (state.sortOption) {
                        SortOption.NAME -> it.name ?: "Unknown Album"
                        SortOption.ARTIST -> it.artist ?: "Unknown Artist"
                        else -> it.name ?: "Unknown Album"
                    }
                },
                numericSelector = {
                    when (state.sortOption) {
                        SortOption.SIZE -> getTotalSize(it.representativeSong.albumId)
                        SortOption.DURATION -> getTotalDuration(it.representativeSong.albumId)
                        else -> null
                    }
                }
            )

            withContext(Dispatchers.Main) {
                _filteredAlbums.value = sorted
                _isLoading.value = false
            }
        }
    }

    private fun getTotalSize(albumId: Long): Long {
        return getSongsForAlbum(albumId).sumOf { it.size }
    }

    private fun getTotalDuration(albumId: Long): Long {
        return getSongsForAlbum(albumId)
            .sumOf{ parseDuration(it.duration) }
    }

}
