package com.example.iimusica.components.mediacomponents

import androidx.annotation.OptIn
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.rememberNavController
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.components.innerShadow
import com.example.iimusica.screens.MusicViewModel
import com.example.iimusica.ui.theme.QUEUE_PANEL_OFFSET

@OptIn(UnstableApi::class)
@Composable
fun QueuePanel(
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    isPanelExpanded: Boolean,
    togglePanelState: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazylistState = rememberLazyListState()
    val currentQueue = musicViewModel.filteredFiles.value

    LaunchedEffect(musicViewModel.filteredFiles.value, musicViewModel.selectedSortOption.value) {
        playerViewModel.queueManager.setQueue(musicViewModel.filteredFiles.value)
        lazylistState.animateScrollToItem(0)
    }

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

    val animatedActiveStart by animateColorAsState(
        targetValue = if (isPanelExpanded) appColors.activeStart else appColors.backgroundDarker,
        animationSpec = tween(durationMillis = 500)
    )

    val animatedActiveEnd by animateColorAsState(
        targetValue = if (isPanelExpanded) appColors.activeEnd else appColors.backgroundDarker,
        animationSpec = tween(durationMillis = 1500)
    )

    val animatedBackgroundGradient = Brush.linearGradient(
        colors = listOf(animatedActiveStart, animatedActiveEnd)
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isPanelExpanded) 2.dp else 0.dp
    )


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(panelHeight)
            .background(appColors.backgroundDarker)
            .zIndex(2f)
            .innerShadow(
                shape = RectangleShape,
                color = appColors.font.copy(alpha = 0.4f),
                blur = 8.dp,
                offsetY = (-6).dp,
                offsetX = 0.dp,
                spread = 0.dp
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-24).dp)
                .clip(CircleShape)
                .border(borderWidth, animatedBackgroundColor, CircleShape)
                .background(appColors.backgroundDarker)
                .innerShadow(
                    shape = RoundedCornerShape(16.dp),
                    color = appColors.font.copy(alpha = 0.25f),
                    blur = 8.dp,
                    offsetY = 6.dp,
                    offsetX = 0.dp,
                    spread = 0.dp
                )
                .clickable { togglePanelState(!isPanelExpanded) }
                .padding(8.dp)
                .zIndex(3f)) {
            Icon(
                imageVector = if (isPanelExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = if (isPanelExpanded) "Collapse Panel" else "Expand Panel",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp),
                tint = appColors.font
            )
        }
        // border in a box for innershadows
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(panelHeight)
                .background(animatedBackgroundColor)
                .innerShadow(
                    shape = RectangleShape,
                    color = appColors.font.copy(alpha = 0.25f),
                    blur = 4.dp,
                    offsetY = 2.dp,
                    offsetX = 0.dp,
                    spread = 0.dp
                )
                .align(Alignment.BottomCenter)
                .zIndex(1f)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(animatedBackgroundGradient)
                    .zIndex(111f)
            )

            if (isPanelExpanded) {
                MusicList(
                    musicFiles = currentQueue,
                    navController = rememberNavController(),
                    playerViewModel = playerViewModel,
                    listState = lazylistState
                )
            }
        }
    }
}