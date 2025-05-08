package com.example.iimusica.utils.fetchers


import android.content.Context
import android.database.Cursor
import android.media.MediaScannerConnection
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.iimusica.types.MusicFile
import com.example.iimusica.utils.formatDuration


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
                val albumArt = getAlbumArtBitmap(context, musicFile.albumId, musicFile.path)
                musicFiles.add(it.copy(albumArtBitmap = albumArt))
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
    Log.d("MusicFiles", "Found: $name $artist $album $albumId")

    return MusicFile(name, formattedDuration, path, artist, null, album, albumId, size, dateAdded)
}

@RequiresApi(Build.VERSION_CODES.R)
fun fetchExtendedMetadataForMusicFile(context: Context, musicFile: MusicFile): MusicFile {
    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        arrayOf(
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.BITRATE,
            MediaStore.Audio.Media.TRACK
        ),
        "${MediaStore.Audio.Media.DATA} = ?",
        arrayOf(musicFile.path),
        null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val genre = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE))
            val year = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
            val bitrate = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Media.BITRATE))
            val trackNumber = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))

            return musicFile.copy(
                genre = genre.takeIf { it.isNotEmpty() },
                year = if (year != 0) year else null,
                bitrate = if (bitrate != 0) bitrate else null,
                trackNumber = if (trackNumber != 0) trackNumber else null
            )
        }
    }

    // Return the original music file if extended metadata is not available
    return musicFile
}

