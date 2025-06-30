package com.example.iimusica.components.mediacomponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import com.example.iimusica.components.innerShadow
import com.example.iimusica.core.viewmodels.AlbumViewModel
import com.example.iimusica.types.Album
import com.example.iimusica.utils.formatDuration
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.R
import com.example.iimusica.components.buttons.ButtonPlayPause
import com.example.iimusica.components.ux.animations.rememberAnimatedGradient
import com.example.iimusica.components.ux.animations.rememberRotationAnimation
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.types.ALBUM_DETAILS_MINIMUM_MUSIC_LIST_HEIGHT
import com.example.iimusica.types.ANIM_SPEED_MEDIUM
import com.example.iimusica.types.ANIM_SPEED_SHORT
import com.example.iimusica.types.ANIM_SPEED_TINY
import com.example.iimusica.types.ANIM_SPEED_VERYSHORT
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

    val bitrateKbps = "${album.representativeSong.bitrate?.div(1000)} kbps"
    val genre = album.representativeSong.genre.orEmpty()
    val songCountText = "${album.songs.size} ${if (album.songs.size == 1) "song" else "songs"}"
    val year = (album.representativeSong.year ?: "").toString()
    val storageSize = albumViewModel.getAlbumStorageSize(album.songs)
    val totalDuration = formatDuration(albumViewModel.getTotalDuration(album.albumId))

    val isDragging = remember { mutableStateOf(false) }
    val dragOffsetState = remember { mutableFloatStateOf(0f) }
    val dragOffset by dragOffsetState
    val maxDragPx = with(density) { 1000.dp.toPx() }
    val hideImageThreshold = with(density) { -300.dp.toPx() }
    val imageVisible = dragOffset >= hideImageThreshold

    val currentCollectionId by playerViewModel.currentCollectionID.collectAsState()
    val isSameAlbum = currentCollectionId == album.albumId

    val albumDetailsHeightPercentage by remember {
        derivedStateOf {
            (1f - (-dragOffset / 1000f)).coerceIn(0.18f, 1f)
        }
    }
    val musicListHeightPercentage by remember {
        derivedStateOf {
            (1f - albumDetailsHeightPercentage).coerceIn(ALBUM_DETAILS_MINIMUM_MUSIC_LIST_HEIGHT, 1f)
        }
    }

    val animatedBackgroundColor = rememberAnimatedGradient(
        isExpanded = isDragging.value,
        expandedColors = listOf(appColors.accentStart, appColors.accentEnd),
        collapsedColors = listOf(appColors.background),
        customDurations = listOf(ANIM_SPEED_TINY, ANIM_SPEED_SHORT)
    )

    val animatedBackgroundGradient = rememberAnimatedGradient(
        isExpanded = isDragging.value,
        expandedColors = listOf(appColors.activeStart, appColors.activeEnd),
        collapsedColors = listOf(appColors.background),
        customDurations = listOf(ANIM_SPEED_VERYSHORT, ANIM_SPEED_MEDIUM)
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isDragging.value) 2.dp else 0.dp
    )

    val animatedIconRotation =
        rememberRotationAnimation(
            imageVisible,
            expandedRotation = 0f,
            collapsedRotation = 180f,
            durationMillis = ANIM_SPEED_SHORT
        )

    Box {
        Column {
            Column(
                modifier = Modifier
                    .weight(albumDetailsHeightPercentage)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            isDragging.value = true
                            dragOffsetState.floatValue =
                                (dragOffsetState.floatValue + delta).coerceIn(-maxDragPx, 0f)
                        },
                        onDragStopped = {
                            isDragging.value = false
                        }
                    )
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(bottom = 16.dp)
                ) {
                    // weird aah shrinking behaviour
                    val baseImageSize =
                        if (isLandscape) this.maxWidth * 0.25f else this.maxWidth * 0.85f
                    var shrinkingThreshhold = if (musicListHeightPercentage <= 0.45f) 0.2f else 1f
                    val shrinkProgress =
                        (-dragOffset / maxDragPx * shrinkingThreshhold).coerceIn(0f, 1f)
                    val animatedImageSize by animateFloatAsState(
                        targetValue = baseImageSize.value * (2.5f - (shrinkProgress * 7f)),
                        label = "ImageSizeAnim"
                    )

                    val animatedVisibility by animateFloatAsState(
                        targetValue = if (imageVisible) 1f else 0f,
                        label = "ImageVisibilityAnim"
                    )


                    album.albumArtBitmap?.let { bitmap ->
                        val finalImageSize =
                            with(density) { animatedImageSize.toDp() * animatedVisibility }

                        if (animatedVisibility > 0.3f) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Album Art",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(finalImageSize)
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
                    CollectionInfo(
                        bitrateKbps = bitrateKbps,
                        genre = genre,
                        songCountText = songCountText,
                        year = year,
                        storageSize = storageSize,
                        totalDuration = totalDuration
                    )
                }
                ButtonPlayPause(
                    playerViewModel = playerViewModel,
                    onPlayTap = {
                        if (!isSameAlbum) {
                            playerViewModel.playCollection(album.songs, album.name, album.albumId)
                        }
                    },
                    isSameAlbum = isSameAlbum
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(musicListHeightPercentage)
            ) {
                // Draggable handle (border line) placed directly below the album details
                Box(
                    modifier = Modifier
                        .offset(y = 24.dp)
                        .clip(CircleShape)
                        .background(appColors.backgroundDarker)
                        .border(borderWidth, animatedBackgroundColor, CircleShape)
                        .innerShadow(
                            shape = RoundedCornerShape(16.dp),
                            color = appColors.font.copy(alpha = 0.25f),
                            blur = 8.dp,
                            offsetY = 6.dp,
                            offsetX = 0.dp,
                            spread = 0.dp
                        )
                        .padding(8.dp)
                        .size(28.dp)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                isDragging.value = true
                                dragOffsetState.floatValue =
                                    (dragOffsetState.floatValue + delta).coerceIn(-maxDragPx, 0f)
                            },
                            onDragStopped = {
                                isDragging.value = false
                            }
                        )
                        .zIndex(3f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.draggableico),
                        contentDescription = "Draggable slider",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp)
                            .rotate(animatedIconRotation)
                            .zIndex(5f),
                        tint = appColors.font
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(animatedBackgroundGradient)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                isDragging.value = true
                                dragOffsetState.floatValue =
                                    (dragOffsetState.floatValue + delta).coerceIn(-maxDragPx, 0f)
                            },
                            onDragStopped = {
                                isDragging.value = false
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
}