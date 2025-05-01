package com.example.iimusica

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iimusica.components.ux.CustomSnackBar
import com.example.iimusica.screens.AlbumViewModel
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
    playerViewModel: PlayerViewModel,
    albumViewModel : AlbumViewModel
) {

    val snackbarHostState = remember { SnackbarHostState() }

    Box(
    ) {
        NavHost(
            navController,
            startDestination = "music_pager",
        ) {
            composable("music_pager") {
                MusicPagerScreen(
                    navController,
                    toggleTheme,
                    musicViewModel = musicViewModel,
                    playerViewModel = playerViewModel,
                    albumViewModel = albumViewModel,
                    context,
                    snackbarHostState = snackbarHostState
                )
            }
            composable("music_detail/{path}") { backStackEntry ->
                val path = Uri.decode(backStackEntry.arguments?.getString("path") ?: "Unknown")
                MusicScreen(
                    path,
                    musicViewModel,
                    playerViewModel,
                    navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 156.dp)
                .padding(horizontal = 32.dp)
        ) { data ->
            CustomSnackBar(data)
        }
    }
}