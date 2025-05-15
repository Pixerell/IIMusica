package com.example.iimusica

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
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
import com.example.iimusica.core.screens.AlbumDetailedScreen
import com.example.iimusica.core.screens.MusicPagerScreen
import com.example.iimusica.core.screens.MusicScreen
import com.example.iimusica.core.screens.QueueScreen
import com.example.iimusica.core.viewmodels.AlbumViewModel
import com.example.iimusica.core.viewmodels.MusicViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.core.viewmodels.PlaylistViewModel
import com.example.iimusica.core.viewmodels.SharedViewModel


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(UnstableApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    context: Context,
    toggleTheme: () -> Unit,
    sharedViewModel : SharedViewModel,
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    albumViewModel : AlbumViewModel,
    playlistViewModel : PlaylistViewModel
) {

    val snackBarHostState = remember { SnackbarHostState() }

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
                    playlistViewModel = playlistViewModel,
                    sharedViewModel = sharedViewModel,
                    context,
                    snackbarHostState = snackBarHostState
                )
            }
            composable("music_detail/{path}") { backStackEntry ->
                val path = Uri.decode(backStackEntry.arguments?.getString("path") ?: "Unknown")
                MusicScreen(
                    path,
                    musicViewModel,
                    playerViewModel,
                    sharedViewModel,
                    navController,
                    snackbarHostState = snackBarHostState
                )
            }
            composable("album_detail/{albumId}") { backStackEntry ->
                val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
                AlbumDetailedScreen(
                    albumId = albumId,
                    navController = navController,
                    albumViewModel = albumViewModel,
                    playerViewModel = playerViewModel,
                    snackbarHostState = snackBarHostState
                )
            }
            composable("queue") {
                QueueScreen(
                    navController = navController,
                    playerViewModel = playerViewModel,
                    snackbarHostState = snackBarHostState
                )
            }

        }
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 156.dp)
                .padding(horizontal = 32.dp)
        ) { data ->
            CustomSnackBar(data)
        }
    }
}