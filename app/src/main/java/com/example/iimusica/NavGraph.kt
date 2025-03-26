package com.example.iimusica

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iimusica.screens.MusicListScreen
import com.example.iimusica.screens.MusicScreen

@Composable
fun AppNavGraph(navController: NavHostController, context: Context, toggleTheme: () -> Unit) {
    NavHost(navController, startDestination = "music_list") {
        composable("music_list") { MusicListScreen(navController, context, toggleTheme) }
        composable("music_detail/{musicName}/{artist}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("musicName") ?: "Unknown"
            val artist = backStackEntry.arguments?.getString("artist") ?: "Unknown"
            MusicScreen(name, artist)
        }
    }
}