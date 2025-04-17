package com.example.iimusica.components.buttons


import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.screens.MusicViewModel
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.reloadmlist

@UnstableApi
@Composable
fun ButtonReload(playerViewModel: PlayerViewModel, viewModel: MusicViewModel, context: Context) {
    val appColors = LocalAppColors.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = {
                reloadmlist(playerViewModel, viewModel, context)
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            contentColor = appColors.icon,
            containerColor = appColors.backgroundDarker,
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh Music Files")
        }
    }
}