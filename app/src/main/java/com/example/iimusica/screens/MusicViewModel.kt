package com.example.iimusica.screens

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iimusica.MusicFile
import com.example.iimusica.getAllMusicFiles
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


    fun loadMusicFiles(context: Context) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val files = getAllMusicFiles(context)
                withContext(Dispatchers.Main) {
                    _mFiles.value = files.toList()
                    Log.d("MusicViewModel", "Music files loaded: ${files}, then size ${files.size}")
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Error fetching music files: ${e.message}"
                    _isLoading.value = false
                }
            }
        }
    }
}
