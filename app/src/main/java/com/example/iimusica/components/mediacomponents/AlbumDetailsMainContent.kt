package com.example.iimusica.components.mediacomponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import com.example.iimusica.components.innerShadow
import com.example.iimusica.core.viewmodels.AlbumViewModel
import com.example.iimusica.types.Album
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.utils.formatDuration
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(UnstableApi::class)
@Composable
fun AlbumDetailsMainContent(
    isLandscape: Boolean,
    playerViewModel: PlayerViewModel,
    albumViewModel: AlbumViewModel,
    album: Album,
    navController: NavController,
) {
    val appColors = LocalAppColors.current

    album.representativeSong.year
    BoxWithConstraints(
        modifier = Modifier
            .padding(top = 32.dp)
            .padding(bottom=16.dp)
    ) {
        val imageSize =
            if (isLandscape) this.maxWidth * 0.25f else this.maxWidth * 0.85f
        album.albumArtBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Album Art",
                modifier = Modifier
                    .fillMaxWidth(1f)

                    .size(imageSize)
                    .align(Alignment.Center)

            )
        }
    }


    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${album.representativeSong.bitrate?.div(1000)} kbps",
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.secondaryFont
                    )
                    Text(
                        text = "${album.representativeSong.genre}",
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.secondaryFont
                    )

                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${album.songs.size} songs",
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.font
                    )
                    Text(
                        text = "${album.representativeSong.year}",
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.font
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = albumViewModel.getAlbumStorageSize(album.songs),
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.secondaryFont
                    )
                    Text(
                        text = formatDuration(albumViewModel.getTotalDuration(album.albumId)),
                        fontStyle = Typography.bodyMedium.fontStyle,
                        fontSize = Typography.bodyMedium.fontSize,
                        color = appColors.secondaryFont
                    )
                }
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