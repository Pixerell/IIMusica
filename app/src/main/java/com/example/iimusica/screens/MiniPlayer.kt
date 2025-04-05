package com.example.iimusica.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.iimusica.components.ButtonPlayPause
import com.example.iimusica.ui.theme.LocalAppColors

@Composable
fun MiniPlayer(playerViewModel: PlayerViewModel, navController: NavController) {
    val appColors = LocalAppColors.current
    val currentPath = playerViewModel.currentPath.value
    val isPlaying = playerViewModel.isPlaying.value
    val currentMusic = playerViewModel.getQueue().find { it.path == currentPath }

    if (currentMusic == null) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .zIndex(111f)
            .background(appColors.backgroundDarker)
            .clickable {
                navController.navigate("music_detail/${Uri.encode(currentPath)}") {
                    launchSingleTop = true
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentMusic.name,
                color = appColors.font,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            )
            ButtonPlayPause(playerViewModel)
        }
    }
}
