package com.example.iimusica.components.ux


import androidx.annotation.OptIn
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.ui.theme.LocalAppColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyListState


@OptIn(UnstableApi::class)
@Composable
fun LazyColumnScrollBar(
    lazyListState: LazyListState, width: Int = 10, avgItemHeight: Int = 85, bottomPadding: Int = 124
) {

    val appColors = LocalAppColors.current
    val density = LocalDensity.current

    val scope = rememberCoroutineScope()
    val horizontalOffset = 40f
    val gesturePadding = 10.dp

    val avgItemHeightPx = with(density) { avgItemHeight.dp.toPx() }
    val bottomPaddingPx = with(density) { bottomPadding.dp.toPx() }

    val totalItemCount by remember(lazyListState) {
        derivedStateOf { lazyListState.layoutInfo.totalItemsCount.takeIf { it > 0 } ?: 1 }
    }
    val scrollHeight by remember(lazyListState) {
        derivedStateOf { lazyListState.layoutInfo.viewportSize.height }
    }
    val contentHeightPx = remember(totalItemCount, scrollHeight) {
        totalItemCount * avgItemHeightPx + bottomPaddingPx
    }
    val thumbHeightPx = remember(contentHeightPx, scrollHeight) {
        ((scrollHeight / contentHeightPx) * scrollHeight).coerceIn(0f, scrollHeight * 0.4f)
    }
    val maxScrollPx by remember(contentHeightPx, scrollHeight) {
        derivedStateOf {
            (contentHeightPx - scrollHeight).coerceAtLeast(1f)
        }
    }

    val scrolledPx by remember(lazyListState) {
        derivedStateOf {
            val firstIndex = lazyListState.firstVisibleItemIndex
            val firstOffset = lazyListState.firstVisibleItemScrollOffset.toFloat()
            firstIndex * avgItemHeightPx + firstOffset
        }
    }


    val rawThumbOffset = (scrolledPx / maxScrollPx) * (scrollHeight - thumbHeightPx)
    val topOffsetPx = rawThumbOffset.coerceIn(0f, scrollHeight - thumbHeightPx)

    val animatedThumbHeight by animateFloatAsState(
        targetValue = thumbHeightPx, animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    val animatedTopOffset by animateFloatAsState(
        targetValue = topOffsetPx, animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    // Animation for fadeins/outs of when you are scrolling
    var isScrolling by remember { mutableStateOf<Boolean>(false) }
    var fadeOutJob: Job? by remember { mutableStateOf(null) }
    val thumbAlpha by animateFloatAsState(
        targetValue = if (isScrolling) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    // Look for changes in scrolling, if you are - no fadeouts. after pause - fadeout
    LaunchedEffect(scrolledPx) {
        if (scrolledPx > 0) {
            if (!isScrolling) {
                isScrolling = true
            }
        }
        fadeOutJob?.cancel()
        fadeOutJob = scope.launch {
            delay(1000)
            isScrolling = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd
    ) {
        Canvas(
            modifier = Modifier
                .width(gesturePadding + width.dp)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()


                        val scrollDelta = dragAmount.y
                        val scrollFraction = scrollDelta / scrollHeight
                        val contentScrollDelta = scrollFraction * contentHeightPx
                        isScrolling = true

                        scope.launch {
                            lazyListState.scrollBy(contentScrollDelta)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val scrollFraction = tapOffset.y / scrollHeight
                        val targetIndex =
                            (scrollFraction * lazyListState.layoutInfo.totalItemsCount).toInt()
                        scope.launch {
                            lazyListState.scrollToItem(targetIndex)
                        }
                    }
                }, onDraw = {
            drawRect(
                color = appColors.activeDarker.copy(alpha = thumbAlpha),
                topLeft = Offset(horizontalOffset, animatedTopOffset),
                size = Size(width.toFloat(), animatedThumbHeight)
            )
        })
    }
}
