package com.example.iimusica.screens


import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.formatDuration
import com.example.iimusica.utils.getMusicFileFromPath
import com.example.iimusica.utils.parseDuration


@Composable
fun MusicScreen(path: String, playerViewModel: PlayerViewModel) {

    val appColors = LocalAppColors.current
    val context = LocalContext.current
    var currentPosition by remember { mutableLongStateOf(0L) }
    val musicFile = getMusicFileFromPath(context, path)
    // Initialize ExoPlayer

    val exoPlayer = playerViewModel.exoPlayer
    // Play the music whenever the path changes
    LaunchedEffect(path) {
        playerViewModel.playMusic(path)
    }


    // Handler for updating position
    val handler = remember { Handler(Looper.getMainLooper()) }

    DisposableEffect(exoPlayer) {
        val updatePosition = object : Runnable {
            override fun run() {
                currentPosition = exoPlayer.currentPosition
                handler.postDelayed(this, 500)
            }
        }
        handler.post(updatePosition)

        onDispose {
            handler.removeCallbacks(updatePosition)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (musicFile != null) {
            val duration = parseDuration(musicFile.duration) // parse "5:23" string into duration

            Text(text = "Now Playing:", color = appColors.font, fontSize = 20.sp)
            Text(text = musicFile.name, color = appColors.font, fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text(text = "by ${musicFile.artist}", color = appColors.secondaryFont, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(currentPosition),
                    color = appColors.font,
                    fontSize = 14.sp
                )
                Text(
                    text = formatDuration(duration),
                    color = appColors.font,
                    fontSize = 14.sp
                )
            }

            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { exoPlayer.seekTo(it.toLong()) },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.fillMaxWidth()
                    .padding(24.dp)
            )

            Row {
                Button(onClick = { exoPlayer.play() }) {
                    Text("Play")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { exoPlayer.pause() }) {
                    Text("Pause")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    exoPlayer.seekTo(0)
                    exoPlayer.play()
                }) {
                    Text("Restart")
                }
            }
        }
        else {
            Text(text = "Error: Music file not found", color = appColors.font)
        }

    }
}