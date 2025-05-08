package com.example.iimusica.utils

import java.util.Locale

fun formatDuration(duration: Long): String {
    val totalSeconds = duration / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}

fun parseDuration(duration: String): Long {
    val parts = duration.split(":").map { it.toLong() }
    return when (parts.size) {
        3 -> {
            val (hours, minutes, seconds) = parts
            (hours * 3600 + minutes * 60 + seconds) * 1000
        }
        2 -> {
            val (minutes, seconds) = parts
            (minutes * 60 + seconds) * 1000
        }
        else -> throw IllegalArgumentException("Invalid duration format")
    }
}