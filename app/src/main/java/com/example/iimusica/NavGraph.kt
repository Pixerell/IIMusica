package com.example.iimusica

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.iimusica.screens.MusicListScreen
import com.example.iimusica.screens.MusicScreen
import com.example.iimusica.screens.PlayerViewModel

@OptIn(UnstableApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    context: Context,
    toggleTheme: () -> Unit,
    playerViewModel: PlayerViewModel
) {
    NavHost(navController, startDestination = "music_list") {
        composable("music_list") {
            MusicListScreen(
                navController,
                context,
                toggleTheme,
                playerViewModel = playerViewModel
            )
        }

        composable(
            "music_detail/{path}",
            deepLinks = listOf(navDeepLink {
                uriPattern = "musica://music_detail/{path}"
            })
        ) { backStackEntry ->
            val path = Uri.decode(backStackEntry.arguments?.getString("path") ?: "Unknown")
            MusicScreen(path, playerViewModel, navController)
        }

    }
}