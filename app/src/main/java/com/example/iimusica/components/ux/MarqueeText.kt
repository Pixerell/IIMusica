package com.example.iimusica.components.ux


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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography


@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Typography.bodyLarge,
    isCentered : Boolean = true,
    isMaintext : Boolean = true

) {
    val appColors = LocalAppColors.current
    var textWidth by remember { mutableFloatStateOf(0f) }
    var boxWidth by remember { mutableFloatStateOf(0f) }
    val animatedOffset = remember { Animatable(0f) }
    val speed = 200
    var delayMillis = 1000
    var isActive by remember { mutableStateOf(true) }
    val finalStyle = style.copy(
        fontWeight = if (isMaintext) FontWeight.Bold else FontWeight.Normal,
        color = if (isMaintext) appColors.font else appColors.secondaryFont
    )

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
                if (isCentered) {
                    val centeredOffset = (boxWidth - textWidth) / 2f
                    animatedOffset.snapTo(centeredOffset)
                }
                else {
                    animatedOffset.snapTo(0f)
                }
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .apply {
                if (isCentered) {
                    clickable {
                        isActive = !isActive
                    }
                }
            }
            .onGloballyPositioned { layoutCoordinates ->
                boxWidth = layoutCoordinates.size.width.toFloat()
            }
            .clipToBounds()


    ) {
        Text(
            text = text,
            style = finalStyle,
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