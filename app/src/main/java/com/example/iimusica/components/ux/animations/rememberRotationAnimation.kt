package com.example.iimusica.components.ux.animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.iimusica.types.ANIM_SPEED_VERYSHORT

@Composable
fun rememberRotationAnimation(
    isExpanded: Boolean,
    expandedRotation: Float = 90f,
    collapsedRotation: Float = 0f,
    durationMillis: Int = ANIM_SPEED_VERYSHORT,
    label: String = "RotationAnimation"
): Float {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) expandedRotation else collapsedRotation,
        animationSpec = tween(durationMillis = durationMillis),
        label = label
    )
    return rotation
}
