package com.example.iimusica.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.net.toUri

fun getAlbumArtBitmap(context: Context, musicFile: MusicFile): Bitmap? {

    // If the albumId is invalid (0), we won't try to fetch the album art URI
    if (musicFile.albumId <= 0) {
        Log.d("MusicFiles", "Invalid albumId, skipping content URI fetch.")
        return null
    }

    val albumArtUri = "content://media/external/audio/albumart/${musicFile.albumId}".toUri()

    // Try fetching album art from content URI
    try {
        context.contentResolver.openInputStream(albumArtUri)?.use { inputStream ->
            return BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        Log.e("MusicFiles", "Failed to get album art from URI: $albumArtUri", e)
    }

    // Fallback: Try retrieving embedded album art from the media file
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, musicFile.path.toUri())
        val art = retriever.embeddedPicture
        retriever.release()
        return if (art != null) BitmapFactory.decodeByteArray(art, 0, art.size) else null
    } catch (e: Exception) {
        Log.e("MusicFiles", "Failed to get album art from file metadata", e)
    }

    return null
}
