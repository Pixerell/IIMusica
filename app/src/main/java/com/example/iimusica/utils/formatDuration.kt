package com.example.iimusica.utils

import java.util.Locale

fun formatDuration(duration: Long): String {
    val minutes = (duration / 1000) / 60
    val seconds = (duration / 1000) % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

fun parseDuration(duration: String): Long {
    val parts = duration.split(":")
    val minutes = parts[0].toLong()
    val seconds = parts[1].toLong()
    return (minutes * 60 + seconds) * 1000 // Return duration in milliseconds
}
