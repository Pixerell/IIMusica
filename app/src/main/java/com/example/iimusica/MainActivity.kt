package com.example.iimusica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.iimusica.ui.theme.IIMusicaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
                AppNavGraph(navController, this, toggleTheme)
            }
        }

    }
}