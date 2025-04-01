package com.example.iimusica.components

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.ui.theme.LocalAppColors
@Composable
@OptIn(UnstableApi::class)
fun CustomSlider(
    exoPlayer: ExoPlayer,
    duration: Long,
    onSeekEnd: (Float) -> Unit,
    currentPosition: Long
) {
    val appColors = LocalAppColors.current
    val sliderWidth = remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }

    var position by remember { mutableFloatStateOf(currentPosition.toFloat()) }
    LaunchedEffect(currentPosition) {
        if (!dragging) {
            position = currentPosition.toFloat()
            Log.d("slider", "currentPosition: $currentPosition")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDragEnd = {
                        dragging = false
                        exoPlayer.seekTo(position.toLong())
                        onSeekEnd(position)
                    },
                    onDragCancel = { dragging = false },
                    onDrag = { change, _ ->
                        change.consume()  // Consume the drag event
                        position = (change.position.x / sliderWidth.floatValue) * duration
                    }
                )
            }
            // Tap gesture in its own pointerInput modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { change ->
                        position = (change.x / sliderWidth.floatValue) * duration
                        exoPlayer.seekTo(position.toLong())
                        onSeekEnd(position)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxWidth()) {
            sliderWidth.floatValue = size.width
            val progress = position / duration

            drawLine(
                color = appColors.secondaryFont,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 10f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = appColors.font,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width * progress, size.height / 2),
                strokeWidth = 10f,
                cap = StrokeCap.Round
            )

            drawCircle(
                color = appColors.font,
                radius = 32f,
                center = Offset(size.width * progress, size.height / 2)
            )
        }
    }
}
