package com.example.iimusica.components.mediacomponents

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.components.innerShadow
import com.example.iimusica.types.MusicFile
import com.example.iimusica.components.ux.AudioVisualizerView
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.types.AVERAGE_MUSICITEM_HEIGHT
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.utils.LocalDismissSearch
import com.example.iimusica.utils.fetchers.albumPainter


@OptIn(UnstableApi::class)
@Composable
fun MusicItem(
    music: MusicFile,
    navController: NavController,
    isLastItem: Boolean,
    playerViewModel: PlayerViewModel,
    isCurrentPlaying: Boolean
) {

    val appColors = LocalAppColors.current
    val fontColor = if (isCurrentPlaying) {
        appColors.active
    } else {
        appColors.font
    }

    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val dismissSearch = LocalDismissSearch.current


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .innerShadow(
                shape = RectangleShape,
                color = appColors.font.copy(alpha = 0.25f),
                blur = 8.dp,
                offsetY = 6.dp,
                offsetX = 0.dp,
                spread = 0.dp
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        dismissSearch()
                        if (music.path == playerViewModel.currentPath.value) {
                            playerViewModel.togglePlayPause()
                        } else {
                            playerViewModel.playMusic(music.path)
                            playerViewModel.setCurrentPath(music.path, false)
                        }
                        Log.d("queuemanager", "Current index in queue - ${playerViewModel.queueManager.getCurrentIndex()}")
                        Log.d("queuemanager", "Queue state - ${playerViewModel.queueManager.getQueueWithIDs()}")
                    },
                    onDoubleTap = {
                        dismissSearch()
                        if (currentRoute != null) {
                            playerViewModel.setCurrentPath(music.path, false)
                            navController.navigate("music_detail/${Uri.encode(music.path)}") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
            .padding(vertical = 2.dp)
            .then(
                if (isLastItem) Modifier.shadow(
                    8.dp,
                    shape = RectangleShape,
                    ambientColor = appColors.font,
                    spotColor = appColors.font
                ) else Modifier
            )
            .background(appColors.background),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = albumPainter(music)

        BoxWithConstraints(
            modifier = Modifier
                .size(AVERAGE_MUSICITEM_HEIGHT.dp) // This still defines a fixed max constraint
                .wrapContentSize(Alignment.Center)
        ) {
            val imageModifier = if (music.albumArtBitmap == null) {
                Modifier.size(this.maxWidth * 0.75f) // Smaller for default image
            } else {
                Modifier.size(this.maxWidth) // Full size for other images
            }

            Image(
                painter = painter,
                contentDescription = "Album Art",
                modifier = imageModifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentScale = ContentScale.FillBounds
            )
        }


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)

        ) {

            Text(
                text = music.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = Typography.bodySmall.fontSize,
                    color = fontColor,
                    fontFamily = Typography.bodyLarge.fontFamily
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Text(
                text = music.artist,
                style = TextStyle(
                    fontSize = Typography.bodySmall.fontSize,
                    color = appColors.secondaryFont,
                    fontFamily = Typography.bodySmall.fontFamily
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
        }

        if (playerViewModel.isPlaying && music.path == playerViewModel.currentPath.value) {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)

            ) {
                AudioVisualizerView()
            }
        }

    }
}
