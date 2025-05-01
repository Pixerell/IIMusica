package com.example.iimusica.screens

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iimusica.types.MusicFile
import com.example.iimusica.types.SortOption
import com.example.iimusica.utils.fetchers.getAllMusicFiles
import com.example.iimusica.utils.sortFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicViewModel : ViewModel() {
    private val _mFiles = mutableStateOf(emptyList<MusicFile>())
    val mFiles: MutableState<List<MusicFile>> get() = _mFiles

    private val _isLoading = mutableStateOf(true)
    val isLoading: MutableState<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String get() = _errorMessage.value ?: ""

    private val _searchQuery = mutableStateOf("")
    val searchQuery: MutableState<String> get() = _searchQuery

    private val _isSearching = mutableStateOf(false)
    val isSearching: MutableState<Boolean> get() = _isSearching

    private val _filteredFiles = mutableStateOf<List<MusicFile>>(emptyList())
    val filteredFiles: MutableState<List<MusicFile>> get() = _filteredFiles


    private val _animationComplete = mutableStateOf(false)
    val animationComplete: MutableState<Boolean> get() = _animationComplete

    private val _selectedSortOption = mutableStateOf(SortOption.NAME)
    val selectedSortOption: MutableState<SortOption> get() = _selectedSortOption

    private val _isDescending = mutableStateOf(false)
    val isDescending: MutableState<Boolean> get() = _isDescending

    private val _miniPlayerVisible = mutableStateOf(false)
    val miniPlayerVisible: MutableState<Boolean> get() = _miniPlayerVisible

    // Observer for albums/playlists
    private val _filesLoading = MutableSharedFlow<FilesLoadingState>(replay = 0) // New shared flow
    val filesLoading = _filesLoading.asSharedFlow() // Exposed as a read-only SharedFlow

    fun toggleMiniPlayerVisibility() {
        _miniPlayerVisible.value = !_miniPlayerVisible.value
    }

    private var lastSuccessfulFiles: List<MusicFile> = emptyList()
    var isFirstTimeEnteredMusic by mutableStateOf(true)

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
                    updateFilteredFiles()
                    _filesLoading.emit(FilesLoadingState.Loaded) // Signal that files have been successfully loaded
                }
                _isLoading.value = false
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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredFiles()
    }


    fun setSortOption(option: SortOption) {
        if (_selectedSortOption.value == option) {
            _isDescending.value = !_isDescending.value
        } else {
            _selectedSortOption.value = option
            _isDescending.value = false
        }
        updateFilteredFiles()
    }

    private fun updateFilteredFiles() {
        viewModelScope.launch(Dispatchers.Default) {
            val query = _searchQuery.value
            val filtered = if (query.isEmpty()) {
                _mFiles.value
            } else {
                _mFiles.value.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }

            val sorted = filtered.sortFiles(_selectedSortOption.value, _isDescending.value)

            withContext(Dispatchers.Main) {
                _filteredFiles.value = sorted
            }
        }
    }

    sealed class FilesLoadingState {
        object Loading : FilesLoadingState()
        object Loaded : FilesLoadingState()
        data class Error(val message: String) : FilesLoadingState()
    }
}
