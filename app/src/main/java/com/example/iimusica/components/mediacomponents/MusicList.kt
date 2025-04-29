package com.example.iimusica.components.mediacomponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.ux.LazyColumnScrollBar
import com.example.iimusica.components.ux.ListScrollBar
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.types.MusicFile
import kotlinx.coroutines.launch


@UnstableApi
@Composable
fun MusicList(
    musicFiles: List<MusicFile>,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    listState: LazyListState = rememberLazyListState()
) {
    val lastIndex = musicFiles.lastIndex
    val bottomPadding = PaddingValues(bottom = 124.dp)
    val currentPath = playerViewModel.currentPath.value

    Box(modifier = Modifier.fillMaxSize()) {
        // LazyColumn displaying music files
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
        /*
        *         ListScrollBar(
            listState = listState,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
        * */
        LazyColumnScrollBar(lazyListState = listState)
    }
}
