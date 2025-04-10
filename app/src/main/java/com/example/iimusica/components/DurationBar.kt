package com.example.iimusica.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.formatDuration
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import com.example.iimusica.screens.PlaybackController
import com.example.iimusica.screens.PlayerViewModel


@Composable
fun DurationBar(duration: Long, playerViewModel: PlayerViewModel, isMiniPlayer: Boolean = false) {

    val appColors = LocalAppColors.current
    var currentPosition by remember { mutableLongStateOf(0L) }
    var dragging by remember { mutableStateOf(false) }
    var draggingPosition by remember { mutableLongStateOf(0L) }

    DisposableEffect(PlaybackController.getExoPlayer(), duration) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED && PlaybackController.getExoPlayer().currentMediaItem != null) {
                    playerViewModel.playNext()
                }
            }
        }
        PlaybackController.getExoPlayer().addListener(listener)

        onDispose {
            PlaybackController.getExoPlayer().removeListener(listener)
        }
    }

    // Position tracking coroutine
    LaunchedEffect(PlaybackController.getExoPlayer(), duration) {
        while (true) {
            if (!dragging) {
                currentPosition = PlaybackController.getExoPlayer().currentPosition
                if (currentPosition >= duration - 500) {
                    playerViewModel.playNext()
                }
            }
            kotlinx.coroutines.delay(200)
        }
    }

    if (!isMiniPlayer) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .offset(y = (-8).dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = formatDuration(if (dragging) draggingPosition else currentPosition),
                color = appColors.font,
                fontSize = 14.sp
            )
            Text(text = formatDuration(duration), color = appColors.font, fontSize = 14.sp)
        }

    }

    CustomSlider(
        duration = duration,
        currentPosition = currentPosition,
        onDragging = { isDragging, position ->
            dragging = isDragging
            draggingPosition = position
            if (!isDragging) {
                currentPosition = position
            }
        },
        dragging = dragging,
        isMiniPlayer = isMiniPlayer
    )
}
