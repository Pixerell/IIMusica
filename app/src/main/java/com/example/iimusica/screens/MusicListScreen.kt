@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.iimusica.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.PositionalThreshold
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.ux.InfoBox
import com.example.iimusica.components.ux.Loader
import com.example.iimusica.components.ux.MessageType
import com.example.iimusica.components.mediacomponents.MusicList
import com.example.iimusica.player.PlaybackCommandBus
import com.example.iimusica.types.MusicFile
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.reloadmlist
import kotlinx.coroutines.flow.collectLatest


@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MusicListScreen(
    navController: NavController,
    context: Context,
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    lazyListState: LazyListState,
    filteredFiles: List<MusicFile>
) {
    val mFiles by musicViewModel.mFiles
    val isLoading by musicViewModel.isLoading
    val errorMessage = musicViewModel.errorMessage
    val appColors = LocalAppColors.current
    val state = rememberPullToRefreshState()

    // To launch a coroutine for fetching music files
    LaunchedEffect(Unit) {
        if (mFiles.isEmpty()) {
            musicViewModel.loadMusicFiles(context)
        }
        //eats commands from bus for notifications
        PlaybackCommandBus.commands.collectLatest {}
    }

    LaunchedEffect(filteredFiles) {
        playerViewModel.queueManager.setQueue(filteredFiles)
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = appColors.accentGradient
                )
        ) {
            if (isLoading) {
                Loader(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage.isNotEmpty()) {
                InfoBox(
                    message = "There was an error $errorMessage",
                    type = MessageType.Error,
                )
            } else {
                if (mFiles.isEmpty()) {
                    InfoBox(
                        message = "No files found on your device. Please download them to your storage",
                        type = MessageType.Warning,
                    )
                } else if (filteredFiles.isEmpty() && musicViewModel.searchQuery.value.isNotEmpty()) {
                    InfoBox(
                        message = "All files were sorted and filtered out",
                        type = MessageType.Info,
                    )
                } else {
                    if (playerViewModel.queueManager.getQueue().isEmpty()) {
                        playerViewModel.queueManager.setQueue(filteredFiles)  // Initialize the queue with sorted files only if it's empty
                    }
                    PullToRefreshBox(
                        state = state,
                        isRefreshing = musicViewModel.isLoading.value,
                        indicator = {
                            Indicator(
                                isRefreshing = musicViewModel.isLoading.value,
                                containerColor = appColors.backgroundDarker,
                                color = appColors.icon,
                                state = state,
                                threshold = PositionalThreshold,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .size(90.dp)
                                    .padding(16.dp)
                            )
                        },
                        onRefresh = {
                            reloadmlist(playerViewModel, musicViewModel, context)
                        }
                    ) {
                        MusicList(
                            musicFiles = filteredFiles,
                            navController = navController,
                            playerViewModel = playerViewModel,
                            listState = lazyListState
                        )
                    }
                }
            }
        }
    }
}

