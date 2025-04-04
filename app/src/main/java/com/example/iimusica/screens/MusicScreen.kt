package com.example.iimusica.screens


import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.iimusica.R
import com.example.iimusica.components.DurationBar
import com.example.iimusica.components.MarqueeText
import com.example.iimusica.components.MusicScreenTopBar
import com.example.iimusica.components.QueuePanel
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.MusicFile
import com.example.iimusica.utils.getAlbumArtBitmap
import com.example.iimusica.utils.getMusicFileFromPath
import com.example.iimusica.utils.parseDuration


@OptIn(UnstableApi::class)
@Composable
fun MusicScreen(path: String, playerViewModel: PlayerViewModel, navController: NavController) {

    val appColors = LocalAppColors.current
    val context = LocalContext.current
    var musicFile by remember { mutableStateOf<MusicFile?>(null) }

    val currentPath = playerViewModel.currentPath.value ?: path
    val isCurrentlyPlaying = currentPath == playerViewModel.currentPath.value
    var isPanelExpanded by remember { mutableStateOf(false) }
    val togglePanelState: (Boolean) -> Unit = { expanded ->
        isPanelExpanded = !isPanelExpanded
    }


    val exoPlayer = playerViewModel.exoPlayer

    LaunchedEffect(currentPath) {
        if (!isCurrentlyPlaying || MediaItem.fromUri(path) != playerViewModel.exoPlayer.currentMediaItem) {
            playerViewModel.setCurrentPath(currentPath)
            Log.d("DurationBar", "New song? ${currentPath}")
            val index = playerViewModel.getQueue().indexOfFirst { it.path == currentPath }
            if (index != -1) {
                playerViewModel.setCurrentIndex(index)
            }
            playerViewModel.playMusic(currentPath.toString())
        }
            playerViewModel.setIsPlaying(true)
            playerViewModel.exoPlayer.playWhenReady = true
            musicFile = getMusicFileFromPath(context, currentPath.toString())

    }

    val albumArtBitmap = remember(musicFile) {
        musicFile?.let { getAlbumArtBitmap(context, it) }
    }

    val painter = rememberAsyncImagePainter(
        model = albumArtBitmap ?: R.drawable.default_image
    )
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
            if (musicFile != null) {
                MusicScreenTopBar(isPlaying = playerViewModel.isPlaying.value,
                    onBackClick = {navController.navigateUp()},
                    onSettingsClick = { Log.d("MusicScreen", "Settings icon clicked") }  )

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Album Art",
                        modifier = Modifier
                            .size(500.dp)
                            .padding(top = 48.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)


                    )


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 32.dp)
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
                    )


                    {
                        IconButton(onClick = { playerViewModel.toggleShuffle() }, modifier = Modifier.weight(1f).size(28.dp)) {
                            Icon(
                                painter  = painterResource( R.drawable.shuffleico),
                                contentDescription = "Shuffle",
                                tint = if (playerViewModel.isShuffleEnabled.value) appColors.active else appColors.icon,
                            )
                        }

                        IconButton(onClick = { playerViewModel.playPrevious() },  modifier = Modifier.weight(1f).size(28.dp)) {
                            Icon(
                                painter  = painterResource( R.drawable.nextico),
                                contentDescription = "Previous",
                                tint = appColors.icon,
                                modifier = Modifier.graphicsLayer(scaleX = -1f)

                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FloatingActionButton(
                                onClick = { playerViewModel.togglePlayPause() },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(80.dp),
                                containerColor = appColors.icon,
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(if (playerViewModel.isPlaying.value) R.drawable.pauseico else R.drawable.playico),
                                        contentDescription = if (playerViewModel.isPlaying.value) "Pause" else "Play",
                                        tint = appColors.active,
                                        modifier = Modifier.size(28.dp)
                                            .then(
                                                if (!playerViewModel.isPlaying.value) {
                                                    Modifier.offset(x = 2.dp)
                                                } else {
                                                    Modifier
                                                }
                                            )
                                    )
                                }
                            }
                        }


                        IconButton(onClick = { playerViewModel.playNext() },  modifier = Modifier.weight(1f).size(28.dp) ) {
                            Icon(
                                painter  = painterResource( R.drawable.nextico),
                                contentDescription = "Next",
                                tint = appColors.icon,

                            )
                        }

                        IconButton(onClick = {playerViewModel.toggleRepeat() },  modifier = Modifier.weight(1f).size(28.dp)) {
                            val repeatIcon = when (exoPlayer.repeatMode) {
                                ExoPlayer.REPEAT_MODE_OFF -> painterResource(R.drawable.repeatico)
                                ExoPlayer.REPEAT_MODE_ALL -> painterResource(R.drawable.repeatico)
                                ExoPlayer.REPEAT_MODE_ONE -> painterResource(R.drawable.repeatsongico)
                                else -> painterResource(R.drawable.repeatico)
                            }

                            Icon(
                                painter = repeatIcon,
                                contentDescription = "Repeat mode",
                                tint = if (playerViewModel.repeatMode.value != ExoPlayer.REPEAT_MODE_OFF) appColors.active else appColors.icon                            )
                        }
                    }
                }


            } else {
                Text(text = "Error: Music file not found", color = appColors.font)
            }
        }
        QueuePanel(playerViewModel, isPanelExpanded, togglePanelState,
            modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
