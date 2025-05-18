package com.example.iimusica.components.ux.scrollbars


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.iimusica.types.SCROLLBAR_WIDTH
import com.example.iimusica.types.SCROLL_GESTURE_PADDING
import com.example.iimusica.types.SCROLL_HORIZONTAL_OFFSET
import com.example.iimusica.ui.theme.LocalAppColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LazyScrollBar(
    modifier: Modifier = Modifier,
    metrics: ScrollBarMetrics,
    onDrag: suspend (Float) -> Unit,
    onTap: suspend (Float) -> Unit,
    width: Int = SCROLLBAR_WIDTH,
    scrollTrigger: Float,
) {

    val appColors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    val horizontalOffset = SCROLL_HORIZONTAL_OFFSET
    val gesturePadding = SCROLL_GESTURE_PADDING.dp

    val animatedThumbHeight by animateFloatAsState(
        targetValue = metrics.thumbHeightPx,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    val animatedTopOffset by animateFloatAsState(
        targetValue = metrics.thumbOffsetPx,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    var isScrolling by remember { mutableStateOf(false) }
    var fadeOutJob by remember { mutableStateOf<Job?>(null) }

    val thumbAlpha by animateFloatAsState(
        targetValue = if (isScrolling) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    LaunchedEffect(scrollTrigger) {
        if (!isScrolling) isScrolling = true
        fadeOutJob?.cancel()
        fadeOutJob = scope.launch {
            delay(1000)
            isScrolling = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Canvas(
            modifier = Modifier
                .width(gesturePadding + width.dp)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        scope.launch { onDrag(dragAmount.y) }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        scope.launch { onTap(offset.y) }
                    }
                },
            onDraw = {
                drawRect(
                    color = appColors.activeDarker.copy(alpha = thumbAlpha),
                    topLeft = Offset(horizontalOffset, animatedTopOffset),
                    size = Size(width.toFloat(), animatedThumbHeight)
                )
            }
        )
    }
}
