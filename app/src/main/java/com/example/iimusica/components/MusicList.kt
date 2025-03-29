package com.example.iimusica.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.utils.MusicFile

@Composable
fun MusicList(
    musicFiles: List<MusicFile>,
    navController: NavController,
    playerViewModel: PlayerViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 124.dp)
    ) {
        itemsIndexed(musicFiles) { index, music ->
            val isLastItem = index == musicFiles.lastIndex
            MusicItem(music = music, navController = navController, isLastItem = isLastItem, playerViewModel = playerViewModel)
        }
    }
}
