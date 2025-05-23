package com.example.iimusica.utils.fetchers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.net.toUri
import com.example.iimusica.types.SKIP_CHECK_CODE
import java.io.File


fun getAlbumArtBitmap(context: Context, albumId: Long, path: String): Bitmap? {

    val file = File(path)
    if (!file.exists()) {
        Log.e("MusicFiles", "Album art fetch aborted: file does not exist -> $path")
        return null
    }

    // If the albumId is invalid (0), we won't try to fetch the album art URI
    if (albumId <= 0 && albumId != SKIP_CHECK_CODE) {
        Log.d("MusicFiles", "Invalid albumId, skipping content URI fetch.")
        return null
    }

    // Try retrieving embedded album art from the media file, no need to mix albums
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val art = retriever.embeddedPicture
        retriever.release()
        if (art != null) {
            Log.d("MusicFiles", "Got the art from retriever for $albumId and $path")
            return BitmapFactory.decodeByteArray(art, 0, art.size)
        } else {
            Log.d("MusicFiles", "No embedded album art found for $path")
        }
    } catch (e: Exception) {
        Log.e("MusicFiles", "Failed to get album art from file metadata | ${e.message}")
    }

    val albumArtUri = "content://media/external/audio/albumart/$albumId".toUri()

    // Fallback: Try fetching album art from content URI
    try {
        context.contentResolver.openInputStream(albumArtUri)?.use { inputStream ->
            return BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        Log.e("MusicFiles", "Failed to get album art from URI: $albumArtUri | ${e.message}")
    }


    return null
}
