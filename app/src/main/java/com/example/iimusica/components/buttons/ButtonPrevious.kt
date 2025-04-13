package com.example.iimusica.components.buttons


import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.example.iimusica.R
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors


@Composable
fun ButtonPrevious(playerViewModel: PlayerViewModel, modifier: Modifier) {
    val appColors = LocalAppColors.current
    IconButton(onClick = { playerViewModel.playPrevious() }, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.nextico),
            contentDescription = "Previous",
            tint = appColors.icon,
            modifier = modifier.graphicsLayer(scaleX = -1f)
        )
    }
}