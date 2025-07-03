package com.example.iimusica.components.mediacomponents

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.iimusica.ui.theme.LocalAppColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.buttons.DraggableHandle
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
import com.example.iimusica.ui.theme.Typography

@OptIn(UnstableApi::class)
@Composable
fun QueuePanel(
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    state: SearchSortState,
    navController: NavController,
    modifier: Modifier = Modifier,
    dragOffsetState: MutableState<Float>,
    isDragging: MutableState<Boolean>
) {
    val appColors = LocalAppColors.current
    val lazylistState = rememberLazyListState()
    val currentQueue = playerViewModel.queueManager.getQueue()
    val queueName = playerViewModel.queueManager.queueName.value
    val maxDragPx = with(LocalDensity.current) { 500.dp.toPx() }

    val dragOffset by dragOffsetState
    val panelHeightPercentage = (dragOffset / maxDragPx).coerceIn(0.07f, 1f)

    val screenHeightPx =
        with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val targetHeightPx = screenHeightPx * panelHeightPercentage
    val panelHeight = with(LocalDensity.current) { targetHeightPx.toDp() }

    val expansionShow = 0.25f

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val boxWidth = screenWidth * 0.4f

    val isExpanded = panelHeightPercentage > expansionShow

    val animatedBackgroundColor = rememberAnimatedGradient(
        isExpanded = isExpanded,
        expandedColors = listOf(appColors.accentStart, appColors.accentEnd),
        collapsedColors = listOf(appColors.backgroundDarker)
    )

    val animatedBackgroundGradient = rememberAnimatedGradient(
        isExpanded = isExpanded,
        expandedColors = listOf(appColors.activeStart, appColors.activeEnd),
        collapsedColors = listOf(appColors.backgroundDarker),
        customDurations = listOf(ANIM_SPEED_SHORT, ANIM_SPEED_HIGH)
    )

    val rotation = rememberRotationAnimation(
        isExpanded = isExpanded,
        expandedRotation = 180f,
        collapsedRotation = 0f
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isExpanded) 2.dp else 0.dp,
        label = "QueuePanelBorderAnim"
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
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        if (isExpanded) {
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
            DraggableHandle(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-24).dp),
                isDragging = isDragging.value,
                dragOffsetState = dragOffsetState,
                onDragStopped = {
                    isDragging.value = false
                },
                maxDragPx = maxDragPx,
                iconRotation = rotation,
                size = 32.dp,
                borderWidth = borderWidth
            )

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

                if (isExpanded) {
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