package com.example.iimusica


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.os.Environment
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
    val album: String
)

fun scanAllFiles(context: Context) {
    // Get the external storage directory (usually for media like music, photos, etc.)
    val externalStorage = Environment.getExternalStorageDirectory()

    // Check if external storage is available
    if (externalStorage.exists()) {
        // Get the root external storage path
        val rootDirectory = externalStorage.absolutePath

        // Scan the entire external storage for media files
        MediaScannerConnection.scanFile(
            context,
            arrayOf(rootDirectory), // Pass the entire external storage directory
            null
        ) { path, uri ->
            Log.d("MusicFiles", "Scanned: $path, URI: $uri")
        }
    } else {
        Log.d("MusicFiles", "External storage not available.")
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



        while (it.moveToNext()) {
            val path = it.getString(pathIndex)
            val relpath = it.getString(relativepathIndex)
            val name = it.getString(nameIndex)
            val duration = it.getLong(durationIndex)
            val artist = it.getString(artistIndex)
            val albumId = it.getLong(albumIdIndex)
            val album = it.getString(albumindex)

            val minutes = (duration / 1000) / 60
            val seconds = (duration / 1000) % 60
            val formattedDuration = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)

            val albumArt = getAlbumArtBitmap(context, albumId, path)

            Log.d("MusicFiles", "Name: $name, Path: $path, Duration: $formattedDuration , relPath: $relpath, albumArtUri $albumArt , albumid $albumId , album $album" )
            musicFiles.add(MusicFile(name, formattedDuration , path, artist, albumArt, album))
        }
    }
    Log.d("MusicFiles", "Total Music Files: ${musicFiles.size}")
    return musicFiles
}
fun getAlbumArtBitmap(context: Context, albumId: Long, filePath: String): Bitmap? {
    // Try to load album art from the system content provider
    val albumArtUri = "content://media/external/audio/albumart/$albumId".toUri()
    Log.d("musicalbum", "HEY brother $albumArtUri")
    try {
        context.contentResolver.openInputStream(albumArtUri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            Log.d("musicalbum", "bitmap question $bitmap")

            if (bitmap != null) {
                return bitmap
            }
        }
    } catch (e: Exception) {
        Log.e("MusicFiles", "Failed to get album art from content URI", e)
    }

    // Fallback: extract embedded album art from the file itself
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)
        val art = retriever.embeddedPicture
        retriever.release()
        Log.d("musicalbum", "EMBEDDEDPICTURE $art")
        if (art != null) {
            return BitmapFactory.decodeByteArray(art, 0, art.size)
        }
    } catch (e: Exception) {
        Log.e("MusicFiles", "Failed to get album art from file metadata", e)
    }

    return null
}
