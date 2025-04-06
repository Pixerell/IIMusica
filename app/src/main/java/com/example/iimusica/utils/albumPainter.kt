package com.example.iimusica.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.iimusica.R


@Composable
fun albumPainter(musicFile: MusicFile?, context: Context): AsyncImagePainter {
    val albumArtBitmap = remember(musicFile) {
        musicFile?.let { getAlbumArtBitmap(context, it) }
    }

    return rememberAsyncImagePainter(
        model = albumArtBitmap ?: R.drawable.default_image
    )
}