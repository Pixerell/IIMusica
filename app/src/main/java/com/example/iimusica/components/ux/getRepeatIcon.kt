package com.example.iimusica.components.ux

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.R

fun getRepeatIconResId(repeatMode: Int): Int {
    return when (repeatMode) {
        ExoPlayer.REPEAT_MODE_OFF -> R.drawable.repeatico
        ExoPlayer.REPEAT_MODE_ALL -> R.drawable.repeatqueueico
        ExoPlayer.REPEAT_MODE_ONE -> R.drawable.repeatsongico
        else -> R.drawable.repeatico
    }
}

// For jetpack compose
@Composable
fun getRepeatPainter(repeatMode: Int): Painter {
    return painterResource(id = getRepeatIconResId(repeatMode))
}