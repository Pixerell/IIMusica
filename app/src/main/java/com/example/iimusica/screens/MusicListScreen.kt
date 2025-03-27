@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.iimusica.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.iimusica.utils.MusicFile
import com.example.iimusica.components.MusicItem
import com.example.iimusica.components.MusicTopBar
import com.example.iimusica.utils.SortOption
import com.example.iimusica.utils.sortFiles
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography


@Composable
fun MusicListScreen(navController: NavController, context: Context, toggleTheme:() -> Unit, viewModel: MusicViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val mFiles by viewModel.mFiles
    val isLoading by viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    val selectedSortOption by viewModel.selectedSortOption
    val isDescending by viewModel.isDescending

    val appColors = LocalAppColors.current



    // Use LaunchedEffect to launch a coroutine for fetching music files
    LaunchedEffect(Unit) {
        if (mFiles.isEmpty()) {
            viewModel.loadMusicFiles(context)
        }
    }

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
        return files.sortFiles(sortOption, descending)
    }

    val filteredFiles by remember { derivedStateOf { filterFiles(mFiles, searchQuery) } }
    val sortedFiles by remember { derivedStateOf { sortFiles(filteredFiles, selectedSortOption, isDescending) } }


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
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.loadMusicFiles(context)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    contentColor = appColors.icon,
                    containerColor = appColors.backgroundDarker,


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
                    brush = appColors.accentGradient
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
                        border = BorderStroke(6.dp, appColors.secondaryFont.copy(alpha = 0.25f))
                    ) {
                        // Inner CircularProgressIndicator
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = appColors.icon,
                            strokeWidth = 6.dp
                        )
                    }
                }
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
                if (filteredFiles.isEmpty()) {
                    Text("|| No music files found ||", color = appColors.font,
                        fontSize = Typography.bodyLarge.fontSize,
                        fontFamily = Typography.bodyLarge.fontFamily,
                        fontWeight = Typography.bodyLarge.fontWeight,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 128.dp)
                    )

                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 124.dp)

                ) {
                    itemsIndexed(sortedFiles) { index, music ->
                        val isLastItem = index == filteredFiles.lastIndex
                        MusicItem(music = music, navController = navController, isLastItem = isLastItem)
                    }
                }
            }
        }
    }
}

