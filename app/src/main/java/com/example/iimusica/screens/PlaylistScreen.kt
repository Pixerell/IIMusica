package com.example.iimusica.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.iimusica.ui.theme.LocalAppColors

@Composable
fun PlaylistScreen(
) {
    val appColors = LocalAppColors.current

    Box(
        modifier = Modifier.fillMaxSize().background(appColors.accentGradient)
    )
    {
        Text(
            text = "Baby"
        )
    }
}