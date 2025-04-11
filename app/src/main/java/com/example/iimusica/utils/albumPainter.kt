package com.example.iimusica.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.iimusica.R
import com.example.iimusica.screens.AlbumCache


@Composable
fun albumPainter(musicFile: MusicFile?, context: Context): AsyncImagePainter {
    val cachedAlbumArt = AlbumCache.getCachedAlbumArt(musicFile?.path ?: "")
    val cachedTimestamp = AlbumCache.getCachedTimestamp(musicFile?.path ?: "")
    val currentTime = System.currentTimeMillis()
    val isCacheExpired = cachedTimestamp != null && (currentTime - cachedTimestamp > AlbumCache.CACHE_EXPIRY_TIME)
    val albumArtBitmap = remember(musicFile) {
        if (isCacheExpired || cachedTimestamp == null) {
            musicFile?.let {
                val newAlbumArt = getAlbumArtBitmap(context, it)
                AlbumCache.setAlbumArt(musicFile.path, newAlbumArt)  // Cache it in ViewModel
                AlbumCache.setAlbumArtTimestamp(musicFile.path, currentTime)  // Update timestamp
                newAlbumArt
            }
        } else {
            cachedAlbumArt
        }
    }
    return rememberAsyncImagePainter(
        model = albumArtBitmap ?: R.drawable.default_image
    )
}
