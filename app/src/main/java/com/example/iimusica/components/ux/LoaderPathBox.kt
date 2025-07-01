package com.example.iimusica.components.ux

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.iimusica.components.innerShadow
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun LoaderPathBox(
    loadingPath: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Box(
        modifier = modifier
            .background(color = appColors.backgroundDarker)
            .zIndex(100f)
            .fillMaxWidth()
            .innerShadow(
                shape = RectangleShape,
                color = appColors.font.copy(alpha = 0.25f),
                blur = 8.dp,
                offsetY = (-6).dp,
                offsetX = 0.dp,
                spread = 0.dp
            )
    ) {
        ExpandableText(
            text = "Loading: $loadingPath",
            color = appColors.font,
            minLines = 2,
            style = Typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
}