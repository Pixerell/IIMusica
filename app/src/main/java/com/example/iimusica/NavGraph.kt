package com.example.iimusica

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iimusica.screens.MusicListScreen
import com.example.iimusica.screens.MusicScreen
import com.example.iimusica.screens.PlayerViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    context: Context,
    toggleTheme: () -> Unit,
    playerViewModel: PlayerViewModel
) {
    NavHost(navController, startDestination = "music_list") {
        composable("music_list") { MusicListScreen(navController, context, toggleTheme, playerViewModel = playerViewModel) }

        composable("music_detail/{path}") { backStackEntry ->
            val path = Uri.decode(backStackEntry.arguments?.getString("path") ?: "Unknown")
            MusicScreen(path, playerViewModel, navController)
        }
    }
}