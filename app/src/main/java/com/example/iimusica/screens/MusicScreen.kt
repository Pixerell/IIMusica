package com.example.iimusica.screens


import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.mediacomponents.MusicScreenMainContent
import com.example.iimusica.components.ux.InfoBox
import com.example.iimusica.components.ux.MessageType
import com.example.iimusica.components.mediacomponents.MusicScreenTopBar
import com.example.iimusica.components.mediacomponents.QueuePanel
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.types.MusicFile
import com.example.iimusica.utils.fetchers.albumPainter
import com.example.iimusica.utils.fetchers.getMusicFileFromPath


@OptIn(UnstableApi::class)
@Composable
fun MusicScreen(path: String, playerViewModel: PlayerViewModel, navController: NavController) {
    var musicFile by remember { mutableStateOf<MusicFile?>(null) }
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val currentPath = playerViewModel.currentPath.value ?: path
    var isPanelExpanded by remember { mutableStateOf(false) }
    val togglePanelState: (Boolean) -> Unit = { expanded ->
        isPanelExpanded = !isPanelExpanded
    }

    LaunchedEffect(currentPath) {
        val currentMediaItem =
            playerViewModel.playbackController.exoPlayer!!.currentMediaItem!!.localConfiguration?.uri?.toString()
        if (currentPath != currentMediaItem) {
            playerViewModel.setCurrentPath(currentPath, true)
            if (currentMediaItem == null) {
                playerViewModel.playMusic(currentPath)
            } else {
                with(playerViewModel.queueManager) {
                    setCurrentIndex(updateIndex(currentPath, getQueue(), getCurrentIndex()))
                    setShuffledIndex(
                        updateIndex(
                            currentPath, getShuffledView(), getShuffledIndex()
                        )
                    )
                }
            }
        }
        musicFile = getMusicFileFromPath(context, currentPath.toString())
    }


    val painter = albumPainter(musicFile, context)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.background)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < 0) { // Swipe up
                        isPanelExpanded = true
                    } else if (dragAmount > 0) { // Swipe down
                        isPanelExpanded = false
                    }
                }
            }

    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MusicScreenTopBar(
                isPlaying = playerViewModel.isPlaying,
                onBackClick = { navController.navigateUp() },
                onSettingsClick = { })

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
                        MusicScreenMainContent(isLandscape = true, playerViewModel, musicFile, painter, appColors)
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = layoutModifier
                    ) {
                        MusicScreenMainContent(isLandscape = false, playerViewModel, musicFile, painter, appColors)
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
            playerViewModel,
            isPanelExpanded,
            togglePanelState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
