package com.example.iimusica.components.ux

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.iimusica.ui.theme.LocalAppColors


@Composable
fun AudioBarGraph(audioAmplitude: List<Float>, barWidth: Dp, maxHeight : Float, barOpacity : Float) {
    val appColors = LocalAppColors.current
    Row(
        modifier = Modifier
            .height(maxHeight.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .rotate(180f)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Loop through the audioAmplitude data and create bars
        audioAmplitude.forEach { amplitude ->
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .alpha(barOpacity)
                    .clip(RoundedCornerShape(2.dp))
                    .fillMaxHeight(fraction = amplitude)
                    .background(appColors.activeGradient)
            )
        }
    }
}
