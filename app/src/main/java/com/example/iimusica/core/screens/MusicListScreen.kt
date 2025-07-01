@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.iimusica.core.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.ux.InfoBox
import com.example.iimusica.components.ux.MessageType
import com.example.iimusica.components.mediacomponents.MusicListWithPull
import com.example.iimusica.core.player.PlaybackCommandBus
import com.example.iimusica.core.viewmodels.MusicViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.core.viewmodels.SharedViewModel
import com.example.iimusica.types.DEFAULT_QUEUE_NAME
import com.example.iimusica.types.DEFAULT_SORTED_QUEUE_NAME
import com.example.iimusica.types.SKIP_CHECK_CODE
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
    sharedViewModel: SharedViewModel
) {
    val filteredFiles by musicViewModel.filteredFiles
    val mFiles by musicViewModel.mFiles
    val isLoading by musicViewModel.isLoading
    val errorMessage = musicViewModel.errorMessage
    val appColors = LocalAppColors.current
    val state = rememberPullToRefreshState()
    val musicListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (mFiles.isEmpty()) {
            musicViewModel.loadMusicFiles(context)
        }
        //eats commands from bus for notifications
        PlaybackCommandBus.commands.collectLatest {}
    }

    LaunchedEffect(filteredFiles) {
        if (playerViewModel.currentCollectionID.value == SKIP_CHECK_CODE) {
            var newQueueName = DEFAULT_SORTED_QUEUE_NAME
            playerViewModel.queueManager.setQueue(filteredFiles, newQueueName)
        }

        if (musicViewModel.shouldScrollTop.value) {
            musicListState.scrollToItem(0)
            musicViewModel.shouldScrollTop.value = false
        }
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
                MusicListWithPull(
                    musicFiles = filteredFiles,
                    navController = navController,
                    playerViewModel = playerViewModel,
                    listState = musicListState,
                    isRefreshing = false,
                    pullState = state,
                    appColors = appColors,
                    onRefresh = {
                        reloadmlist(
                            playerViewModel,
                            musicViewModel,
                            sharedViewModel,
                            context
                        )
                    }
                )
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
                } else if (filteredFiles.isEmpty()) {
                    InfoBox(
                        message = "All files were sorted and filtered out",
                        type = MessageType.Info,
                    )
                } else {
                    if (playerViewModel.queueManager.getQueue().isEmpty()) {
                        playerViewModel.queueManager.setQueue(
                            filteredFiles,
                            DEFAULT_QUEUE_NAME
                        )  // Initialize the queue with sorted files only if it's empty
                    }
                    MusicListWithPull(
                        musicFiles = filteredFiles,
                        navController = navController,
                        playerViewModel = playerViewModel,
                        listState = musicListState,
                        isRefreshing = musicViewModel.isLoading.value,
                        pullState = state,
                        appColors = appColors,
                        onRefresh = {
                            reloadmlist(
                                playerViewModel,
                                musicViewModel,
                                sharedViewModel,
                                context
                            )
                        }
                    )
                }
            }
        }
    }
}
