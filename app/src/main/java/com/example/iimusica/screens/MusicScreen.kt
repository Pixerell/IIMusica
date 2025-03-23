package com.example.iimusica.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MusicScreen(name: String, artist: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Now Playing:", color = Color.White, fontSize = 20.sp)
        Text(text = name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = "by $artist", color = Color.Gray, fontSize = 18.sp)
    }
}