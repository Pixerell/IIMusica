package com.example.iimusica.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.iimusica.ui.theme.LocalAppColors

@Composable
fun Loader(modifier: Modifier) {
    val appColors = LocalAppColors.current
    Box(
        modifier = modifier
    ) {
        // Outer circle (Outline)
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(6.dp, appColors.secondaryFont.copy(alpha = 0.25f))
        ) {
            // Inner CircularProgressIndicator
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = appColors.icon,
                strokeWidth = 6.dp
            )
        }
    }
}