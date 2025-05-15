package com.example.iimusica.components.mediacomponents.topbars

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.iimusica.components.buttons.ButtonBack
import com.example.iimusica.components.buttons.ButtonSettings
import com.example.iimusica.components.innerShadow
import com.example.iimusica.components.ux.AudioVisualizerView
import com.example.iimusica.components.ux.ShadowBox
import com.example.iimusica.types.SortOption
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun MusicScreenTopBar(
    isPlaying: Boolean,
    onBackClick: () -> Unit,
    onNavToQueue: () -> Unit,
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
        ShadowBox(modifier = Modifier.align(Alignment.BottomCenter))

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
            ButtonBack(onBackClick)
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

            ButtonSettings(
                selectedSortOption = selectedSortOption,
                isDescending = isDescending,
                onSortOptionSelected = onSortOptionSelected,
                onReshuffle = onReshuffle,
                onReloadLocalFiles = onReloadLocalFiles,
                onToggleDescending = onToggleDescending,
                onNavToQueue = onNavToQueue,
                snackbarHostState = snackbarHostState
            )

        }
    }
}
