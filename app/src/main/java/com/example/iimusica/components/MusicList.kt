package com.example.iimusica.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
    val lastIndex = musicFiles.lastIndex
    val bottomPadding = PaddingValues(bottom = 124.dp)
    val listState = rememberLazyListState()
    val currentPath = playerViewModel.currentPath.value


    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = bottomPadding
    ) {
        itemsIndexed(musicFiles, key = { _, item -> item.path }) { index, music ->

            MusicItem(

                music = music,
                navController = navController,
                isLastItem = index == lastIndex,
                playerViewModel = playerViewModel,
                isCurrentPlaying = music.path == currentPath
            )
        }
    }
}
