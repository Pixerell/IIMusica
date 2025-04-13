package com.example.iimusica.components.mediacomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun MusicScreenTopBar(isPlaying: Boolean, onBackClick: () -> Unit, onSettingsClick: () -> Unit) {
    val appColors = LocalAppColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(appColors.backgroundDarker)
    ) {
        // This Box is responsible for drawing the shadow only at the bottom.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)

                .background(appColors.font.copy(alpha = 0.2f))
                .align(Alignment.BottomCenter)
                .shadow(
                    8.dp,
                    shape = RectangleShape,
                    ambientColor = appColors.font,
                    spotColor = appColors.font
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp)
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = appColors.icon,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = if (isPlaying) "Now Playing" else "Paused",
                color = appColors.font,
                fontSize = Typography.bodyLarge.fontSize,
                fontFamily = Typography.bodyLarge.fontFamily,
                fontWeight = Typography.bodyLarge.fontWeight,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = appColors.icon,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
