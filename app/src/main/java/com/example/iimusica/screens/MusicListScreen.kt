@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.iimusica.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.iimusica.components.ButtonReload
import com.example.iimusica.components.Loader
import com.example.iimusica.utils.MusicFile
import com.example.iimusica.components.MusicList
import com.example.iimusica.components.MusicTopBar
import com.example.iimusica.utils.SortOption
import com.example.iimusica.utils.sortFiles
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography


@Composable
fun MusicListScreen(navController: NavController, context: Context, toggleTheme:() -> Unit, viewModel: MusicViewModel = viewModel(), playerViewModel: PlayerViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val mFiles by viewModel.mFiles
    val isLoading by viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    val selectedSortOption by viewModel.selectedSortOption
    val isDescending by viewModel.isDescending

    val appColors = LocalAppColors.current

    fun onSortOptionSelected(option: SortOption) {
        viewModel.setSortOption(option)
    }

    // Function to filter files based on search query
    fun filterFiles(files: List<MusicFile>, query: String): List<MusicFile> {
        return if (query.isEmpty()) {
            files
        } else {
            files.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    // Function to sort files based on the selected option and direction
    fun sortFiles(files: List<MusicFile>, sortOption: SortOption, descending: Boolean): List<MusicFile> {
        val sortedFiles = files.sortFiles(sortOption, descending)  // Perform sorting
        playerViewModel.setQueue(sortedFiles)
        return sortedFiles  // Return sorted files)
    }

    val filteredFiles by remember { derivedStateOf { filterFiles(mFiles, searchQuery) } }
    val sortedFiles by remember { derivedStateOf { sortFiles(filteredFiles, selectedSortOption, isDescending) } }


    // Use LaunchedEffect to launch a coroutine for fetching music files
    LaunchedEffect(Unit) {
        if (mFiles.isEmpty()) {
            viewModel.loadMusicFiles(context)
        }

    }

    Scaffold(
            topBar = { MusicTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = {searchQuery = it},
                isSearching = isSearching,
                onToggleSearch = { isSearching = !isSearching },
                onSortOptionSelected = { onSortOptionSelected(it) },
                selectedSortOption = selectedSortOption,
                isDescending = isDescending,
                toggleTheme = toggleTheme

            ) },

        floatingActionButton = {
            ButtonReload(playerViewModel, viewModel, context)
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = appColors.accentGradient
                )
        ) {
            if (isLoading) {
                Loader(modifier = Modifier.align(Alignment.Center))
            }

            else if (errorMessage.isNotEmpty()) {
                Text(
                    text = "There was an error $errorMessage",
                    color = appColors.font,
                    fontSize = 40.sp,
                    modifier = Modifier.align(Alignment.Center),


                )
            }

            else {
                if (sortedFiles.isEmpty()) {
                    Text("|| No music files found ||", color = appColors.font,
                        fontSize = Typography.bodyLarge.fontSize,
                        fontFamily = Typography.bodyLarge.fontFamily,
                        fontWeight = Typography.bodyLarge.fontWeight,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 128.dp)
                    )

                }
                else {
                    if (playerViewModel.getQueue().isEmpty()) {
                        playerViewModel.setQueue(sortedFiles)  // Initialize the queue with sorted files only if it's empty
                    }
                    MusicList(musicFiles = sortedFiles, navController = navController, playerViewModel=playerViewModel)

                        Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).zIndex(1f)) {
                            MiniPlayer(playerViewModel = playerViewModel, navController = navController)
                        }

                }
            }
        }
    }
}

