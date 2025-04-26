package com.example.iimusica

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iimusica.screens.MusicPagerScreen
import com.example.iimusica.screens.MusicScreen
import com.example.iimusica.screens.MusicViewModel
import com.example.iimusica.screens.PlayerViewModel

@OptIn(UnstableApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    context: Context,
    toggleTheme: () -> Unit,
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel
) {
    NavHost(navController, startDestination = "music_pager") {
        composable("music_pager") {
            MusicPagerScreen(
                navController,
                toggleTheme,
                musicViewModel = musicViewModel,
                playerViewModel = playerViewModel,
                context
            )
        }

        composable(
            "music_detail/{path}",
        ) { backStackEntry ->
            val path = Uri.decode(backStackEntry.arguments?.getString("path") ?: "Unknown")
            MusicScreen(path, musicViewModel, playerViewModel, navController)
        }

    }
}