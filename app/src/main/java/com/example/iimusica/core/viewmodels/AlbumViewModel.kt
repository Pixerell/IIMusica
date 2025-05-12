package com.example.iimusica.core.viewmodels

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.types.Album
import com.example.iimusica.types.AlbumSummary
import com.example.iimusica.types.MusicFile
import com.example.iimusica.types.SortOption
import com.example.iimusica.utils.fetchers.fetchExtendedMetadataForMusicFile
import com.example.iimusica.utils.parseDuration
import com.example.iimusica.utils.sortByOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.R)
@UnstableApi
class AlbumViewModel(
    private val app: Application,
    private val musicViewModel: MusicViewModel,
) : AndroidViewModel(app) {

    private val _albums = mutableStateOf<List<AlbumSummary>>(emptyList())
    val albums: State<List<AlbumSummary>> = _albums
    private val _filteredAlbums = mutableStateOf<List<AlbumSummary>>(emptyList())
    val filteredAlbums: State<List<AlbumSummary>> get() = _filteredAlbums
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String get() = _errorMessage.value ?: ""

    private val metadataCache = mutableMapOf<String, MusicFile>()

    private val context: Context get() = app.applicationContext

    init {
        viewModelScope.launch {
            musicViewModel.filesLoading.collectLatest{ state ->
                handleFilesLoadingState(state)
            }
        }
    }

    private fun updateAlbumSummaries(files: List<MusicFile>) {
        _albums.value = groupIntoAlbumSummaries(files)
    }

    private fun groupIntoAlbumSummaries(files: List<MusicFile>): List<AlbumSummary> {
        return files.groupBy { it.albumId }.mapNotNull { (_, songs) ->
            if (songs.isEmpty()) return@mapNotNull null
            val representativeSong =
                songs.firstOrNull { it.albumArtBitmap != null } ?: songs.first()

            // Fetch extended metadata for the representative song
            val representativeSongWithMetadata = getCachedExtendedMetadata(representativeSong)

            AlbumSummary(
                name = representativeSongWithMetadata.album,
                artist = representativeSongWithMetadata.artist,
                representativeSong = representativeSongWithMetadata
            )
        }.sortedBy { it.name ?: "Unknown Album" }
    }


    suspend fun getAlbumById(albumId: Long): Album? {
        val songs = getSongsForAlbum(albumId)
        if (songs.isEmpty()) return null
        val rawRepresentative = songs.firstOrNull { it.albumArtBitmap != null } ?: songs.first()
        val representativeSong = getCachedExtendedMetadata(rawRepresentative)
        val sortedSongs = fetchMetadataForAllAlbumTracks(albumId)
        return Album(
            albumId = albumId,
            name = representativeSong.album,
            artist = representativeSong.artist,
            songs = sortedSongs,
            albumArtBitmap = representativeSong.albumArtBitmap,
            representativeSong = representativeSong,
        )
    }

    fun getSongsForAlbum(albumId: Long): List<MusicFile> {
        return musicViewModel.mFiles.value.filter { it.albumId == albumId }
    }

    fun updateFilteredAlbums(state: SearchSortState) {
        if (_albums.value.isEmpty()) {
            return
        }
        _isLoading.value = false
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
            // preindexing
            val songsByAlbum = musicViewModel.mFiles.value.groupBy { it.albumId }
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
                    val albumId = it.representativeSong.albumId
                    val songs = songsByAlbum[albumId] ?: emptyList()
                    when (state.sortOption) {
                        SortOption.SIZE -> songs.sumOf { it.size }
                        SortOption.DURATION -> songs.sumOf { parseDuration(it.duration) }
                        SortOption.DATE -> it.representativeSong.dateAdded
                        else -> null
                    }
                })

            withContext(Dispatchers.Main) {
                _filteredAlbums.value = sorted
                _isLoading.value = false
            }
        }
    }

    fun getTotalDuration(albumId: Long): Long {
        return getSongsForAlbum(albumId).sumOf { parseDuration(it.duration) }
    }

    fun getAlbumStorageSize(songs: List<MusicFile>): String {
        val totalBytes = songs.sumOf { it.size }
        val totalMB = totalBytes.toDouble() / (1024 * 1024)
        return "%.1f MB".format(totalMB)
    }

    private fun getCachedExtendedMetadata(file: MusicFile): MusicFile {
        return metadataCache.getOrPut(file.path) {
            runCatching {
                fetchExtendedMetadataForMusicFile(context, file)
            }.getOrElse {
                Log.e("AlbumViewModel", "Failed to fetch metadata for ${file.path}", it)
                file
            }
        }
    }

    private suspend fun fetchMetadataForAllAlbumTracks(albumId: Long): List<MusicFile> {
        val albumTracks = getSongsForAlbum(albumId)
        return withContext(Dispatchers.IO) {
            albumTracks.map { song ->
                getCachedExtendedMetadata(song)
            }.sortedBy { it.trackNumber ?: Int.MAX_VALUE } // fallback to the end if track number is missing
        }
    }


    private fun handleFilesLoadingState(state: MusicViewModel.FilesLoadingState) {
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
