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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors

@Composable
@OptIn(UnstableApi::class)
fun CustomSlider(
    playerViewModel: PlayerViewModel,
    duration: Long,
    currentPosition: Long,
    onDragging: (Boolean, Long) -> Unit,
    dragging: Boolean
) {
    val appColors = LocalAppColors.current
    val sliderWidth = remember { mutableFloatStateOf(0f) }

    var position by remember { mutableFloatStateOf(currentPosition.toFloat()) }
    // Reset the position when the duration or currentPosition changes
    LaunchedEffect(currentPosition) {
        if (!dragging) {
            position = currentPosition.toFloat().coerceIn(0f, duration.toFloat())

        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .pointerInput(duration) {
                detectDragGestures(
                    onDragStart = {
                        onDragging(true, position.toLong())
                                  },
                    onDragEnd = {
                        playerViewModel.exoPlayer.seekTo(position.toLong())
                        onDragging(false, position.toLong())

                    },
                    onDragCancel = {
                        onDragging(false, position.toLong()) },
                    onDrag = { change, _ ->
                        change.consume()  // Consume the drag event

                        val newPosition = (change.position.x / sliderWidth.floatValue) * duration
                        position = newPosition.coerceIn(0f, duration.toFloat())
                        onDragging(true, position.toLong())
                    }
                )
            }
            // Tap gesture in its own pointerInput modifier
            .pointerInput(duration) {
                detectTapGestures(
                    onTap = { change ->
                        position = (change.x / sliderWidth.floatValue) * duration
                        playerViewModel.exoPlayer.seekTo(position.toLong())
                        onDragging(false, position.toLong())
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
