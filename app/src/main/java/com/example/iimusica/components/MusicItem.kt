package com.example.iimusica.components

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.utils.MusicFile
import com.example.iimusica.R
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun MusicItem(music: MusicFile,
              navController: NavController,
              isLastItem: Boolean,
              playerViewModel: PlayerViewModel,
              isCurrentPlaying: Boolean) {

    val appColors = LocalAppColors.current
    val fontColor = if (isCurrentPlaying) {
        appColors.active
    } else {
        appColors.font
    }

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // State for handling single and double click
    var lastTapTime by remember { mutableLongStateOf(0L) }
    val doubleTapThreshold  = 500L // in milliseconds
    val coroutineScope = rememberCoroutineScope()

    var isDoubleTapDetected by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val currentTime = System.currentTimeMillis()

                    val isDoubleTap = currentTime - lastTapTime < doubleTapThreshold

                if (isDoubleTap) {
                        isDoubleTapDetected = true
                        if (currentRoute != null) {
                            playerViewModel.setCurrentPath(music.path, false)

                            navController.navigate("music_detail/${Uri.encode(music.path)}") {
                                launchSingleTop = true
                            }
                        }
                    } else {
                        isDoubleTapDetected = false

                        coroutineScope.launch {
                            delay(doubleTapThreshold  / 2)

                            if (!isDoubleTapDetected) {
                                if (music.path == playerViewModel.currentPath.value) {
                                    playerViewModel.togglePlayPause()
                                } else {
                                    playerViewModel.playMusic(music.path)
                                    playerViewModel.setCurrentPath(music.path, false)
                                }
                            }
                        }
                    }

                    // Update the last tap time
                    lastTapTime = currentTime


            }
            .padding(vertical = 2.dp)
            .then(
                if (isLastItem) Modifier.shadow(
                    8.dp, shape = RectangleShape, ambientColor = appColors.font, spotColor = appColors.font
                ) else Modifier
            )            .background(appColors.background),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberAsyncImagePainter(
            model = music.albumArtUri ?: R.drawable.default_image
        )

        val imageModifier = if (music.albumArtUri == null) {
            Modifier.size(60.dp) // Smaller size for the default image
        } else {
            Modifier.size(80.dp) // Regular size for other images
        }


        Box(
            modifier = Modifier
                .size(80.dp)  // Fixed size for the Box that wraps the image
                .wrapContentSize(Alignment.Center)
        ) {
            Image(
                painter = painter,
                contentDescription = "Album Art",
                modifier = imageModifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentScale = ContentScale.FillBounds
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)

        ) {

            Text(
                text = music.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = Typography.bodyMedium.fontSize, color = fontColor, fontFamily = Typography.bodyLarge.fontFamily),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Text(text = music.artist, style = TextStyle(fontSize = Typography.bodySmall.fontSize, color = appColors.font, fontFamily = Typography.bodySmall.fontFamily),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
        }

        if (playerViewModel.isPlaying.value && music.path == playerViewModel.currentPath.value) {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)

            ) {
                AudioVisualizerView()
            }
        }

    }
}