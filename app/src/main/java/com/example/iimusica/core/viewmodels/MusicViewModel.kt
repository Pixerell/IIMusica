package com.example.iimusica.core.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iimusica.types.MusicFile
import com.example.iimusica.types.SortOption
import com.example.iimusica.utils.fetchers.getAllMusicFiles
import com.example.iimusica.utils.filterAndSortList
import com.example.iimusica.utils.parseDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicViewModel(
    private val sharedViewModel: SharedViewModel
) : ViewModel(
) {

    private val _mFiles = mutableStateOf(emptyList<MusicFile>())
    val mFiles: MutableState<List<MusicFile>> get() = _mFiles

    private val _isLoading = mutableStateOf(true)
    val isLoading: MutableState<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String get() = _errorMessage.value ?: ""

    private val _filteredFiles = mutableStateOf<List<MusicFile>>(emptyList())
    val filteredFiles: MutableState<List<MusicFile>> get() = _filteredFiles

    private val _currentLoadingPath = mutableStateOf<String>("")
    val currentLoadingPath: MutableState<String> get() = _currentLoadingPath
    val loadingFiles = mutableStateListOf<MusicFile>()

    private var lastSongFilterState: SearchSortState? = null

    private val _shouldScrollTop = mutableStateOf(false)
    val shouldScrollTop: MutableState<Boolean> get() = _shouldScrollTop

    // Observer for albums/playlists
    private val _filesLoading = MutableSharedFlow<FilesLoadingState>(replay = 0)
    val filesLoading = _filesLoading.asSharedFlow()
    private var lastSuccessfulFiles: List<MusicFile> = emptyList()

    private val _isFullLoaded = mutableStateOf(false)
    val isFullLoaded : MutableState<Boolean> = _isFullLoaded

    fun loadMusicFiles(context: Context) {
        _isLoading.value = true
        _isFullLoaded.value = false
        _errorMessage.value = null
        _mFiles.value = emptyList()
        lastSongFilterState = null
        loadingFiles.clear()

        viewModelScope.launch {
            _filesLoading.emit(FilesLoadingState.Loading)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAllMusicFiles(context).collect { file ->
                    withContext(Dispatchers.Main) {
                        loadingFiles.add(file)
                        _mFiles.value = loadingFiles.toList()
                        _filteredFiles.value = loadingFiles.toList()
                        currentLoadingPath.value = file.path
                    }
                }
                withContext(Dispatchers.Main) {
                    lastSuccessfulFiles = loadingFiles.toList()
                    lastSongFilterState = null // so it re-applies sorting later
                    updateFilteredFiles(sharedViewModel.getState(ScreenKey.Songs))
                    _isLoading.value = false
                    _filesLoading.emit(FilesLoadingState.Loaded)
                    _isFullLoaded.value = true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mFiles.value = lastSuccessfulFiles
                    _errorMessage.value = "Error fetching music files: ${e.message}"
                    _isLoading.value = false
                    _filesLoading.emit(FilesLoadingState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }


    fun updateFilteredFiles(state: SearchSortState) {
        // Prevents premature filtering due to multithreading and cached states
        if (_mFiles.value.isEmpty()) return
        if (state == lastSongFilterState) {
            _isLoading.value = false
            return
        }

        _isLoading.value = true
        viewModelScope.launch(Dispatchers.Default) {
            val sorted = filterAndSortList(
                originalList = _mFiles.value,
                state = state,
                querySelector = { it.name },
                stringSelector = {
                    when (state.sortOption) {
                        SortOption.NAME -> it.name
                        SortOption.ARTIST -> it.artist
                        else -> null
                    }
                },
                numericSelector = {
                    when (state.sortOption) {
                        SortOption.SIZE -> it.size.toLong()
                        SortOption.DURATION -> parseDuration(it.duration)
                        SortOption.DATE -> it.dateAdded
                        else -> null
                    }
                }
            )

            withContext(Dispatchers.Main) {
                _filteredFiles.value = sorted
                lastSongFilterState = state.copy()
                shouldScrollTop.value = true
                _isLoading.value = false
            }
        }
    }

    fun removeMissingFile(path: String) {
        val updatedList = _mFiles.value.filter { it.path != path }
        _mFiles.value = updatedList
        shouldScrollTop.value = false
        _isFullLoaded.value = false
        updateFilteredFiles(sharedViewModel.getState(ScreenKey.Songs))
        viewModelScope.launch(Dispatchers.IO) {
            _filesLoading.emit(FilesLoadingState.Loaded)
            _isFullLoaded.value = true
        }
    }

    fun getMusicFileByPath(path: String): MusicFile? {
        return _mFiles.value.find { it.path == path }
    }

    sealed class FilesLoadingState {
        object Loading : FilesLoadingState()
        object Loaded : FilesLoadingState()
        data class Error(val message: String) : FilesLoadingState()
    }
}
