package com.example.iimusica.core.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iimusica.types.MusicFile
import com.example.iimusica.types.SortOption
import com.example.iimusica.utils.fetchers.getAllMusicFiles
import com.example.iimusica.utils.sortByOption
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

    private val _shouldScrollTop = mutableStateOf(false)
    val shouldScrollTop: MutableState<Boolean> get() = _shouldScrollTop

    // Observer for albums/playlists
    private val _filesLoading = MutableSharedFlow<FilesLoadingState>(replay = 0)
    val filesLoading = _filesLoading.asSharedFlow()
    private var lastSuccessfulFiles: List<MusicFile> = emptyList()

    fun loadMusicFiles(context: Context) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            _filesLoading.emit(FilesLoadingState.Loading) // Signal that files are being reset and loaded
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val files = getAllMusicFiles(context)
                lastSuccessfulFiles = files
                withContext(Dispatchers.Main) {
                    _mFiles.value = files.toList()
                    updateFilteredFiles(sharedViewModel.getState(ScreenKey.Songs))
                    _filesLoading.emit(FilesLoadingState.Loaded) // Signal that files have been successfully loaded
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
        //prevents premature filtering due to multithreadding
        if (_mFiles.value.isEmpty()) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            val query = state.query
            val filtered = if (query.isEmpty()) {
                _mFiles.value
            } else {
                _mFiles.value.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }

            val sorted = filtered.sortByOption(
                state.sortOption,
                state.isDescending,
                selector = {
                    when (state.sortOption) {
                        SortOption.NAME -> it.name
                        SortOption.ARTIST -> it.artist
                        else -> null
                    }
                },
                numericSelector = {
                    when (state.sortOption) {
                        SortOption.SIZE -> it.size.toLong()
                        SortOption.DURATION -> it.duration.toLong()
                        SortOption.DATE -> it.dateAdded
                        else -> null
                    }
                }
            )

            withContext(Dispatchers.Main) {
                _filteredFiles.value = sorted
                shouldScrollTop.value = true
                _isLoading.value = false
            }
        }
    }

    fun removeMissingFile(path: String) {
        val updatedList = _mFiles.value.filter { it.path != path }
        _mFiles.value = updatedList
        shouldScrollTop.value = false
        updateFilteredFiles(sharedViewModel.getState(ScreenKey.Songs))
        viewModelScope.launch(Dispatchers.IO) {
            _filesLoading.emit(FilesLoadingState.Loaded)
        }
    }

    sealed class FilesLoadingState {
        object Loading : FilesLoadingState()
        object Loaded : FilesLoadingState()
        data class Error(val message: String) : FilesLoadingState()
    }
}
