package com.example.iimusica.player


import androidx.media3.exoplayer.ExoPlayer

fun navigateToIndex(
    isNext: Boolean,
    queueSize: Int,
    currentIndex: Int,
    repeatMode: Int
): Int {
    if (queueSize <= 0) return -1

    return when (repeatMode) {
        ExoPlayer.REPEAT_MODE_ONE -> currentIndex
        ExoPlayer.REPEAT_MODE_ALL -> {
            if (isNext) {
                (currentIndex + 1) % queueSize
            } else {
                (currentIndex - 1 + queueSize) % queueSize
            }
        }
        ExoPlayer.REPEAT_MODE_OFF -> {
            if (isNext) {
                if (currentIndex < queueSize - 1) currentIndex + 1 else -1
            } else {
                if (currentIndex > 0) currentIndex - 1 else -1
            }
        }
        else -> -1
    }
}

