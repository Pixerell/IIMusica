package com.example.iimusica


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.example.iimusica.core.player.PlaybackController
import com.example.iimusica.core.viewmodels.AlbumViewModel
import com.example.iimusica.core.viewmodels.MusicViewModel
import com.example.iimusica.core.viewmodels.MusicViewModelFactory
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModelFactory
import com.example.iimusica.core.viewmodels.PlaylistViewModel
import com.example.iimusica.core.viewmodels.SharedViewModel
import com.example.iimusica.core.viewmodels.SharedViewModelFactory
import com.example.iimusica.ui.theme.IIMusicaTheme

@UnstableApi
class MainActivity : ComponentActivity() {
    private lateinit var playerViewModel: PlayerViewModel
    lateinit var playbackController: PlaybackController

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedViewModel: SharedViewModel =
            ViewModelProvider(this)[SharedViewModel::class.java]

        val musicViewModelFactory = SharedViewModelFactory(sharedViewModel)
        val musicViewModel: MusicViewModel =
            ViewModelProvider(this, musicViewModelFactory)[MusicViewModel::class.java]
        playbackController = PlaybackController(application, musicViewModel)
        val factory = PlayerViewModelFactory(application, playbackController)
        playerViewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]
        val albumViewModel: AlbumViewModel = ViewModelProvider(
            this,
            MusicViewModelFactory(application, musicViewModel)
        )[AlbumViewModel::class.java]
        val playlistViewModel: PlaylistViewModel = ViewModelProvider(
            this,
            MusicViewModelFactory(application, musicViewModel)
        )[PlaylistViewModel::class.java]


        super.onCreate(savedInstanceState)
        checkAndRequestPermissions(this)
        enableEdgeToEdge()

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var isDarkTheme: Boolean by remember { mutableStateOf(systemDarkTheme) }
            val toggleTheme: () -> Unit = {
                isDarkTheme = !isDarkTheme
            }

            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.isAppearanceLightStatusBars = isDarkTheme

            IIMusicaTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                AppNavGraph(
                    navController,
                    this,
                    toggleTheme,
                    sharedViewModel,
                    musicViewModel,
                    playerViewModel,
                    albumViewModel,
                    playlistViewModel
                )
            }
        }
    }
}