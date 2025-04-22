package com.example.iimusica.components.mediacomponents

import androidx.annotation.OptIn
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.rememberNavController
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.ui.theme.QUEUE_PANEL_OFFSET

@OptIn(UnstableApi::class)
@Composable
fun QueuePanel(
    playerViewModel: PlayerViewModel,
    isPanelExpanded: Boolean,
    togglePanelState: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val expandedHeight = if (isLandscape) 180.dp else 400.dp
    val panelHeight by animateDpAsState(targetValue = if (isPanelExpanded) expandedHeight else QUEUE_PANEL_OFFSET)


    val appColors = LocalAppColors.current

    val animatedAccentStart by animateColorAsState(
        targetValue = if (isPanelExpanded) appColors.accentStart else appColors.backgroundDarker,
        animationSpec = tween(durationMillis = 1000)
    )

    val animatedAccentEnd by animateColorAsState(
        targetValue = if (isPanelExpanded) appColors.accentEnd else appColors.backgroundDarker,
        animationSpec = tween(durationMillis = 1000)
    )

    val animatedBackgroundColor = Brush.linearGradient(
        colors = listOf(animatedAccentStart, animatedAccentEnd)
    )


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(panelHeight)
            .background(appColors.backgroundDarker)
            .zIndex(2f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-24).dp) // Overlap the icon slightly
                .clip(CircleShape)
                .background(appColors.backgroundDarker)
                .clickable { togglePanelState(!isPanelExpanded) }
                .padding(8.dp)
                .zIndex(3f)
        ) {
            Icon(
                imageVector = if (isPanelExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = if (isPanelExpanded) "Collapse Panel" else "Expand Panel",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp),
                tint = appColors.font
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(panelHeight)
                .background(animatedBackgroundColor)
                // border
                .drawBehind {
                    drawLine(
                        color = appColors.backgroundDarker,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 30f
                    )
                }
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        ) {
            if (isPanelExpanded) {
                MusicList(
                    musicFiles = playerViewModel.queueManager.getQueue(),
                    navController = rememberNavController(),
                    playerViewModel = playerViewModel
                )
            }
        }
    }
}