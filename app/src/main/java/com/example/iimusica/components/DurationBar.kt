package com.example.iimusica.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.formatDuration
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue



@Composable
fun DurationBar(duration: Long, exoPlayer: ExoPlayer) {
    val appColors = LocalAppColors.current
    var currentPosition by remember { mutableLongStateOf(0L) }
    var dragging by remember { mutableStateOf(false) }
    // Handler for updating position
    val handler = remember { Handler(Looper.getMainLooper()) }

    DisposableEffect(exoPlayer) {
        val updatePosition = object : Runnable {
            override fun run() {
                if (!dragging) {  // Update only if not dragging
                    currentPosition = exoPlayer.currentPosition
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
            .offset(y = -8.dp),
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
        exoPlayer = exoPlayer,
        duration = duration,
        onSeekEnd = { dragging = false },
        currentPosition = currentPosition
    )

    /*
    Slider(
        value = currentPosition.toFloat(),
        onValueChange = { exoPlayer.seekTo(it.toLong()) },
        valueRange = 0f..duration.toFloat(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )

     */
}
