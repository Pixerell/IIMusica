package com.example.iimusica.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.formatDuration

@Composable
fun DurationBar(currentPosition: Long, duration: Long, exoPlayer: ExoPlayer) {
    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatDuration(currentPosition),
            color = appColors.font,
            fontSize = 14.sp
        )
        Text(text = formatDuration(duration), color = appColors.font, fontSize = 14.sp)
    }

    Slider(
        value = currentPosition.toFloat(),
        onValueChange = { exoPlayer.seekTo(it.toLong()) },
        valueRange = 0f..duration.toFloat(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    )
}
