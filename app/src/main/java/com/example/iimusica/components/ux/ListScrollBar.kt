package com.example.iimusica.components.ux

import android.R.attr.thumbOffset
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ListScrollBar(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    var trackHeight by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    // Calculate thumb size/offset
    val info = listState.layoutInfo
    val totalCount = info.totalItemsCount.takeIf { it > 0 } ?: return
    val first = info.visibleItemsInfo.firstOrNull()?.index ?: 0
    val visibleCount = info.visibleItemsInfo.size

    // Compute thumb height and offset (simple ratio)
    val thumbHeight = (visibleCount.toFloat() / totalCount) * trackHeight
    var thumbOffset = (first.toFloat() / totalCount) * trackHeight

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(8.dp)
            .onSizeChanged { trackHeight = it.height.toFloat() }
            .background(Color.Gray.copy(alpha = 0.3f))  // track color
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val totalCount = listState.layoutInfo.totalItemsCount
                    if (totalCount <= 0) return@detectDragGestures

                    // Update thumb offset based on drag
                    val newThumbOffset = (thumbOffset + dragAmount.y).coerceIn(0f, trackHeight - thumbHeight)
                    thumbOffset = newThumbOffset

                    // Calculate the scroll position relative to the new thumb offset
                    val scrollOffset = (thumbOffset / trackHeight) * totalCount

                    // Launch coroutine to scroll to item
                    coroutineScope.launch {
                        listState.scrollToItem(scrollOffset.roundToInt())
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(0, thumbOffset.roundToInt()) }
                .height(thumbHeight.dp)
                .fillMaxWidth()
                .background(Color.DarkGray.copy(alpha = 0.7f))

        )
    }
}