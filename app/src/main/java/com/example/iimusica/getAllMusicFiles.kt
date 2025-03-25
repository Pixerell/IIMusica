package com.example.iimusica


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.util.Log
import java.util.Locale
import androidx.core.net.toUri

data class MusicFile(
    val name: String,
    val duration: String,
    val path: String,
    val artist: String,
    val albumArtUri: Bitmap?,
    val album: String,
    val size: Long,
    val dateAdded: Long
)

fun scanAllFiles(context: Context) {
    // You can trigger the scan only once at app startup or when needed.
    val externalStorage = context.getExternalFilesDir(null) ?: return

    // Scan the specified directory (you can scan individual files or directories)
    MediaScannerConnection.scanFile(
        context,
        arrayOf(externalStorage.absolutePath), // Specify the path to scan
        null // Mime types (null to scan all types)
    ) { path, uri ->
        Log.d("MusicFiles", "Scanned: $path, URI: $uri")
    }
}


fun getAllMusicFiles(context: Context): List<MusicFile> {
    scanAllFiles(context)
    val musicFiles = mutableListOf<MusicFile>()
    val projection = arrayOf(
        MediaStore.Audio.Media.DATA,  // Full file path
        MediaStore.Audio.Media.RELATIVE_PATH ,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID, // Added album ID to get album artwork
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATE_ADDED
        )

    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null, null, null
    )

    cursor?.use {
        val pathIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)  // Full path
        val relativepathIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH )
        val nameIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumIdIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val albumindex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        val dateAddedIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)




        while (it.moveToNext()) {
            val path = it.getString(pathIndex)
            val relpath = it.getString(relativepathIndex)
            val name = it.getString(nameIndex)
            val duration = it.getLong(durationIndex)
            val artist = it.getString(artistIndex)
            val albumId = it.getLong(albumIdIndex)
            val album = it.getString(albumindex)
            val size = it.getLong(sizeIndex)
            val dateAdded = it.getLong(dateAddedIndex)

            val minutes = (duration / 1000) / 60
            val seconds = (duration / 1000) % 60
            val formattedDuration = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)

            val albumArt = getAlbumArtBitmap(context, albumId, path)

            Log.d("MusicFiles", "Name: $name, Path: $path, Duration: $formattedDuration , relPath: $relpath, albumArtUri $albumArt , albumid $albumId , album $album" )
            musicFiles.add(MusicFile(name, formattedDuration , path, artist, albumArt, album, size, dateAdded))
        }
    }
    Log.d("MusicFiles", "Total Music Files: ${musicFiles.size}")
    return musicFiles
}

fun getAlbumArtBitmap(context: Context, albumId: Long, filePath: String): Bitmap? {
    // Check cache first (assuming you add a bitmap cache implementation here)
    val albumArtUri = "content://media/external/audio/albumart/$albumId".toUri()

    try {
        // Attempt to fetch from the content provider (URI-based)
        context.contentResolver.openInputStream(albumArtUri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                return bitmap
            }
        }
    } catch (e: Exception) {
        Log.e("MusicFiles", "Failed to get album art from content URI", e)
    }

    // Fallback: Try embedded art from the media file
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)
        val art = retriever.embeddedPicture
        retriever.release()
        if (art != null) {
            return BitmapFactory.decodeByteArray(art, 0, art.size)
        }
    } catch (e: Exception) {
        Log.e("MusicFiles", "Failed to get album art from file metadata", e)
    }

    return null
}