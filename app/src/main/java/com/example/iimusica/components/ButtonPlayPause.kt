package com.example.iimusica.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.iimusica.R
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors

@Composable
fun ButtonPlayPause(playerViewModel: PlayerViewModel) {
    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        FloatingActionButton(
            onClick = { playerViewModel.togglePlayPause() },
            modifier = Modifier
                .clip(CircleShape)
                .size(80.dp),
            containerColor = appColors.icon,
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (playerViewModel.isPlaying.value) R.drawable.pauseico else R.drawable.playico),
                    contentDescription = if (playerViewModel.isPlaying.value) "Pause" else "Play",
                    tint = appColors.active,
                    modifier = Modifier.size(28.dp)
                        .then(
                            if (!playerViewModel.isPlaying.value) {
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