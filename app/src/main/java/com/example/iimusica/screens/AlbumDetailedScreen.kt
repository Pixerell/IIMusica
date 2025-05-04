package com.example.iimusica.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@OptIn(UnstableApi::class)
@Composable
fun AlbumDetailedScreen(
    albumId: String,
    navController: NavController,
    albumViewModel: AlbumViewModel
) {
    val albumIdLong = albumId.toLongOrNull() ?: return
    val album = albumViewModel.getAlbumById(albumIdLong) ?: return

    val appColors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 128.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = album.name,
            fontStyle = Typography.bodySmall.fontStyle,
            fontSize = Typography.bodySmall.fontSize,
            color = appColors.secondaryFont,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = album.artist,
            fontStyle = Typography.bodySmall.fontStyle,
            fontSize = Typography.bodySmall.fontSize,
            color = appColors.secondaryFont,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            items(album.songs.size) { index ->
                val song = album.songs[index]
                Text(
                    text = song.name,
                    fontStyle = Typography.bodySmall.fontStyle,
                    fontSize = Typography.bodySmall.fontSize,
                    color = appColors.font,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}