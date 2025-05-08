package com.example.iimusica.screens

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.innerShadow
import com.example.iimusica.components.mediacomponents.MusicList
import com.example.iimusica.components.ux.Loader
import com.example.iimusica.types.Album
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.QUEUE_PANEL_OFFSET
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.utils.formatDuration

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(UnstableApi::class)
@Composable
fun AlbumDetailedScreen(
    albumId: String,
    navController: NavController,
    albumViewModel: AlbumViewModel,
    playerViewModel: PlayerViewModel,
    ) {
    val albumIdLong = albumId.toLongOrNull() ?: return
    val albumState = produceState<Album?>(initialValue = null, albumIdLong) {
        value = albumViewModel.getAlbumById(albumIdLong)
    }
    val album = albumState.value

    val appColors = LocalAppColors.current
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE


    if (album == null) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Loader(modifier = Modifier.padding(bottom = 16.dp))
                Text(
                    text = "Loading...",
                    fontStyle = Typography.bodyLarge.fontStyle,
                    fontSize = Typography.bodyLarge.fontSize,
                    fontWeight = Typography.bodyLarge.fontWeight,
                    color = appColors.secondaryFont,
                )
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {

        BoxWithConstraints(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            val imageSize =
                if (isLandscape) this.maxWidth * 0.25f else this.maxWidth * 0.95f
            album.albumArtBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .apply {
                            if (isLandscape) {
                                height(maxHeight - QUEUE_PANEL_OFFSET)
                            }
                        }
                        .size(imageSize)
                        .align(Alignment.Center)
                )
            }
        }


        Row(
            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

            ) {
            Column {
                Text(
                    text = album.name,
                    fontStyle = Typography.bodyLarge.fontStyle,
                    fontSize = Typography.bodyLarge.fontSize,
                    fontWeight = Typography.headlineLarge.fontWeight,
                    color = appColors.font,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = album.artist,
                    fontStyle = Typography.bodyMedium.fontStyle,
                    fontSize = Typography.bodyMedium.fontSize,
                    color = appColors.secondaryFont,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row() {
                    Text(
                        text = "${album.representativeSong} songs",
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.secondaryFont
                    )

                    Text(
                        text = "${album.songs.size.toString()} songs",
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.secondaryFont
                    )

                    Text(
                        text = albumViewModel.getAlbumStorageSize(album.songs),
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.secondaryFont
                    )
                    Text(
                        text = formatDuration(albumViewModel.getTotalDuration(albumIdLong)),
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.secondaryFont
                    )
                }

            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .innerShadow(
                    shape = RectangleShape,
                    color = appColors.font.copy(alpha = 0.4f),
                    blur = 8.dp,
                    offsetY = 6.dp,
                    offsetX = 0.dp,
                    spread = 0.dp
                )
        ) {
            MusicList(
                musicFiles = album.songs,
                navController = navController,
                playerViewModel = playerViewModel
            )
        }

    }
}