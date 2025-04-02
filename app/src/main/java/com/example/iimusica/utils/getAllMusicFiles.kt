package com.example.iimusica.utils


import android.content.Context
import android.database.Cursor
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.util.Log



val projection = arrayOf(
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.DISPLAY_NAME,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.SIZE,
    MediaStore.Audio.Media.DATE_ADDED
)


fun scanAllFiles(context: Context) {
    val externalStorage = context.getExternalFilesDir(null) ?: return
    MediaScannerConnection.scanFile(
        context,
        arrayOf(externalStorage.absolutePath),
        null
    ) { path, uri ->
        Log.d("MusicFiles", "Scanned: $path, URI: $uri")
    }
}


fun getAllMusicFiles(context: Context): List<MusicFile> {
    scanAllFiles(context)
    val musicFiles = mutableListOf<MusicFile>()

    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null, null, null
    )

    cursor?.use {
        while (it.moveToNext()) {
            val musicFile = extractMusicFileFromCursor(it)
            musicFile?.let {
                val albumArt = getAlbumArtBitmap(context, it)
                musicFiles.add(it.copy(albumArtUri = albumArt))
            }
        }
    }
    Log.d("MusicFiles", "Total Music Files: ${musicFiles.size}")
    return musicFiles
}

fun getMusicFileFromPath(context: Context, path: String): MusicFile? {
    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        "${MediaStore.Audio.Media.DATA} = ?",
        arrayOf(path),
        null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            return extractMusicFileFromCursor(it)
        }
    }
    return null
}

fun extractMusicFileFromCursor(cursor: Cursor): MusicFile? {
    val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
    val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
    val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
    val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
    val albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
    val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
    val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
    val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))

    val formattedDuration = formatDuration(duration)
    Log.d("MusicFiles", "Found: $name $artist")

    return MusicFile(name, formattedDuration, path, artist, null, album, albumId, size, dateAdded)
}

