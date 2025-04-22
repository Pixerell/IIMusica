package com.example.iimusica.screens


import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.buttons.ButtonNext
import com.example.iimusica.components.buttons.ButtonPlayPause
import com.example.iimusica.components.buttons.ButtonPrevious
import com.example.iimusica.components.buttons.ButtonRepeat
import com.example.iimusica.components.buttons.ButtonShuffle
import com.example.iimusica.components.mediacomponents.DurationBar
import com.example.iimusica.components.ux.InfoBox
import com.example.iimusica.components.ux.MarqueeText
import com.example.iimusica.components.ux.MessageType
import com.example.iimusica.components.mediacomponents.MusicScreenTopBar
import com.example.iimusica.components.mediacomponents.QueuePanel
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.types.MusicFile
import com.example.iimusica.ui.theme.QUEUE_PANEL_OFFSET
import com.example.iimusica.utils.fetchers.albumPainter
import com.example.iimusica.utils.fetchers.getMusicFileFromPath
import com.example.iimusica.utils.parseDuration


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
                val content: @Composable () -> Unit = {
                    BoxWithConstraints(
                        modifier = Modifier
                            .padding(
                                vertical = if (isLandscape) 24.dp else 64.dp
                            )
                            .padding(end = if (isLandscape) 32.dp else 0.dp)

                    ) {
                        val imageSize =
                            if (isLandscape) this.maxWidth * 0.25f else this.maxWidth * 0.95f
                        Image(
                            painter = painter,
                            contentDescription = "Album Art",
                            modifier = Modifier
                                .apply {
                                    if (isLandscape) {
                                        height(maxHeight - QUEUE_PANEL_OFFSET)
                                    }
                                }
                                .size(imageSize)
                                .align(Alignment.Center))
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = if (isLandscape) 24.dp else 0.dp)
                    ) {
                        MarqueeText(
                            text = musicFile!!.name, modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Text(
                            text = "by ${musicFile!!.artist}",
                            color = appColors.secondaryFont,
                            fontSize = 18.sp
                        )

                        if (!isLandscape) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        DurationBar(
                            duration = parseDuration((musicFile?.duration ?: 0L).toString()),
                            playerViewModel = playerViewModel
                        )

                        Row(
                            modifier = Modifier
                                .padding(vertical = if (isLandscape) 20.dp else 32.dp)
                                .height(IntrinsicSize.Min)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ButtonShuffle(
                                playerViewModel, modifier = Modifier
                                    .weight(1f)
                                    .size(28.dp)
                            )
                            ButtonPrevious(
                                playerViewModel, modifier = Modifier
                                    .weight(1f)
                                    .size(28.dp)
                            )
                            ButtonPlayPause(playerViewModel)
                            ButtonNext(
                                playerViewModel, modifier = Modifier
                                    .weight(1f)
                                    .size(28.dp)
                            )
                            ButtonRepeat(
                                playerViewModel, modifier = Modifier
                                    .weight(1f)
                                    .size(28.dp)
                            )
                        }
                    }
                }

                if (isLandscape) {
                    Row(
                        modifier = layoutModifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        content()
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = layoutModifier
                    ) {
                        content()
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
