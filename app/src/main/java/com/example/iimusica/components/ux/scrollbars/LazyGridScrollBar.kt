package com.example.iimusica.components.ux.scrollbars

import androidx.annotation.OptIn
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.types.BOTTOM_LIST_PADDING
import com.example.iimusica.types.SCROLLBAR_WIDTH
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(UnstableApi::class)
@Composable
fun LazyGridScrollBar(
    gridState: LazyGridState,
    width: Int = SCROLLBAR_WIDTH,
    bottomPadding: Int = BOTTOM_LIST_PADDING,
    avgRowHeight: Int,
    columnCount: Int
) {
    val density = LocalDensity.current
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

    val metrics = calculateScrollBarMetrics(
        scrolledPx = scrolledPx,
        scrollHeight = scrollHeight,
        contentHeightPx = totalRowCount * avgRowHeightPx + bottomPaddingPx
    )

    LazyScrollBar(
        metrics = metrics,
        width = width,
        scrollTrigger = scrolledPx,
        modifier = Modifier.fillMaxSize(),
        onDrag = { dragAmountY ->
            val scrollFraction = dragAmountY / scrollHeight
            val contentScrollDelta = scrollFraction * metrics.contentHeightPx
            gridState.scrollBy(contentScrollDelta)
        },
        onTap = { tapOffsetY ->
            val scrollFraction = tapOffsetY / scrollHeight
            val targetRow = (scrollFraction * totalRowCount).toInt()
            val targetIndex = (targetRow * columnCount).coerceIn(0, gridState.layoutInfo.totalItemsCount - 1)
            gridState.scrollToItem(targetIndex)
        }
    )
}
