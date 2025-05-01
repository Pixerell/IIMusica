package com.example.iimusica.components.mediacomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iimusica.types.AlbumSummary
import com.example.iimusica.utils.fetchers.albumPainter

@Composable
fun AlbumItem(summary: AlbumSummary) {

    val painter = albumPainter(summary.representativeSong)


    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.size(128.dp)
    )
    Text(text = summary.name)
}
