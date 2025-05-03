package com.example.iimusica.components.mediacomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.iimusica.components.innerShadow
import com.example.iimusica.types.AlbumSummary
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.utils.fetchers.albumPainter

@Composable
fun AlbumItem(summary: AlbumSummary) {
    val appColors = LocalAppColors.current
    val painter = albumPainter(summary.representativeSong)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .innerShadow(
                shape = RectangleShape,
                color = appColors.font.copy(alpha = 0.4f),
                blur = 8.dp,
                offsetY = 4.dp,
                offsetX = 0.dp,
                spread = 0.dp
            )

    ) {
        Image(
            painter = painter,
            contentDescription = summary.representativeSong?.albumId.toString(),
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .innerShadow(
                    shape = RectangleShape,
                    color = appColors.font.copy(alpha = 0.4f),
                    blur = 8.dp,
                    offsetY = 4.dp,
                    offsetX = 0.dp,
                    spread = 0.dp
                )
                .padding(vertical = 2.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = summary.name,
                fontStyle = Typography.bodyMedium.fontStyle,
                fontSize = Typography.bodyMedium.fontSize,
                fontWeight = Typography.headlineMedium.fontWeight,
                color = appColors.font,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

                )
            Text(
                text = summary.artist ?: "Unknown Artist",
                fontStyle = Typography.bodySmall.fontStyle,
                fontSize = Typography.bodySmall.fontSize,
                color = appColors.secondaryFont,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
