@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.iimusica.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.iimusica.MusicFile
import com.example.iimusica.getAllMusicFiles
import com.example.iimusica.components.MusicItem
import com.example.iimusica.components.MusicTopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MusicListScreen(navController: NavController, context: Context) {
    var mFiles by remember { mutableStateOf(emptyList<MusicFile>()) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredFiles by remember { mutableStateOf(emptyList<MusicFile>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()


    fun loadMusicFiles() {
        isLoading = true
        errorMessage = null // Reset error message on new fetch
        fetchMusicFiles(context, coroutineScope, { files ->
            mFiles = files
            isLoading = false
        }, { error ->
            errorMessage = error
            isLoading = false
        })
    }

    // Use LaunchedEffect to launch a coroutine for fetching music files
    LaunchedEffect(Unit) {
        loadMusicFiles()
    }


    // Using rememberUpdatedState to keep the latest value of mFiles
    val currentFiles = rememberUpdatedState(mFiles)

    // Update the filtered list when either mFiles or searchQuery changes
    LaunchedEffect(currentFiles.value, searchQuery) {
        filteredFiles = when {
            currentFiles.value.isEmpty() -> emptyList() // Show empty list if no files are available
            searchQuery.isEmpty() -> currentFiles.value // Show all files if the search query is empty
            else -> currentFiles.value.filter { music ->
                music.name.contains(searchQuery, ignoreCase = true) // Case-insensitive search
            }
        }
    }
    Scaffold(
            topBar = { MusicTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = {searchQuery = it}
            ) },
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                FloatingActionButton(
                    onClick = {
                        loadMusicFiles()
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh Music Files")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0B1045), Color(0xFF5B245A))
                    )
                )
        ) {
            if (isLoading) {

                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    // Outer circle (Outline)
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(6.dp, Color.Gray.copy(alpha = 0.25f))
                    ) {
                        // Inner CircularProgressIndicator
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White,
                            strokeWidth = 6.dp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredFiles) { music ->
                        MusicItem(music, navController)
                    }
                }
            }
        }
    }
}

fun fetchMusicFiles(context: Context, coroutineScope: CoroutineScope, onSuccess: (List<MusicFile>) -> Unit, onError: (String) -> Unit) {
    coroutineScope.launch(Dispatchers.IO) {
        try {
            val musicFiles = getAllMusicFiles(context)
            withContext(Dispatchers.Main) {
                onSuccess(musicFiles)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Error fetching music files: ${e.message}")
            }
        }
    }
}
