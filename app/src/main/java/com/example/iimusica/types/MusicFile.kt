package com.example.iimusica.types

import android.graphics.Bitmap

data class MusicFile(
    val name: String,
    val duration: String,
    val path: String,
    val artist: String,
    val albumArtUri: Bitmap?,
    val album: String,
    val albumId: Long,
    val size: Long,
    val dateAdded: Long
)