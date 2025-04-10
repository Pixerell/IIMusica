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

    // Try retrieving embedded album art from the media file, no need to mix albums
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(musicFile.path)
        val art = retriever.embeddedPicture
        retriever.release()
        if (art != null) {
            Log.d("MusicFiles", "Got the art from retriever for ${musicFile.name}")
            return BitmapFactory.decodeByteArray(art, 0, art.size)
        } else {
            Log.d("MusicFiles", "No embedded album art found for ${musicFile.name}")
        }
    } catch (e: Exception) {
        Log.e("MusicFiles", "Failed to get album art from file metadata | ${e.message}")
    }

    val albumArtUri = "content://media/external/audio/albumart/${musicFile.albumId}".toUri()

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
