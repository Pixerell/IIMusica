package com.example.iimusica.screens


import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.iimusica.R
import com.example.iimusica.components.DurationBar
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
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (musicFile != null) {
                val duration = parseDuration(musicFile!!.duration)
                MusicScreenTopBar(isPlaying = playerViewModel.isPlaying.value,  onBackClick = {navController.popBackStack()}, onSettingsClick = { Log.d("MusicScreen", "Settings icon clicked") }  )


                    Image(
                        painter = painter,
                        contentDescription = "Album Art",
                        modifier = Modifier
                            .size(350.dp)
                            .padding(top = 32.dp)
                            .align(Alignment.CenterHorizontally)


                    )


                Text(
                    text = musicFile!!.name,
                    color = appColors.font,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "by ${musicFile!!.artist}",
                    color = appColors.secondaryFont,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
                DurationBar(duration, exoPlayer)

                Row (
                    modifier = Modifier.padding(vertical = 32.dp)
                ) {
                    Button(onClick = { playerViewModel.playPrevious() }) { Text("Previous") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { playerViewModel.togglePlayPause() }) { Text("Play/Pause") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { playerViewModel.playNext() }) { Text("Next") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { exoPlayer.seekTo(0); exoPlayer.play() }) { Text("Restart") }
                }

            } else {
                Text(text = "Error: Music file not found", color = appColors.font)
            }
        }
        QueuePanel(playerViewModel, isPanelExpanded, togglePanelState,
            modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
