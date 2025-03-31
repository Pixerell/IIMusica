package com.example.iimusica.components

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.ui.theme.LocalAppColors

@OptIn(UnstableApi::class)
@Composable
fun AudioBarGraph(audioAmplitude: List<Float>) {
    val barWidth = 1.dp
    val maxHeight = 90f
    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier
            .height(maxHeight.dp)
            .fillMaxHeight()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .rotate(180f)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // Loop through the audioAmplitude data and create bars
        audioAmplitude.forEach { amplitude ->

            Box(

                modifier = Modifier
                    .width(barWidth)
                    .fillMaxHeight(fraction = amplitude)
                    .background(appColors.active)
            )
        }
    }
}
