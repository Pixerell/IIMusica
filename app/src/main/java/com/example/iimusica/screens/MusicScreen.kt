package com.example.iimusica.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iimusica.ui.theme.LocalAppColors

@Composable
fun MusicScreen(name: String, artist: String) {

    val appColors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Now Playing:", color = appColors.font, fontSize = 20.sp)
        Text(text = name, color = appColors.font, fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(text = "by $artist", color = appColors.secondaryFont, fontSize = 18.sp)
    }
}