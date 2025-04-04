package com.example.iimusica.components


import androidx.annotation.OptIn
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography


@OptIn(UnstableApi::class)
@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val appColors = LocalAppColors.current
    var textWidth by remember { mutableFloatStateOf(0f) }
    var boxWidth by remember { mutableFloatStateOf(0f) }
    val animatedOffset = remember { Animatable(0f) }
    val speed = 200
    var delayMillis = 1000
    var isActive by remember { mutableStateOf(true) }

    LaunchedEffect(text, isActive) {
        if (textWidth == boxWidth && isActive) {
            var isFirstanim = true
            while (true) {
                if (isFirstanim) {
                    val startOffset = (boxWidth - textWidth) / 2f
                    animatedOffset.snapTo(startOffset)
                }
                val distance = textWidth + boxWidth
                animatedOffset.animateTo(
                    targetValue = -distance,
                    animationSpec = tween(
                        durationMillis = (distance / speed * 1000).toInt(),
                        easing = LinearEasing,
                        delayMillis = delayMillis
                    )
                )
                isFirstanim = false
                delayMillis = 100
                animatedOffset.snapTo(boxWidth)
            }
        }
        else {
            if (textWidth > 0f && boxWidth > 0f) {
                val centeredOffset = (boxWidth - textWidth) / 2f
                animatedOffset.snapTo(centeredOffset)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable{
                isActive = !isActive
            }
            .onGloballyPositioned { layoutCoordinates ->
                boxWidth = layoutCoordinates.size.width.toFloat()
            }
            .clipToBounds()


    ) {
        Text(
            text = text,
            style = Typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = appColors.font
            ),
            maxLines = 1,
            overflow = TextOverflow.Visible,
            softWrap = false,
            modifier = Modifier
                .offset { IntOffset(animatedOffset.value.toInt(), 0) }
                .onGloballyPositioned { layoutCoordinates ->
                    textWidth = layoutCoordinates.size.width.toFloat()
                }
        )
    }
}