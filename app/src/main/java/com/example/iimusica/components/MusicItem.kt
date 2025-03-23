package com.example.iimusica.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.example.iimusica.MusicFile
import com.example.iimusica.R

@Composable
fun MusicItem(music: MusicFile, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("music_detail/${music.name}/${music.artist}")
            }
            .padding(vertical = 2.dp)
            .background(color = Color(0xFF030310)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberAsyncImagePainter(
            model = music.albumArtUri ?: R.drawable.default_image
        )

        Image(
            painter = painter,
            contentDescription = "Album Art",
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = music.name,
                style = TextStyle(fontWeight = FontWeight.W500, fontSize = 20.sp, color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = music.artist, style = TextStyle(fontSize = 18.sp, color = Color.White))
        }
    }
}