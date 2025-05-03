package com.example.iimusica.utils.fetchers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.iimusica.R
import com.example.iimusica.utils.cachers.AlbumCache
import com.example.iimusica.types.MusicFile


@Composable
fun albumPainter(musicFile: MusicFile?): AsyncImagePainter {
    val context = LocalContext.current

    if (musicFile == null) {
        return rememberAsyncImagePainter(R.drawable.default_image)
    }

    val cachedAlbumArt = AlbumCache.getCachedAlbumArt(musicFile.path)
    val cachedTimestamp = AlbumCache.getCachedTimestamp(musicFile.path)
    val currentTime = System.currentTimeMillis()
    val isCacheExpired =
        cachedTimestamp != null && (currentTime - cachedTimestamp > AlbumCache.CACHE_EXPIRY_TIME)
    val albumArtBitmap = remember(musicFile) {
        if (isCacheExpired || cachedTimestamp == null) {
            musicFile.let {
                val newAlbumArt = getAlbumArtBitmap(context, musicFile.albumId, musicFile.path)
                if (newAlbumArt == null) return@remember null
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
