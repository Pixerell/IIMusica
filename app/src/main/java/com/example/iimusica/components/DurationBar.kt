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
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.screens.PlayerViewModel


@OptIn(UnstableApi::class)
@Composable
fun DurationBar(duration: Long, playerViewModel: PlayerViewModel) {
    val appColors = LocalAppColors.current
    var currentPosition by remember { mutableLongStateOf(0L) }
    var dragging by remember { mutableStateOf(false) }
    // Handler for updating position
    val handler = remember { Handler(Looper.getMainLooper()) }

    //Log.d("DurationBar", "repeat mode ${playerViewModel.exoPlayer.repeatMode}, next song ${playerViewModel.getQueue()}")
    DisposableEffect(playerViewModel.exoPlayer, dragging) {
        val updatePosition = object : Runnable {
            override fun run() {
                if (!dragging) {  // Update only if not dragging
                    Log.d("DurationBar", "current positionplayer ${playerViewModel.exoPlayer.currentPosition} current position $currentPosition duration $duration")

                    if (currentPosition >= duration && !dragging) {
                        Log.d("DurationBar", "play next $currentPosition, queuenext ${playerViewModel.getCurrentIndex()}")
                        playerViewModel.playNext()
                    }
                    currentPosition = playerViewModel.exoPlayer.currentPosition


                }
                handler.postDelayed(this, 200)  // Update position every 200ms
            }
        }
        handler.post(updatePosition)

        onDispose {
            handler.removeCallbacks(updatePosition)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
            .offset(y = (-8).dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatDuration(currentPosition),
            color = appColors.font,
            fontSize = 14.sp
        )
        Text(text = formatDuration(duration), color = appColors.font, fontSize = 14.sp)
    }

    CustomSlider(
        playerViewModel = playerViewModel,
        duration = duration,
        onSeekEnd = { dragging = false },
        currentPosition = currentPosition
    )
}
