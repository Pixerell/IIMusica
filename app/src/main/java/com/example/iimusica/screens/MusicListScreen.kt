@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.iimusica.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.iimusica.MusicFile
import com.example.iimusica.getAllMusicFiles
import com.example.iimusica.components.MusicItem

@Composable
fun MusicListScreen(navController: NavController, context: Context) {
    var mFiles by remember { mutableStateOf(emptyList<MusicFile>()) }

    LaunchedEffect(Unit) {
        mFiles = getAllMusicFiles(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Music Files",
                        style = TextStyle(
                            fontWeight = FontWeight.W500,
                            fontSize = 28.sp,
                            color = Color.White
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF030310))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0B1045), Color(0xFF5B245A))
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(mFiles) { music ->
                    MusicItem(music, navController)
                }
            }
        }
    }
}