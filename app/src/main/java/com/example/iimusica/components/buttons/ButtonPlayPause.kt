package com.example.iimusica.components.buttons

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.R
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors

@OptIn(UnstableApi::class)
@Composable
fun ButtonPlayPause(
    playerViewModel: PlayerViewModel,
    isSmallMode: Boolean = false,
    onPlayTap: (() -> Unit)? = null,
    isSameAlbum: Boolean = true,
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        FloatingActionButton(
            onClick = {
                if (!isSameAlbum) {
                    onPlayTap?.invoke()
                }
                else {
                    playerViewModel.togglePlayPause()
                }
            },
            modifier = Modifier
                .clip(CircleShape)
                .size(if (isSmallMode) 32.dp else 72.dp),
            containerColor = if (isSmallMode) Color.Transparent else appColors.icon,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (playerViewModel.isPlaying && isSameAlbum) R.drawable.pauseico else R.drawable.playico),
                    contentDescription = if (playerViewModel.isPlaying) "Pause" else "Play",
                    tint = appColors.active,
                    modifier = Modifier
                        .size(24.dp)
                        .then(
                            if (!playerViewModel.isPlaying) {
                                Modifier.offset(x = 2.dp)
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }
    }
}