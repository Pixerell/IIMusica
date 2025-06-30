package com.example.iimusica.components.ux.animations

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.iimusica.types.ANIM_SPEED_MEDIUM

@Composable
fun rememberAnimatedGradient(
    isExpanded: Boolean,
    expandedColors: List<Color>,
    collapsedColors: List<Color>,
    durationMillis: Int = ANIM_SPEED_MEDIUM,
    customDurations: List<Int>? = null // optional per-color overrides
): Brush {
    val animatedColors = expandedColors.mapIndexed { index, expandedColor ->
        val targetColor = if (isExpanded) {
            expandedColor
        } else {
            collapsedColors.getOrNull(index) ?: Color.Unspecified
        }

        val duration = customDurations?.getOrNull(index) ?: durationMillis

        animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(durationMillis = duration)
        ).value
    }

    return Brush.linearGradient(colors = animatedColors)
}
