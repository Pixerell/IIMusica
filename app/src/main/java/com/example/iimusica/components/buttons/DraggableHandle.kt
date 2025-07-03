package com.example.iimusica.components.buttons


import androidx.compose.foundation.layout.Box
import com.example.iimusica.components.innerShadow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.iimusica.R
import com.example.iimusica.components.ux.animations.rememberAnimatedGradient
import com.example.iimusica.types.ANIM_SPEED_SHORT
import com.example.iimusica.types.ANIM_SPEED_TINY
import com.example.iimusica.ui.theme.LocalAppColors


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DraggableHandle(
    modifier: Modifier = Modifier,
    isDragging: Boolean,
    dragOffsetState: MutableState<Float>,
    onDragStopped: () -> Unit,
    maxDragPx: Float,
    iconRotation: Float = 0f,
    size: Dp = 28.dp,
    borderWidth: Dp = if (isDragging) 2.dp else 0.dp
) {
    val appColors = LocalAppColors.current
    val animatedBackgroundColor = rememberAnimatedGradient(
        isExpanded = isDragging,
        expandedColors = listOf(appColors.accentStart, appColors.accentEnd),
        collapsedColors = listOf(appColors.background),
        customDurations = listOf(ANIM_SPEED_TINY, ANIM_SPEED_SHORT)
    )

    Box(
        modifier = modifier
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
            .size(size)
            .draggable(
                orientation = Orientation.Vertical,
                //0f, maxDragPx
                state = rememberDraggableState { delta ->
                    dragOffsetState.value = (dragOffsetState.value - delta).coerceIn(0f, maxDragPx)
                },
                onDragStopped = { onDragStopped() }
            )
            .zIndex(3f)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.draggableico),
            contentDescription = "Draggable slider",
            modifier = Modifier
                .align(Alignment.Center)
                .size(size * 0.85f)
                .rotate(iconRotation)
                .pointerInteropFilter {
                    // Let it pass through so parent receives gesture
                    false
                }
                .zIndex(5f),
            tint = appColors.font
        )
    }
}
