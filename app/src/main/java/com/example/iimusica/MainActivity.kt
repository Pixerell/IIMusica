package com.example.iimusica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.ui.theme.IIMusicaTheme
import com.example.iimusica.utils.NotificationUtils
import com.example.iimusica.utils.checkAndRequestPermissions

class MainActivity : ComponentActivity() {
    // keep only one player across the app and pass it down as props
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions(this)
        enableEdgeToEdge()
        NotificationUtils.init(applicationContext)

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