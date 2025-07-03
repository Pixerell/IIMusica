package com.example.iimusica.components

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.buttons.ButtonNext
import com.example.iimusica.components.buttons.ButtonPlayPause
import com.example.iimusica.components.buttons.ButtonPrevious
import com.example.iimusica.components.mediacomponents.DurationBar
import com.example.iimusica.components.ux.MarqueeText
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.types.MusicFile
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.utils.fetchers.albumPainter
import com.example.iimusica.utils.parseDuration

@OptIn(UnstableApi::class)
@Composable
fun MiniPlayer(
    playerViewModel: PlayerViewModel,
    isMiniPlayerVisible: Boolean,
    onToggleMiniPlayerVisibility: () -> Unit,
    currentMusic: MusicFile?,
    navController: NavController
) {
    if (currentMusic == null) {
        return
    }

    val appColors = LocalAppColors.current
    val currentPath = playerViewModel.currentPath.value
    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val rotation by animateFloatAsState(
        targetValue = if (isMiniPlayerVisible) 180f else 0f,
        label = "ArrowRotation",
    )

    val painter = albumPainter(currentMusic)
    val buttonOffsetY by animateDpAsState(
        targetValue = if (isMiniPlayerVisible) (-24).dp else (-32).dp,
        label = "MiniPlayerButtonOffset"
    )

    val offsetY by animateDpAsState(
        targetValue = if (isMiniPlayerVisible) 0.dp else 78.dp,
        label = "MiniPlayerSlide"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .offset(y = offsetY)
            .background(appColors.backgroundDarker)
            .innerShadow(
                shape = RectangleShape,
                color = appColors.font.copy(alpha = 0.4f),
                blur = 4.dp,
                offsetY = (-6).dp,
                offsetX = 0.dp,
                spread = 4.dp
            )
            .zIndex(111f)
            .clickable {
                navController.navigate("music_detail/${Uri.encode(currentPath)}") {
                    launchSingleTop = true
                }
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DurationBar(
                modifier = Modifier.weight(if (isLandscape) 18f else 8f),
                duration = parseDuration(currentMusic.duration.toString()),
                playerViewModel = playerViewModel,
                isMiniPlayer = true,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                IconButton(
                    onClick = { onToggleMiniPlayerVisibility() },
                    modifier = Modifier
                        .offset(y = buttonOffsetY)
                        .innerShadow(
                            shape = RoundedCornerShape(16.dp),
                            color = appColors.font.copy(alpha = 0.4f),
                            blur = 4.dp,
                            offsetY = 6.dp,
                            offsetX = 0.dp,
                            spread = 0.dp
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(appColors.backgroundDarker)
                        .zIndex(112f)


                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        tint = appColors.icon,
                        contentDescription = "Hide/Show Mini player",
                        modifier = Modifier
                            .rotate(rotation)
                            .zIndex(112f)
                    )
                }
            }


        }

        Row(
            modifier = Modifier
                .fillMaxSize()

                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painter,
                contentDescription = "Album Art",
                modifier = Modifier.size(48.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(10f)
            ) {

                MarqueeText(
                    text = currentMusic.name, style = Typography.bodyMedium, isCentered = false
                )
                MarqueeText(
                    text = currentMusic.artist,
                    style = Typography.bodySmall,
                    isCentered = false,
                    isMaintext = false
                )

            }
            ButtonPrevious(
                playerViewModel, modifier = Modifier
                    .weight(1f)
                    .size(18.dp)
            )
            ButtonPlayPause(playerViewModel, isSmallMode = true)
            ButtonNext(
                playerViewModel, modifier = Modifier
                    .weight(1f)
                    .size(18.dp)
            )
        }
    }

}
