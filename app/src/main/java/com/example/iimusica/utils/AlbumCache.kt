package com.example.iimusica.utils

import android.graphics.Bitmap
import java.util.concurrent.TimeUnit

object AlbumCache {

    private const val MAX_CACHE_SIZE = 30
    val CACHE_EXPIRY_TIME = TimeUnit.MINUTES.toMillis(60)

    private val albumArtCache =
        object : LinkedHashMap<String, Bitmap?>(MAX_CACHE_SIZE, 0.75f, true) {
            override fun removeEldestEntry(eldest: Map.Entry<String, Bitmap?>): Boolean {
                return size > MAX_CACHE_SIZE
            }
        }

    private val albumArtTimestamps =
        object : LinkedHashMap<String, Long>(MAX_CACHE_SIZE, 0.75f, true) {
            override fun removeEldestEntry(eldest: Map.Entry<String, Long>): Boolean {
                return size > MAX_CACHE_SIZE
            }
        }

    fun getCachedAlbumArt(path: String): Bitmap? {
        return albumArtCache[path]
    }

    fun setAlbumArt(path: String, art: Bitmap?) {
        albumArtCache[path] = art
    }

    fun getCachedTimestamp(path: String): Long? {
        return albumArtTimestamps[path]
    }

    fun setAlbumArtTimestamp(path: String, timestamp: Long) {
        albumArtTimestamps[path] = timestamp
    }
}