package com.example.iimusica.core.screens


import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.mediacomponents.MusicList
import com.example.iimusica.components.mediacomponents.topbars.QueueTopBar
import com.example.iimusica.components.rememberQueueActions
import com.example.iimusica.core.viewmodels.MusicViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.ui.theme.LocalAppColors


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(UnstableApi::class)
@Composable
fun QueueScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    musicViewModel: MusicViewModel,
    snackbarHostState: SnackbarHostState
) {
    val appColors = LocalAppColors.current
    val queueName = playerViewModel.queueManager.queueName.value
    val queue = playerViewModel.queueManager.getQueue()
    val defaultFiles by musicViewModel.mFiles
    val queueListState = rememberLazyListState()

    val queueActions = rememberQueueActions(
        snackbarHostState = snackbarHostState,
        navController = navController,
        playerViewModel = playerViewModel,
        defaultFiles = defaultFiles,
        songs = queue,
        queueName = queueName
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.background)
    )
    {
        Column {
            QueueTopBar(
                onBackClick = { navController.navigateUp() },
                onReshuffle = { playerViewModel.queueManager.regenerateShuffleOrder() },
                queueName = queueName.toString(),
                queueActions = queueActions,
                snackbarHostState = snackbarHostState
            )

            MusicList(
                musicFiles = queue,
                navController = navController,
                playerViewModel = playerViewModel,
                listState = queueListState
            )
        }

    }
}