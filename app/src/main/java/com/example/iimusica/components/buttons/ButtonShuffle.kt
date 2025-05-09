package com.example.iimusica.components.buttons


import androidx.annotation.OptIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.R
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors


@OptIn(UnstableApi::class)
@Composable
fun ButtonShuffle(playerViewModel: PlayerViewModel, modifier: Modifier) {
    val appColors = LocalAppColors.current
    IconButton(onClick = { playerViewModel.toggleShuffle() }, modifier = modifier.size(28.dp)) {
        Icon(
            painter = painterResource(R.drawable.shuffleico),
            contentDescription = "Shuffle",
            tint = if (playerViewModel.isShuffleEnabled.value) appColors.active else appColors.icon,
        )
    }
}