package com.example.iimusica.components.ux


import androidx.annotation.OptIn
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.ui.theme.LocalAppColors
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun LazyColumnScrollBar(
    lazyListState: LazyListState,
    width: Int = 10,
) {
    val appColors = LocalAppColors.current
    val density = LocalDensity.current

    val closeItemHeight = with(density) { 83.dp.toPx() }
    val totalItemCount = lazyListState.layoutInfo.totalItemsCount.takeIf { it > 0 } ?: 1
    val viewportHeight = lazyListState.layoutInfo.viewportSize.height
    val bottomPaddingPx = with(density) { 120.dp.toPx() }

    val thumbHeightPx = remember(totalItemCount, viewportHeight) {
        val contentHeight = totalItemCount * closeItemHeight + bottomPaddingPx
        val ratio = viewportHeight / contentHeight
        (ratio * viewportHeight).coerceIn(0f, viewportHeight.toFloat() * 0.4f)
    }


    val topOffset by remember(lazyListState) {
        derivedStateOf {
            val totalCnt = lazyListState.layoutInfo.totalItemsCount.takeIf { it > 0 } ?: 1
            val visibleCnt =
                lazyListState.layoutInfo.visibleItemsInfo.count().takeIf { it > 0 } ?: 1
            val columnHeight = lazyListState.layoutInfo.viewportSize.height
            val firstVisibleIndex = lazyListState.firstVisibleItemIndex
            val scrollItemHeight = (columnHeight.toFloat() / totalCnt)
            val realItemHeight = (columnHeight.toFloat() / visibleCnt)
            val offset = ((firstVisibleIndex) * scrollItemHeight)
            val firstItemOffset =
                lazyListState.firstVisibleItemScrollOffset / realItemHeight * scrollItemHeight

            offset + firstItemOffset
        }
    }

    val scope = rememberCoroutineScope()

    val columnSize by remember(lazyListState) {
        derivedStateOf {
            lazyListState.layoutInfo.viewportSize
        }
    }

    val thumbHeight = thumbHeightPx.coerceIn(0f, columnSize.height.toFloat() * 0.4f)
    val scrollHeight = lazyListState.layoutInfo.viewportSize.height


    val estimatedItemHeight by remember {
        mutableStateOf(51f) // or whatever a typical item height is
    }

    val estimatedThumbHeight = remember(lazyListState) {
        derivedStateOf {
            val totalCount = lazyListState.layoutInfo.totalItemsCount.takeIf { it > 0 } ?: 1
            val viewportHeight = lazyListState.layoutInfo.viewportSize.height

            val totalContentHeight = totalCount * closeItemHeight + bottomPaddingPx
            val thumbRatio = viewportHeight / totalContentHeight

            (thumbRatio * viewportHeight).coerceAtMost(viewportHeight.toFloat())
        }
    }

    val hasStableLayout = remember(lazyListState) {
        derivedStateOf {
            val visibleInfo = lazyListState.layoutInfo.visibleItemsInfo
            val totalItems = lazyListState.layoutInfo.totalItemsCount
            val enoughItems = visibleInfo.size >= 3
            val coversEnough = (visibleInfo.firstOrNull()?.index ?: 0) > 0 &&
                    (visibleInfo.lastOrNull()?.index ?: 0) < totalItems - 1

            enoughItems && coversEnough
        }
    }


    Log.d("scrollershit3", "finalTH - ${hasStableLayout.value}")
    val finalThumbHeight = if (hasStableLayout.value) {
        thumbHeight
    } else {
        estimatedThumbHeight.value.coerceAtMost(scrollHeight.toFloat())
    }

    val finalTopOffset= if (hasStableLayout.value){
        topOffset
    } else {
        val index = lazyListState.firstVisibleItemIndex
        index * estimatedItemHeight
    }

    val animatedThumbHeight by animateFloatAsState(
        targetValue = finalThumbHeight,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "AnimatedThumbHeight"
    )



    val animatedTopOffset by animateFloatAsState(
        targetValue = finalTopOffset,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "AnimatedTopOffset"
    )

    Log.d("scrollershit2", "finalTH - $finalThumbHeight TH - $thumbHeight and ETH ${estimatedThumbHeight.value.coerceAtMost(scrollHeight.toFloat())}")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Canvas(
            modifier = Modifier
                .width(width.dp)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val scrollFraction = tapOffset.y / scrollHeight
                        val targetIndex =
                            (scrollFraction * lazyListState.layoutInfo.totalItemsCount).toInt()
                        scope.launch {
                            lazyListState.scrollToItem(targetIndex)
                        }
                    }
                },
            onDraw = {
                drawRect(
                    color = appColors.activeDarker,
                    topLeft = Offset(16f, animatedTopOffset),
                    size = Size(width.toFloat(), animatedThumbHeight)
                )
            }
        )
    }
}
