package com.example.iimusica.components.ux

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.iimusica.ui.theme.LocalAppColors

@Composable
fun ShadowBox(modifier: Modifier = Modifier) {
    val appColors = LocalAppColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(appColors.font.copy(alpha = 0.2f))
            .shadow(
                8.dp,
                shape = RectangleShape,
                ambientColor = appColors.font,
                spotColor = appColors.font
            )
    )
}