package com.example.iimusica.core.screens


import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.mediacomponents.MusicScreenMainContent
import com.example.iimusica.components.ux.InfoBox
import com.example.iimusica.components.ux.MessageType
import com.example.iimusica.components.mediacomponents.topbars.MusicScreenTopBar
import com.example.iimusica.components.mediacomponents.QueuePanel
import com.example.iimusica.core.viewmodels.MusicViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.core.viewmodels.SharedViewModel
import com.example.iimusica.core.viewmodels.pageToScreenKey
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.types.MusicFile
import com.example.iimusica.utils.fetchers.albumPainter
import com.example.iimusica.utils.fetchers.getMusicFileFromPath
import com.example.iimusica.utils.reloadmlist


@OptIn(UnstableApi::class)
@Composable
fun MusicScreen(
    path: String,
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    var musicFile by remember { mutableStateOf<MusicFile?>(null) }
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val currentPath = playerViewModel.currentPath.value ?: path

    val dragOffsetState = remember { mutableFloatStateOf(0f) }
    val isDragging = remember { mutableStateOf(false) }
    val maxDragPx = with(LocalDensity.current) { 500.dp.toPx() }

    val screenKey = pageToScreenKey(0)
    val state = sharedViewModel.getState(screenKey)

    LaunchedEffect(currentPath) {
        val currentMediaItem =
            playerViewModel.playbackController.exoPlayer!!.currentMediaItem!!.localConfiguration?.uri?.toString()
        if (currentPath != currentMediaItem) {
            playerViewModel.setCurrentPath(currentPath, true)
            if (currentMediaItem == null) {
                playerViewModel.playMusic(currentPath)
            } else {
                with(playerViewModel.queueManager) {
                    setCurrentIndex(
                        updateIndex(
                            currentPath,
                            getQueueWithIDs(),
                            getCurrentIndex()
                        )
                    )
                    setShuffledIndex(
                        updateIndex(
                            currentPath, getShuffledViewWithIDs(), getShuffledIndex()
                        )
                    )
                }
            }
        }
        musicFile = getMusicFileFromPath(context, currentPath.toString())
    }


    val painter = albumPainter(musicFile)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.background)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    isDragging.value = true
                    dragOffsetState.floatValue = (dragOffsetState.floatValue - delta).coerceIn(0f, maxDragPx)
                },
                onDragStopped = {
                    isDragging.value = false
                }
            )

    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MusicScreenTopBar(
                isPlaying = playerViewModel.isPlaying,
                onBackClick = { navController.navigateUp() },
                onNavToQueue = {
                    navController.navigate("queue") {
                        launchSingleTop = true
                    }
                },
                isDescending = state.isDescending,
                selectedSortOption = state.sortOption,
                onSortOptionSelected = {
                    sharedViewModel.updateSort(screenKey, it)
                },
                onReshuffle = { playerViewModel.queueManager.regenerateShuffleOrder() },
                onReloadLocalFiles = {
                    reloadmlist(playerViewModel, musicViewModel, sharedViewModel, context)
                },
                onToggleDescending = { sharedViewModel.toggleDescending(screenKey) },
                snackbarHostState = snackbarHostState
            )

            if (musicFile != null) {

                val layoutModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(vertical = if (isLandscape) 20.dp else 0.dp)


                if (isLandscape) {
                    Row(
                        modifier = layoutModifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        MusicScreenMainContent(
                            isLandscape = true,
                            playerViewModel,
                            musicFile,
                            painter,
                            appColors
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = layoutModifier
                    ) {
                        MusicScreenMainContent(
                            isLandscape = false,
                            playerViewModel,
                            musicFile,
                            painter,
                            appColors
                        )
                    }
                }

            } else {
                InfoBox(
                    message = "Music file was not found",
                    type = MessageType.Error,
                    mainBoxColor = appColors.backgroundDarker
                )
            }
        }
        QueuePanel(
            musicViewModel,
            playerViewModel,
            state,
            navController = navController,
            dragOffsetState = dragOffsetState,
            isDragging = isDragging,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
