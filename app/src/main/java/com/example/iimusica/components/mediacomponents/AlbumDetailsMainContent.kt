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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.ux.ExpandableText
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
    val density = LocalDensity.current

    val dragOffsetState = remember { mutableFloatStateOf(0f) }
    val dragOffset by dragOffsetState
    val maxDragPx = with(density) { 1000.dp.toPx() }
    val hideImageThreshold = with(density) { -300.dp.toPx() }
    val imageVisible = dragOffset >= hideImageThreshold

    val albumDetailsHeightPercentage by remember {
        derivedStateOf {
            (1f - (-dragOffset / 1000f)).coerceIn(0.18f, 1f)
        }
    }
    val musicListHeightPercentage by remember {
        derivedStateOf {
            (1f - albumDetailsHeightPercentage).coerceIn(0.2f, 1f)
        }
    }

    Column {
        Column(
            modifier = Modifier
                .weight(albumDetailsHeightPercentage)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        dragOffsetState.floatValue =
                            (dragOffsetState.floatValue + delta).coerceIn(-maxDragPx, 0f)
                    }

                )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(bottom = 16.dp)
            ) {
                // weird aah shrinking behaviour
                val baseImageSize = if (isLandscape) this.maxWidth * 0.25f else this.maxWidth * 0.85f
                var shrinkingThreshhold = if (musicListHeightPercentage <= 0.45f) 0.2f else 1f
                val shrinkProgress = (-dragOffset / maxDragPx*shrinkingThreshhold).coerceIn(0f, 1f)
                val animatedImageSize by animateFloatAsState(
                    targetValue = baseImageSize.value * (2.5f - (shrinkProgress * 7f)),
                    label = "ImageSizeAnim"
                )

                album.albumArtBitmap?.let { bitmap ->
                    if (imageVisible) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Album Art",
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(with(density) { animatedImageSize.toDp() })
                                .align(Alignment.Center)
                        )
                    }
                }
            }


            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ExpandableText(
                                text = "${album.representativeSong.bitrate?.div(1000)} kbps",
                                color = appColors.secondaryFont,
                                style = Typography.bodyMedium
                            )
                            ExpandableText(
                                text = album.representativeSong.genre.orEmpty(),
                                color = appColors.secondaryFont,
                                style = Typography.bodyMedium
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ExpandableText(
                                text = "${album.songs.size} ${if (album.songs.size == 1) "song" else "songs"}",
                                color = appColors.font,
                                style = Typography.bodyMedium
                            )
                            ExpandableText(
                                text = (album.representativeSong.year ?: "").toString(),
                                color = appColors.secondaryFont,
                                style = Typography.bodyMedium
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ExpandableText(
                                text = albumViewModel.getAlbumStorageSize(album.songs),
                                color = appColors.secondaryFont,
                                style = Typography.bodyMedium
                            )
                            ExpandableText(
                                text = formatDuration(albumViewModel.getTotalDuration(album.albumId)),
                                color = appColors.secondaryFont,
                                style = Typography.bodyMedium
                            )
                        }
                    }

                }
            }
        }

        Column(
            modifier = Modifier
                .weight(musicListHeightPercentage)
        ) {
            // Draggable handle (border line) placed directly below the album details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(appColors.activeGradient)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            dragOffsetState.floatValue =
                                (dragOffsetState.floatValue + delta).coerceIn(-1000f, 0f)
                        }
                    )
            )


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
}