@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.iimusica.screens

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.PositionalThreshold
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.MiniPlayer
import com.example.iimusica.components.buttons.ButtonReload
import com.example.iimusica.components.ux.InfoBox
import com.example.iimusica.components.ux.Loader
import com.example.iimusica.components.ux.MessageType
import com.example.iimusica.components.mediacomponents.MusicList
import com.example.iimusica.components.mediacomponents.MusicTopBar
import com.example.iimusica.player.PlaybackCommandBus
import com.example.iimusica.types.MusicTopBarActions
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.LocalDismissSearch
import com.example.iimusica.utils.reloadmlist
import kotlinx.coroutines.flow.collectLatest


@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MusicListScreen(
    navController: NavController,
    context: Context,
    toggleTheme: () -> Unit,
    viewModel: MusicViewModel,
    playerViewModel: PlayerViewModel
) {
    var isSearching by viewModel.isSearching
    val mFiles by viewModel.mFiles
    val isLoading by viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val selectedSortOption by viewModel.selectedSortOption
    val isDescending by viewModel.isDescending
    val appColors = LocalAppColors.current
    val state = rememberPullToRefreshState()
    val lazylistState = rememberLazyListState()
    val filteredFiles by viewModel.filteredFiles


    val screenHeight =
        with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val targetOffset =
        if (!viewModel.isFirstTimeEnteredMusic) Offset(0f, 0f) else Offset(0f, screenHeight)
    val offset by animateOffsetAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 1000, delayMillis = 0),
        label = "MiniPlayerSlideIn"
    )
    var animationComplete by viewModel.animationComplete
    val intOffset = IntOffset(offset.x.toInt(), offset.y.toInt())
    val fabOffsetY by animateDpAsState(
        targetValue = if (!viewModel.miniPlayerVisible.value) 0.dp else 140.dp,
        animationSpec = tween(durationMillis = 1000),
        label = "FABOffset"
    )
    // To launch a coroutine for fetching music files
    LaunchedEffect(Unit) {
        if (mFiles.isEmpty()) {
            viewModel.loadMusicFiles(context)
        }
        //eats commands from bus for notifications
        PlaybackCommandBus.commands.collectLatest {}
    }
    // Reset flag after animation completes
    LaunchedEffect(offset, playerViewModel.isPlaying) {
        if (offset == targetOffset && viewModel.isFirstTimeEnteredMusic && playerViewModel.isPlaying) {
            animationComplete = true
        }
    }
    // Reset the flag after animation is complete
    LaunchedEffect(animationComplete) {
        if (animationComplete && viewModel.isFirstTimeEnteredMusic) {
            viewModel.isFirstTimeEnteredMusic = false
            viewModel.miniPlayerVisible.value = true
        }
    }
    LaunchedEffect(filteredFiles) {
        lazylistState.animateScrollToItem(0)
        playerViewModel.queueManager.setQueue(filteredFiles)
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                MusicTopBar(
                    searchQuery = viewModel.searchQuery.value,
                    isSearching = isSearching,
                    selectedSortOption = selectedSortOption,
                    isDescending = isDescending,
                    actions = MusicTopBarActions(
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onToggleSearch = { isSearching = !isSearching },
                        onSortOptionSelected = { viewModel.setSortOption(it) },
                        toggleTheme = toggleTheme,
                        onReloadLocalFiles = {reloadmlist(playerViewModel, viewModel, context)},
                        onReshuffle = {playerViewModel.queueManager.regenerateShuffleOrder()}
                    )

                )
            },

            floatingActionButton = {
                Box(modifier = Modifier.offset(y = fabOffsetY)) {
                    ButtonReload(playerViewModel, viewModel, context)
                }
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // This padding is required so that list doesnt go **inside** the topappbar
                    .padding(padding)
                    .background(
                        brush = appColors.accentGradient
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            isSearching = false
                        })
                    }) {
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
                    } else if (filteredFiles.isEmpty() && viewModel.searchQuery.value.isNotEmpty() ) {
                        InfoBox(
                            message = "All files were sorted and filtered out",
                            type = MessageType.Info,
                        )
                    } else {
                        if (playerViewModel.queueManager.getQueue().isEmpty()) {
                            playerViewModel.queueManager.setQueue(filteredFiles)  // Initialize the queue with sorted files only if it's empty
                        }
                        CompositionLocalProvider(LocalDismissSearch provides {
                            isSearching = false
                        }) {
                            PullToRefreshBox(
                                state = state,
                                isRefreshing = viewModel.isLoading.value,
                                indicator = {
                                    Indicator(
                                        isRefreshing = viewModel.isLoading.value,
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
                                    reloadmlist(playerViewModel, viewModel, context)
                                }
                            ) {
                                MusicList(
                                    musicFiles = filteredFiles,
                                    navController = navController,
                                    playerViewModel = playerViewModel,
                                    listState = lazylistState
                                )
                            }

                        }

                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .zIndex(1f)
                .offset { intOffset }

        ) {
            MiniPlayer(
                playerViewModel = playerViewModel,
                musicViewModel = viewModel,
                navController = navController
            )
        }
    }
}

