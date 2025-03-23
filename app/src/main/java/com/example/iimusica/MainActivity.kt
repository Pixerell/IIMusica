package com.example.iimusica

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.iimusica.ui.theme.IIMusicaTheme
import android.os.Build

private const val REQUEST_CODE = 1

fun checkAndRequestPermissions(activity: Activity) {
    val permissions = mutableListOf<String>()

    // For devices running Android 10 and below
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
    ) {
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    // For devices running Android 13 and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
        }
    }

    if (permissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), REQUEST_CODE)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions(this)
        enableEdgeToEdge()


        setContent {
            IIMusicaTheme {
                val navController = rememberNavController()
                AppNavGraph(navController, this)
            }
        }
    }
}