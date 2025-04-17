package com.example.iimusica.components

import android.net.Uri
import androidx.annotation.OptIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.buttons.ButtonNext
import com.example.iimusica.components.buttons.ButtonPlayPause
import com.example.iimusica.components.buttons.ButtonPrevious
import com.example.iimusica.components.mediacomponents.DurationBar
import com.example.iimusica.components.ux.MarqueeText
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.utils.fetchers.albumPainter
import com.example.iimusica.utils.parseDuration

@OptIn(UnstableApi::class)
@Composable
fun MiniPlayer(playerViewModel: PlayerViewModel, navController: NavController) {
    val context = LocalContext.current
    val appColors = LocalAppColors.current
    val currentPath = playerViewModel.currentPath.value
    val currentMusic = playerViewModel.queueManager.getQueue().find { it.path == currentPath }

    if (currentMusic == null) return
    val painter = albumPainter(currentMusic, context)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .zIndex(111f)
            .background(appColors.backgroundDarker)
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
                modifier = Modifier.weight(8f),
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
                    onClick = { /* TODO Make the miniplayer hide/unhide by this */ },
                    modifier = Modifier
                        .offset(y = (-24).dp)
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
                        .zIndex(20f)

                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        tint = appColors.icon,
                        contentDescription = "Options"
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
                painter = painter, contentDescription = "Album Art", modifier = Modifier.size(48.dp)
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
