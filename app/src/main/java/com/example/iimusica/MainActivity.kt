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
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.example.iimusica.player.PlaybackController
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.screens.PlayerViewModelFactory
import com.example.iimusica.ui.theme.IIMusicaTheme

@UnstableApi
class MainActivity : ComponentActivity() {
    private lateinit var playerViewModel: PlayerViewModel
    lateinit var playbackController: PlaybackController

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        playbackController = PlaybackController(application)
        val factory = PlayerViewModelFactory(application, playbackController)
        playerViewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]

        super.onCreate(savedInstanceState)
        checkAndRequestPermissions(this)
        enableEdgeToEdge()

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var isDarkTheme: Boolean by remember { mutableStateOf(systemDarkTheme) }
            val toggleTheme: () -> Unit = {
                isDarkTheme = !isDarkTheme
            }

            IIMusicaTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                AppNavGraph(navController, this, toggleTheme, playerViewModel)
            }
        }


    }


}