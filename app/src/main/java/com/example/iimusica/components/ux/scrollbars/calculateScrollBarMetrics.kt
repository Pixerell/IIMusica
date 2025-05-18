package com.example.iimusica.components.ux.scrollbars

data class ScrollBarMetrics(
    val thumbOffsetPx: Float,
    val thumbHeightPx: Float,
    val scrollHeight: Float,
    val contentHeightPx: Float
)

fun calculateScrollBarMetrics(
    scrolledPx: Float,
    scrollHeight: Float,
    contentHeightPx: Float
): ScrollBarMetrics {
    val thumbHeightPx = ((scrollHeight / contentHeightPx) * scrollHeight).coerceIn(0f, scrollHeight * 0.4f)
    val maxScrollPx = (contentHeightPx - scrollHeight).coerceAtLeast(1f)
    val rawThumbOffset = (scrolledPx / maxScrollPx) * (scrollHeight - thumbHeightPx)
    val topOffsetPx = rawThumbOffset.coerceIn(0f, scrollHeight - thumbHeightPx)

    return ScrollBarMetrics(
        thumbOffsetPx = topOffsetPx,
        thumbHeightPx = thumbHeightPx,
        scrollHeight = scrollHeight,
        contentHeightPx = contentHeightPx
    )
}
