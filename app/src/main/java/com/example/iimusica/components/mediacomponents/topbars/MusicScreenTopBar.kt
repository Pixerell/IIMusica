package com.example.iimusica.components.mediacomponents.topbars

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.iimusica.R
import com.example.iimusica.components.innerShadow
import com.example.iimusica.components.ux.AudioVisualizerView
import com.example.iimusica.types.SortOption
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun MusicScreenTopBar(
    isPlaying: Boolean,
    onBackClick: () -> Unit,
    selectedSortOption: SortOption,
    onSortOptionSelected: (SortOption) -> Unit,
    isDescending: Boolean,
    onReshuffle: () -> Unit,
    onReloadLocalFiles: () -> Unit,
    onToggleDescending: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val appColors = LocalAppColors.current
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var settingsExpanded by remember { mutableStateOf(false) }


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

        if (isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RectangleShape)
                    .offset(y=24.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.BottomCenter
            ) {
                AudioVisualizerView(
                    barCount = 16,
                    barWidth = 8.dp,
                    barHeight = 80f,
                    barOpacity = 0.05f,
                    updateTime = 60
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = if (isLandscape) 8.dp else 24.dp),
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
                    painter = painterResource(id = R.drawable.backicon),
                    contentDescription = "Back",
                    tint = appColors.icon,
                    modifier = Modifier.size(32.dp)
                )
            }
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPlaying) "Now Playing" else "Paused",
                    color = appColors.font,
                    fontSize = Typography.bodyLarge.fontSize,
                    fontFamily = Typography.bodyLarge.fontFamily,
                    fontWeight = Typography.bodyLarge.fontWeight,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { settingsExpanded = !settingsExpanded },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.moreico),
                    contentDescription = "Settings",
                    tint = appColors.icon,
                    modifier = Modifier.size(32.dp)
                )
                SettingsDropDownMenu(
                    expanded = settingsExpanded,
                    onDismissRequest = { settingsExpanded = false },
                    onSortOptionSelected = onSortOptionSelected,
                    selectedSortOption = selectedSortOption,
                    isDescending = isDescending,
                    onReshuffle = {
                        onReshuffle()
                        settingsExpanded = false
                    },
                    onReloadLocalFiles = {
                        onReloadLocalFiles()
                        settingsExpanded = false
                    },
                    onToggleDescending = onToggleDescending,
                    snackbarHostState = snackbarHostState
                )
            }

        }
    }
}
