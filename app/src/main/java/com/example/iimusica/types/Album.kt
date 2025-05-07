package com.example.iimusica.types

import android.graphics.Bitmap

data class AlbumSummary(
    val name: String?,
    val artist: String?,
    val representativeSong: MusicFile
)

data class Album(
    val albumId : Long,
    val name: String,
    val artist: String,
    val songs: List<MusicFile>,
    val albumArtBitmap: Bitmap? = null
)
