package com.example.iimusica.components.ux.Animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

@Composable
fun rememberRotationAnimation(
    isExpanded: Boolean,
    expandedRotation: Float = 90f,
    collapsedRotation: Float = 0f,
    durationMillis: Int = 200,
    label: String = "RotationAnimation"
): Float {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) expandedRotation else collapsedRotation,
        animationSpec = tween(durationMillis = durationMillis),
        label = label
    )
    return rotation
}
