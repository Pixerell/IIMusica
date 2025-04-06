package com.example.iimusica.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.iimusica.components.ButtonPlayPause
import com.example.iimusica.components.ButtonPrevious
import com.example.iimusica.components.MarqueeText
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.utils.albumPainter

@Composable
fun MiniPlayer(playerViewModel: PlayerViewModel, navController: NavController) {
    val context = LocalContext.current
    val appColors = LocalAppColors.current
    val currentPath = playerViewModel.currentPath.value
    val currentMusic = playerViewModel.getQueue().find { it.path == currentPath }

    if (currentMusic == null) return
    val painter = albumPainter(currentMusic, context)

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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painter,
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(48.dp)
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp).width(200.dp)
            ) {

                MarqueeText(
                    text = currentMusic.name,
                    style = Typography.bodyMedium,
                    isCentered = false
                )
                MarqueeText(
                    text = currentMusic.artist,
                    style = Typography.bodySmall,
                    isCentered = false,
                    isMaintext = false
                )

            }
            ButtonPrevious(playerViewModel, modifier = Modifier.weight(1f))
            ButtonPlayPause(playerViewModel)
        }
    }
}
