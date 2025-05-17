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
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.types.BOTTOM_LIST_PADDING
import com.example.iimusica.types.SCROLLBAR_WIDTH
import com.example.iimusica.types.SCROLL_GESTURE_PADDING
import com.example.iimusica.types.SCROLL_HORIZONTAL_OFFSET
import com.example.iimusica.ui.theme.LocalAppColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun LazyGridScrollBar(
    gridState: LazyGridState,
    width: Int = SCROLLBAR_WIDTH,
    bottomPadding: Int = BOTTOM_LIST_PADDING,
    avgRowHeight: Int,
    columnCount: Int
) {
    val appColors = LocalAppColors.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val horizontalOffset = SCROLL_HORIZONTAL_OFFSET
    val gesturePadding = SCROLL_GESTURE_PADDING.dp

    val scrollHeight by remember(gridState) {
        derivedStateOf {
            gridState.layoutInfo.viewportSize.height.toFloat()
        }
    }

    // im in love with this
    var measuredRowHeightPx by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        if (measuredRowHeightPx == 0f) {
            snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
                .map { items ->
                    if (measuredRowHeightPx == 0f && items.isNotEmpty()) {
                        val firstRow = items.first().index / columnCount
                        val rowItems = items.filter { it.index / columnCount == firstRow }
                        val minY = rowItems.minOf { it.offset.y }
                        val maxY = rowItems.maxOf { it.offset.y + it.size.height }
                        (maxY - minY).toFloat()
                    } else null
                }
                .distinctUntilChanged()
                .collect { measured ->
                    if (measured != null) {
                        measuredRowHeightPx = measured
                    }
                }
        }
    }


    val avgRowHeightPx = if (measuredRowHeightPx > 0f) measuredRowHeightPx
    else with(density) { avgRowHeight.dp.toPx() }

    val bottomPaddingPx = with(density) { bottomPadding.dp.toPx() }

    val totalRowCount by remember(gridState) {
        derivedStateOf {
            val count = gridState.layoutInfo.totalItemsCount
            (count + columnCount - 1) / columnCount // Ceil division
        }
    }

    val scrolledPx by remember(gridState) {
        derivedStateOf {
            val firstIndex = gridState.firstVisibleItemIndex
            val offset = gridState.firstVisibleItemScrollOffset.toFloat()
            val rowIndex = firstIndex / columnCount
            rowIndex * avgRowHeightPx + offset
        }
    }

    val contentHeightPx = totalRowCount * avgRowHeightPx + bottomPaddingPx
    val thumbHeightPx = ((scrollHeight / contentHeightPx) * scrollHeight).coerceIn(0f, scrollHeight * 0.4f)

    val maxScrollPx by remember(contentHeightPx, scrollHeight) {
        derivedStateOf {
            (contentHeightPx - scrollHeight).coerceAtLeast(1f)
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

    var isScrolling by remember { mutableStateOf(false) }
    var fadeOutJob: Job? by remember { mutableStateOf(null) }
    val thumbAlpha by animateFloatAsState(
        targetValue = if (isScrolling) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    LaunchedEffect(scrolledPx) {
        if (scrolledPx > 0) {
            if (!isScrolling) isScrolling = true
        }
        fadeOutJob?.cancel()
        fadeOutJob = scope.launch {
            delay(1000)
            isScrolling = false
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
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
                            gridState.scrollBy(contentScrollDelta)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val scrollFraction = tapOffset.y / scrollHeight
                        val targetRow = (scrollFraction * totalRowCount).toInt()
                        val targetIndex = (targetRow * columnCount).coerceIn(0, gridState.layoutInfo.totalItemsCount - 1)

                        scope.launch {
                            gridState.scrollToItem(targetIndex)
                        }
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
