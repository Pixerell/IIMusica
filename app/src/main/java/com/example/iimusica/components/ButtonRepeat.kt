package com.example.iimusica.components


import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.example.iimusica.R
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors

@Composable
fun ButtonRepeat(playerViewModel: PlayerViewModel, modifier: Modifier) {
    val appColors = LocalAppColors.current
    IconButton(onClick = {playerViewModel.toggleRepeat() },  modifier = modifier.size(28.dp)) {
        val repeatIcon = when (playerViewModel.exoPlayer.repeatMode) {
            ExoPlayer.REPEAT_MODE_OFF -> painterResource(R.drawable.repeatico)
            ExoPlayer.REPEAT_MODE_ALL -> painterResource(R.drawable.repeatico)
            ExoPlayer.REPEAT_MODE_ONE -> painterResource(R.drawable.repeatsongico)
            else -> painterResource(R.drawable.repeatico)
        }

        Icon(
            painter = repeatIcon,
            contentDescription = "Repeat mode",
            tint = if (playerViewModel.repeatMode.value != ExoPlayer.REPEAT_MODE_OFF) appColors.active else appColors.icon                            )
    }
}