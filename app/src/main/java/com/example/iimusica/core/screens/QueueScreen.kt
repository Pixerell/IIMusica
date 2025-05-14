package com.example.iimusica.core.screens


import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.core.viewmodels.PlayerViewModel



@RequiresApi(Build.VERSION_CODES.R)
@OptIn(UnstableApi::class)
@Composable
fun QueueScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    snackbarHostState: SnackbarHostState
) {


}