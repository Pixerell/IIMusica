package com.example.iimusica.components.mediacomponents.topbars

import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.components.innerShadow
import com.example.iimusica.components.ux.MarqueeText
import com.example.iimusica.types.Album
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@OptIn(UnstableApi::class)
@Composable
fun AlbumDetailsTopBar(
    album: Album,
    onBackClick: () -> Unit,
    onReshuffle: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val appColors = LocalAppColors.current
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var queueDropDownExpanded by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(appColors.background)
            .innerShadow(
                shape = RectangleShape,
                color = appColors.font.copy(alpha = 0.4f),
                blur = 8.dp,
                offsetY = 6.dp,
                offsetX = 0.dp,
                spread = 0.dp
            )
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (isLandscape) 8.dp else 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
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
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 64.dp) // space for buttons
                    .fillMaxWidth(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MarqueeText(
                    text = album.name, style = Typography.bodyLarge, isCentered = true
                )
                MarqueeText(
                    text = album.artist,
                    style = Typography.bodyMedium,
                    isCentered = true,
                    isMaintext = false
                )
            }

            // Settings Button (Right)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp)
                    .clickable { queueDropDownExpanded = !queueDropDownExpanded },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Queue Settings",
                    tint = appColors.icon,
                    modifier = Modifier.size(32.dp)
                )

                QueueDropDownMenu(
                    expanded = queueDropDownExpanded,
                    onDismissRequest = { queueDropDownExpanded = false },
                    onReshuffle = onReshuffle,
                    snackbarHostState = snackbarHostState
                )
            }
        }

    }
}
