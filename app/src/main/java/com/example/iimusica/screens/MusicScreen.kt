package com.example.iimusica.screens


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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.iimusica.components.ButtonNext
import com.example.iimusica.components.ButtonPlayPause
import com.example.iimusica.components.ButtonPrevious
import com.example.iimusica.components.ButtonRepeat
import com.example.iimusica.components.ButtonShuffle
import com.example.iimusica.components.DurationBar
import com.example.iimusica.components.InfoBox
import com.example.iimusica.components.MarqueeText
import com.example.iimusica.components.MessageType
import com.example.iimusica.components.MusicScreenTopBar
import com.example.iimusica.components.QueuePanel
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.MusicFile
import com.example.iimusica.utils.albumPainter
import com.example.iimusica.utils.getMusicFileFromPath
import com.example.iimusica.utils.parseDuration


@Composable
fun MusicScreen(path: String, playerViewModel: PlayerViewModel, navController: NavController) {

    val appColors = LocalAppColors.current
    val context = LocalContext.current
    var musicFile by remember { mutableStateOf<MusicFile?>(null) }

    val currentPath = playerViewModel.currentPath.value ?: path
    var isPanelExpanded by remember { mutableStateOf(false) }
    val togglePanelState: (Boolean) -> Unit = { expanded ->
        isPanelExpanded = !isPanelExpanded
    }

    LaunchedEffect(currentPath) {
        val currentMediaItem = PlaybackController.getExoPlayer().currentMediaItem?.localConfiguration?.uri?.toString()
        if (currentPath != currentMediaItem ) {
            playerViewModel.setCurrentPath(currentPath, true)
            if (currentMediaItem == null) {
                playerViewModel.playMusic(currentPath)
            }
            else {
                with(playerViewModel.queueManager) {
                    setCurrentIndex(updateIndex(currentPath, getQueue(), getCurrentIndex()))
                    setShuffledIndex(updateIndex(currentPath, getShuffledQueue(), getShuffledIndex()))
                }
            }
        }
        musicFile = getMusicFileFromPath(context, currentPath.toString())
    }


    val painter =  albumPainter(musicFile, context)

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
            modifier = Modifier
                .fillMaxSize(),
        ) {
            MusicScreenTopBar(isPlaying = playerViewModel.isPlaying.value,
                onBackClick = {navController.navigateUp()},
                onSettingsClick = { }  )

            if (musicFile != null) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp)
                    ) {
                        val imageSize = this.maxWidth * 0.95f

                        Image(
                            painter = painter,
                            contentDescription = "Album Art",
                            modifier = Modifier
                                .size(imageSize)
                                .align(Alignment.Center)
                        )
                    }


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 64.dp)
                    ) {

                        MarqueeText(
                            text = musicFile!!.name,
                            modifier = Modifier.padding(horizontal = 16.dp))

                        Text(
                            text = "by ${musicFile!!.artist}",
                            color = appColors.secondaryFont,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    DurationBar(duration = parseDuration((musicFile?.duration ?: 0L).toString()), playerViewModel)

                    Row(
                        modifier = Modifier
                            .padding(vertical = 32.dp)
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = (Alignment.CenterVertically)
                    ) {
                        ButtonShuffle(playerViewModel, modifier = Modifier.weight(1f).size(28.dp))
                        ButtonPrevious(playerViewModel, modifier = Modifier.weight(1f).size(28.dp))
                        ButtonPlayPause(playerViewModel)
                        ButtonNext(playerViewModel, modifier = Modifier.weight(1f).size(28.dp))
                        ButtonRepeat(playerViewModel, modifier = Modifier.weight(1f).size(28.dp))
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
        QueuePanel(playerViewModel, isPanelExpanded, togglePanelState,
            modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
