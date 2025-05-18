package com.example.iimusica.core.screens

import android.content.Context
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.MiniPlayer
import com.example.iimusica.components.buttons.ButtonReload
import com.example.iimusica.components.mediacomponents.topbars.MusicTopBar
import com.example.iimusica.components.ux.animations.rememberMiniPlayerAnimation
import com.example.iimusica.core.viewmodels.AlbumViewModel
import com.example.iimusica.core.viewmodels.MusicViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.core.viewmodels.PlaylistViewModel
import com.example.iimusica.core.viewmodels.SharedViewModel
import com.example.iimusica.core.viewmodels.pageToScreenKey
import com.example.iimusica.types.MusicTopBarActions
import com.example.iimusica.types.PAGE_TITLES
import com.example.iimusica.utils.reloadmlist
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(UnstableApi::class)
@Composable
fun MusicPagerScreen(
    navController: NavController,
    toggleTheme: () -> Unit,
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    albumViewModel: AlbumViewModel,
    playlistViewModel: PlaylistViewModel,
    sharedViewModel: SharedViewModel,
    context: Context,
    snackbarHostState: SnackbarHostState,
) {

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { PAGE_TITLES.size })
    val coroutineScope = rememberCoroutineScope()
    val screenKey = pageToScreenKey(pagerState.currentPage)
    val state = sharedViewModel.getState(screenKey)

    val animationState = rememberMiniPlayerAnimation(
        isFirstTimeEntered = sharedViewModel.isFirstTimeEnteredMusic,
        isPlaying = playerViewModel.isPlaying,
        animationComplete = sharedViewModel.animationComplete,
        miniPlayerVisible = sharedViewModel.miniPlayerVisible
    )

    if (animationState.offset == IntOffset.Zero && sharedViewModel.isFirstTimeEnteredMusic) {
        sharedViewModel.isFirstTimeEnteredMusic = false
    }

    LaunchedEffect(state.query, state.sortOption, state.isDescending) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                val state = sharedViewModel.getState(pageToScreenKey(page))
                when (page) {
                    0 -> musicViewModel.updateFilteredFiles(state)
                    1 -> albumViewModel.updateFilteredAlbums(state)
                    2 -> playlistViewModel.updateFilteredPlaylists(state)
                }
            }
    }

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
                    onSearchQueryChange = { sharedViewModel.updateQuery(screenKey, it) },
                    onToggleSearch = { sharedViewModel.toggleSearch(screenKey) },
                    onSortOptionSelected = { sharedViewModel.updateSort(screenKey, it) },
                    toggleTheme = toggleTheme,
                    onReloadLocalFiles = {
                        reloadmlist(playerViewModel, musicViewModel, sharedViewModel, context)
                    },
                    onReshuffle = { playerViewModel.queueManager.regenerateShuffleOrder() }
                ),
                currentPage = pagerState.currentPage,
                onPageSelected = { selectedIndex ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(selectedIndex)
                    }
                },
                onToggleDescending = {
                    sharedViewModel.toggleDescending(screenKey)
                },
                onNavToQueue = {     navController.navigate("queue") {
                    launchSingleTop = true
                }},
                snackbarHostState = snackbarHostState
            )
        },

        floatingActionButton = {
            Box(modifier = Modifier.offset(y = animationState.fabOffset)) {
                ButtonReload(playerViewModel, musicViewModel, sharedViewModel, context)
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
                            sharedViewModel.disableSearch(screenKey)
                        })
                    }

            ) { page ->
                when (page) {
                    0 -> MusicListScreen(
                        navController = navController,
                        context = context,
                        musicViewModel = musicViewModel,
                        playerViewModel = playerViewModel,
                        sharedViewModel = sharedViewModel
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
                    .zIndex(111f)
                    .offset { animationState.offset }
            )
             {
                MiniPlayer(
                    playerViewModel = playerViewModel,
                    isMiniPlayerVisible = sharedViewModel.miniPlayerVisible.value,
                    onToggleMiniPlayerVisibility = {
                        sharedViewModel.toggleMiniPlayerVisibility()
                    },
                    currentMusic = musicViewModel.getMusicFileByPath(playerViewModel.currentPath.value.toString()),
                    navController = navController
                )
            }
        }
    }

}