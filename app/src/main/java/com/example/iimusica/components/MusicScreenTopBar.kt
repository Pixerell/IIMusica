package com.example.iimusica.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun MusicScreenTopBar(isPlaying: Boolean, onBackClick: () -> Unit, onSettingsClick: () -> Unit) {
    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = appColors.icon,
            modifier = Modifier
                .size(40.dp)
                .clickable { onBackClick() }
        )

        Text(
            text = if (isPlaying) "Now Playing" else "Paused",
            color = appColors.font,
            fontSize = Typography.bodyLarge.fontSize,
            fontFamily = Typography.bodyLarge.fontFamily,
            fontWeight = Typography.bodyLarge.fontWeight,
            textAlign = TextAlign.Center
        )

        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings",
            tint = appColors.icon,
            modifier = Modifier
                .size(32.dp)
                .clickable { onSettingsClick() }

        )

    }
}
