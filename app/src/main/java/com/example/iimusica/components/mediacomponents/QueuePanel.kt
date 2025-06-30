package com.example.iimusica.components.mediacomponents

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.iimusica.ui.theme.LocalAppColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.R
import com.example.iimusica.components.innerShadow
import com.example.iimusica.components.ux.ExpandableText
import com.example.iimusica.components.ux.animations.rememberAnimatedGradient
import com.example.iimusica.components.ux.animations.rememberRotationAnimation
import com.example.iimusica.core.viewmodels.MusicViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.core.viewmodels.SearchSortState
import com.example.iimusica.types.ANIM_SPEED_HIGH
import com.example.iimusica.types.ANIM_SPEED_SHORT
import com.example.iimusica.types.SKIP_CHECK_CODE
import com.example.iimusica.ui.theme.QUEUE_PANEL_OFFSET
import com.example.iimusica.ui.theme.Typography

@OptIn(UnstableApi::class)
@Composable
fun QueuePanel(
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    state: SearchSortState,
    isPanelExpanded: Boolean,
    togglePanelState: (Boolean) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val appColors = LocalAppColors.current
    val lazylistState = rememberLazyListState()
    val currentQueue = playerViewModel.queueManager.getQueue()
    val queueName = playerViewModel.queueManager.queueName.value

    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val expandedHeight = if (isLandscape) 180.dp else 400.dp
    val panelHeight by animateDpAsState(targetValue = if (isPanelExpanded) expandedHeight else QUEUE_PANEL_OFFSET)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val boxWidth = screenWidth * 0.4f

    val animatedBackgroundColor = rememberAnimatedGradient(
        isExpanded = isPanelExpanded,
        expandedColors = listOf(appColors.accentStart, appColors.accentEnd),
        collapsedColors = listOf(appColors.backgroundDarker)
    )

    val animatedBackgroundGradient = rememberAnimatedGradient(
        isExpanded = isPanelExpanded,
        expandedColors = listOf(appColors.activeStart, appColors.activeEnd),
        collapsedColors = listOf(appColors.backgroundDarker),
        customDurations = listOf(ANIM_SPEED_SHORT, ANIM_SPEED_HIGH)
    )

    val rotation =
        rememberRotationAnimation(isPanelExpanded, expandedRotation = 270f, collapsedRotation = 90f)


    val borderWidth by animateDpAsState(
        targetValue = if (isPanelExpanded) 2.dp else 0.dp
    )


    LaunchedEffect(state.isDescending, state.sortOption) {
        musicViewModel.updateFilteredFiles(state)
        if (playerViewModel.currentCollectionID.value == SKIP_CHECK_CODE) {
            playerViewModel.queueManager.setQueue(musicViewModel.filteredFiles.value)
        }
    }

    LaunchedEffect(musicViewModel.shouldScrollTop.value) {
        if (musicViewModel.shouldScrollTop.value) {
            lazylistState.scrollToItem(0)
            musicViewModel.shouldScrollTop.value = false
        }
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        if (isPanelExpanded) {
            Box(
                modifier = Modifier
                    .align(Alignment.Start)
                    .width(boxWidth)
                    .clip(RoundedCornerShape(topEnd = 8.dp))
                    .innerShadow(
                        shape = RectangleShape,
                        color = appColors.font.copy(alpha = 0.4f),
                        blur = 8.dp,
                        offsetY = 6.dp,
                        offsetX = 0.dp,
                        spread = 0.dp
                    )
                    .background(appColors.background)
                    .zIndex(10f)

            ) {
                ExpandableText(
                    text = queueName,
                    color = appColors.font,
                    style = Typography.labelSmall.copy(
                        fontFamily = Typography.bodySmall.fontFamily,
                        fontWeight = Typography.headlineMedium.fontWeight
                    ),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.Center)
                )

            }

        }

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
                    .size(32.dp)
                    .zIndex(3f)) {
                Icon(
                    painter = painterResource(id = R.drawable.pointerico),
                    contentDescription = if (isPanelExpanded) "Collapse Panel" else "Expand Panel",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .rotate(rotation)
                        .size(16.dp),
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
                    .zIndex(1f)
            ) {
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
                        navController = navController,
                        playerViewModel = playerViewModel,
                        listState = lazylistState
                    )
                }
            }
        }
    }
}