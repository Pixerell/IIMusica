package com.example.iimusica.screens


import android.os.Handler
import android.os.Looper
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.rememberNavController
import com.example.iimusica.components.DurationBar
import com.example.iimusica.components.MusicList
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.MusicFile
import com.example.iimusica.utils.getMusicFileFromPath
import com.example.iimusica.utils.parseDuration


@Composable
fun MusicScreen(path: String, playerViewModel: PlayerViewModel) {

    val appColors = LocalAppColors.current
    val context = LocalContext.current
    var currentPosition by remember { mutableLongStateOf(0L) }
    var musicFile by remember { mutableStateOf<MusicFile?>(null) }

    val currentPath = playerViewModel.currentPath.value ?: path
    val isCurrentlyPlaying = currentPath == playerViewModel.currentPath.value

    var isPanelExpanded by remember { mutableStateOf(false) }
    val panelHeight by animateDpAsState(targetValue = if (isPanelExpanded) 400.dp else 50.dp)

    val animatedAccentStart by animateColorAsState(
        targetValue = if (isPanelExpanded) appColors.accentStart else appColors.backgroundDarker,
        animationSpec = tween(durationMillis = 1000)
    )

    val animatedAccentEnd by animateColorAsState(
        targetValue = if (isPanelExpanded) appColors.accentEnd else appColors.backgroundDarker,
        animationSpec = tween(durationMillis = 1000)
    )

    val animatedBackgroundColor = Brush.linearGradient(
        colors = listOf(animatedAccentStart, animatedAccentEnd)
    )

    val exoPlayer = playerViewModel.exoPlayer
    // Play the music whenever the path changes
    LaunchedEffect(currentPath) {
        if (!isCurrentlyPlaying) {
            playerViewModel.setCurrentPath(currentPath)
            val index = playerViewModel.getQueue().indexOfFirst { it.path == currentPath }
            if (index != -1) {
                playerViewModel.setCurrentIndex(index)
            }
            playerViewModel.playMusic(currentPath.toString())
        }
            playerViewModel.exoPlayer.play()
            musicFile = getMusicFileFromPath(context, currentPath.toString())


    }

    // Handler for updating position
    val handler = remember { Handler(Looper.getMainLooper()) }

    DisposableEffect(exoPlayer) {
        val updatePosition = object : Runnable {
            override fun run() {
                currentPosition = exoPlayer.currentPosition
                handler.postDelayed(this, 1000) // Update every 1 second
            }
        }
        handler.post(updatePosition)

        onDispose {
            handler.removeCallbacks(updatePosition)
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Leave this as Top, we'll pin the panel separately
        ) {
            if (musicFile != null) {
                val duration = parseDuration(musicFile!!.duration)

                Text(text = "Now Playing:", color = appColors.font, fontSize = 20.sp)
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
                DurationBar(currentPosition, duration, exoPlayer)

                Row {
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

        // Panel pinned to the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(panelHeight)
                .background(appColors.backgroundDarker)
                .align(Alignment.BottomCenter) // This pins it to the bottom of the screen
                .zIndex(2f)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-24).dp) // Overlap the icon slightly
                    .size(56.dp) // Circle size
                    .clip(CircleShape) // Make it circular
                    .background(
                        appColors.backgroundDarker
                    )
                    .clickable { isPanelExpanded = !isPanelExpanded }
                    .padding(8.dp) // Add padding inside the circle for the icon
                    .zIndex(3f)
            ) {
                Icon(
                    imageVector = if (isPanelExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = if (isPanelExpanded) "Collapse Panel" else "Expand Panel",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp), // Icon size

                    tint = appColors.font
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(panelHeight)
                    .background(animatedBackgroundColor)
                    .drawBehind {
                        drawLine(
                            color = appColors.backgroundDarker, // Border color
                            start = Offset(0f, 0f), // Starting position at the top left
                            end = Offset(size.width, 0f), // Ending position at the top right
                            strokeWidth = 30f // Border width
                        )
                    }
                    .align(Alignment.BottomCenter) // This pins it to the bottom of the screen
                    .zIndex(1f) // Keep it behind the button

            ) {
                if (isPanelExpanded) {
                    MusicList(
                        musicFiles = playerViewModel.getQueue(),
                        navController = rememberNavController(), // Provide the appropriate NavController
                        playerViewModel = playerViewModel
                    )
                }
            }
        }
    }
    }