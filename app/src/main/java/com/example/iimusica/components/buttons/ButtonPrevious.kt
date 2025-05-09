package com.example.iimusica.components.buttons


import androidx.annotation.OptIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.R
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors


@OptIn(UnstableApi::class)
@Composable
fun ButtonPrevious(playerViewModel: PlayerViewModel, modifier: Modifier) {
    val appColors = LocalAppColors.current
    IconButton(onClick = { playerViewModel.playPrevious() }, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.previco),
            contentDescription = "Previous",
            tint = appColors.icon,
            modifier = modifier
        )
    }
}