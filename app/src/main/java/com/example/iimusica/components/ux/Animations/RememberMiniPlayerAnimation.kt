package com.example.iimusica.components.ux.Animations

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

data class MiniPlayerAnimationState(
    val offset: IntOffset,
    val fabOffset: Dp,
    val onAnimationFinished: () -> Unit
)

@Composable
fun rememberMiniPlayerAnimation(
    isFirstTimeEntered: Boolean,
    isPlaying: Boolean,
    animationComplete: MutableState<Boolean>,
    miniPlayerVisible: MutableState<Boolean>
): MiniPlayerAnimationState {
    val density = LocalDensity.current
    val screenHeight = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    val targetOffset = if (!isFirstTimeEntered) Offset.Zero else Offset(0f, screenHeight)
    val offset by animateOffsetAsState(
        targetValue = if (!miniPlayerVisible.value) targetOffset else Offset.Zero,
        animationSpec = tween(durationMillis = 1000),
        label = "MiniPlayerSlideIn"
    )

    val fabOffsetY by animateDpAsState(
        targetValue = if (!miniPlayerVisible.value) 0.dp else 140.dp,
        animationSpec = tween(durationMillis = 1000),
        label = "FABOffset"
    )

    // Trigger animation complete once conditions are met
    LaunchedEffect(offset, isPlaying) {
        if (offset == targetOffset && isFirstTimeEntered && isPlaying) {
            animationComplete.value = true
        }
    }

    // Reset the flag after animation is complete
    LaunchedEffect(animationComplete.value) {
        if (animationComplete.value && isFirstTimeEntered) {
            miniPlayerVisible.value = true
            animationComplete.value = false
        }
    }

    return MiniPlayerAnimationState(
        offset = IntOffset(0, offset.y.toInt()),
        fabOffset = fabOffsetY,
        onAnimationFinished = {
            miniPlayerVisible.value = true
        }
    )
}
