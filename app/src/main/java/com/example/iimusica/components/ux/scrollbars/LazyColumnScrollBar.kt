package com.example.iimusica.components.ux.scrollbars


import androidx.annotation.OptIn
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.compose.foundation.lazy.LazyListState
import com.example.iimusica.types.AVERAGE_MUSICITEM_HEIGHT
import com.example.iimusica.types.BOTTOM_LIST_PADDING
import com.example.iimusica.types.SCROLLBAR_WIDTH

@OptIn(UnstableApi::class)
@Composable
fun LazyColumnScrollBar(
    lazyListState: LazyListState,
    width: Int = SCROLLBAR_WIDTH,
    avgItemHeight: Int = AVERAGE_MUSICITEM_HEIGHT,
    bottomPadding: Int = BOTTOM_LIST_PADDING,
) {
    val density = LocalDensity.current

    val avgItemHeightPx = with(density) { avgItemHeight.dp.toPx() }
    val bottomPaddingPx = with(density) { bottomPadding.dp.toPx() }

    val totalItemCount by remember(lazyListState) {
        derivedStateOf { lazyListState.layoutInfo.totalItemsCount.takeIf { it > 0 } ?: 1 }
    }

    val scrollHeight by remember(lazyListState) {
        derivedStateOf { lazyListState.layoutInfo.viewportSize.height.toFloat() }
    }

    val scrolledPx by remember(lazyListState) {
        derivedStateOf {
            val indexOffset = lazyListState.firstVisibleItemIndex * avgItemHeightPx
            val scrollOffset = lazyListState.firstVisibleItemScrollOffset.toFloat()
            indexOffset + scrollOffset
        }
    }

    val contentHeightPx = remember(totalItemCount, scrollHeight) {
        totalItemCount * avgItemHeightPx + bottomPaddingPx
    }

    val metrics = calculateScrollBarMetrics(
        scrolledPx = scrolledPx,
        scrollHeight = scrollHeight,
        contentHeightPx = contentHeightPx
    )

    LazyScrollBar(
        metrics = metrics,
        scrollTrigger = scrolledPx,
        onDrag = { dragAmount ->
            val scrollFraction = dragAmount / scrollHeight
            val scrollByPx = scrollFraction * contentHeightPx
            lazyListState.scrollBy(scrollByPx)
        },
        onTap = { tapOffset ->
            val scrollFraction = tapOffset / scrollHeight
            val targetIndex =
                (scrollFraction * totalItemCount).toInt().coerceIn(0, totalItemCount - 1)
            lazyListState.scrollToItem(targetIndex)
        },
        width = width
    )
}
