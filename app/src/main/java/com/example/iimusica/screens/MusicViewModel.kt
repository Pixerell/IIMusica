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
import kotlinx.coroutines.Dispatchers
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

    private val _selectedSortOption = mutableStateOf(SortOption.NAME)
    val selectedSortOption: MutableState<SortOption> get() = _selectedSortOption

    private val _isDescending = mutableStateOf(false)
    val isDescending: MutableState<Boolean> get() = _isDescending

    fun setSortOption(option: SortOption) {
        if (_selectedSortOption.value == option) {
            _isDescending.value = !_isDescending.value
        } else {
            _selectedSortOption.value = option
            _isDescending.value = false
        }
    }

    private var lastSuccessfulFiles: List<MusicFile> = emptyList()
    var isFirstTimeEnteredMusic by mutableStateOf(true)

    fun loadMusicFiles(context: Context) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val files = getAllMusicFiles(context)
                lastSuccessfulFiles = files
                withContext(Dispatchers.Main) {
                    _mFiles.value = files.toList()
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mFiles.value = lastSuccessfulFiles
                    _errorMessage.value = "Error fetching music files: ${e.message}"
                    _isLoading.value = false
                }
            }
        }
    }
}
