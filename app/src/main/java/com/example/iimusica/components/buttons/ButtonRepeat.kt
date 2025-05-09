package com.example.iimusica.components.buttons


import androidx.annotation.OptIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.components.ux.getRepeatPainter
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors


@OptIn(UnstableApi::class)
@Composable
fun ButtonRepeat(playerViewModel: PlayerViewModel, modifier: Modifier) {
    val appColors = LocalAppColors.current
    IconButton(onClick = { playerViewModel.toggleRepeat() }, modifier = modifier.size(28.dp)) {
        Icon(
            painter = getRepeatPainter(playerViewModel.repeatMode.value),
            contentDescription = "Repeat mode",
            tint = if (playerViewModel.repeatMode.value != ExoPlayer.REPEAT_MODE_OFF) appColors.active else appColors.icon
        )
    }
}