package com.example.iimusica.types

import android.graphics.Bitmap

data class MusicFile(
    val name: String,
    val duration: String,
    val path: String,
    val artist: String,
    val albumArtBitmap: Bitmap?,
    val album: String,
    val albumId: Long,
    val size: Long,
    val dateAdded: Long,

    // Extended Metadata
    val genre: String? = null,
    val year: Int? = null,
    val bitrate: Int? = null,
    val trackNumber: Int? = null,
)

// To solve the problem of uniqueness when duplicate songs with paths are present
data class QueuedMusicFile(
    val musicFile: MusicFile,
    val queueId: Long
)