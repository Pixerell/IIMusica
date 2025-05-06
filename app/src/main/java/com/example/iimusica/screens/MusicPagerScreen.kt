package com.example.iimusica.screens

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.MiniPlayer
import com.example.iimusica.components.buttons.ButtonReload
import com.example.iimusica.components.mediacomponents.MusicTopBar
import com.example.iimusica.types.MusicTopBarActions
import com.example.iimusica.types.PAGE_TITLES
import com.example.iimusica.utils.reloadmlist
import kotlinx.coroutines.launch


@OptIn(UnstableApi::class)
@Composable
fun MusicPagerScreen(
    navController: NavController,
    toggleTheme: () -> Unit,
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    albumViewModel: AlbumViewModel,
    playlistViewModel : PlaylistViewModel,
    sharedSearchViewModel: SharedSearchViewModel,
    context: Context,
    snackbarHostState: SnackbarHostState,
) {

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { PAGE_TITLES.size })
    val coroutineScope = rememberCoroutineScope()
    val musicListState = rememberLazyListState()

    /*
    var isSearching by musicViewModel.isSearching
    val selectedSortOption by musicViewModel.selectedSortOption
    val isDescending by musicViewModel.isDescending
    val filteredFiles by musicViewModel.filteredFiles
    */

    val screenKey = pageToScreenKey(pagerState.currentPage)
    val state = sharedSearchViewModel.getState(screenKey)

    val filteredFiles by musicViewModel.filteredFiles

    val screenHeight =
        with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val targetOffset =
        if (!musicViewModel.isFirstTimeEnteredMusic) Offset(0f, 0f) else Offset(0f, screenHeight)
    val offset by animateOffsetAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 1000, delayMillis = 0),
        label = "MiniPlayerSlideIn"
    )
    val intOffset = IntOffset(offset.x.toInt(), offset.y.toInt())

    var animationComplete by musicViewModel.animationComplete
    val fabOffsetY by animateDpAsState(
        targetValue = if (!musicViewModel.miniPlayerVisible.value) 0.dp else 140.dp,
        animationSpec = tween(durationMillis = 1000),
        label = "FABOffset"
    )

    LaunchedEffect(offset, playerViewModel.isPlaying) {
        if (offset == targetOffset && musicViewModel.isFirstTimeEnteredMusic && playerViewModel.isPlaying) {
            animationComplete = true
        }
    }
    // Reset the flag after animation is complete
    LaunchedEffect(animationComplete) {
        if (animationComplete && musicViewModel.isFirstTimeEnteredMusic) {
            musicViewModel.isFirstTimeEnteredMusic = false
            musicViewModel.miniPlayerVisible.value = true
        }
    }

    LaunchedEffect(state.query, state.sortOption, state.isDescending) {
        musicViewModel.updateFilteredFiles()
    }

    LaunchedEffect(musicViewModel.shouldScrollTop.value) {
        if (musicViewModel.shouldScrollTop.value) {
            musicListState.scrollToItem(0)
            musicViewModel.shouldScrollTop.value = false
        }
    }

    Log.d("statezbar", "Issearching query? ${state.query.isEmpty()}, issearching? ${state.isSearching}")
    Log.d("statezbar", "The sort state? ${state.sortOption} its descent? ${state.isDescending}")

    Scaffold(
        // This line makes the scaffold not respect the navbar at the bottom
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MusicTopBar(
                searchQuery = state.query,
                isSearching = state.isSearching,
                selectedSortOption = state.sortOption,
                isDescending = state.isDescending,
                actions = MusicTopBarActions(
                    onSearchQueryChange = { sharedSearchViewModel.updateQuery(screenKey, it) },
                    onToggleSearch = { sharedSearchViewModel.toggleSearch(screenKey) },
                    onSortOptionSelected = { sharedSearchViewModel.updateSort(screenKey, it) },
                    toggleTheme = toggleTheme,
                    onReloadLocalFiles = { reloadmlist(playerViewModel, musicViewModel, context) },
                    onReshuffle = { playerViewModel.queueManager.regenerateShuffleOrder() }
                ),
                currentPage = pagerState.currentPage,
                onPageSelected = { selectedIndex ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(selectedIndex)
                    }
                },
                onToggleDescending = {
                    sharedSearchViewModel.toggleDescending(screenKey)
                },
                snackbarHostState = snackbarHostState
            )
        },

        floatingActionButton = {
            Box(modifier = Modifier.offset(y = fabOffsetY)) {
                ButtonReload(playerViewModel, musicViewModel, context)
            }
        },
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
        {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            sharedSearchViewModel.disableSearch(screenKey)
                        })
                    }

            ) { page ->
                when (page) {
                    0 -> MusicListScreen(
                        navController = navController,
                        context = context,
                        musicViewModel = musicViewModel,
                        playerViewModel = playerViewModel,
                        musicListState,
                        filteredFiles
                    )

                    1 -> AlbumsScreen(
                        albumViewModel = albumViewModel,
                        navController = navController
                    )
                    2 -> PlaylistsScreen(
                        playlistViewModel = playlistViewModel,
                        navController = navController
                    )
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
                    musicViewModel = musicViewModel,
                    navController = navController
                )
            }
        }
    }

}