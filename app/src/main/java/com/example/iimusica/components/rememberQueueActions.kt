package com.example.iimusica.components

import androidx.annotation.OptIn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.types.MusicFile
import com.example.iimusica.types.QueueActions
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun rememberQueueActions(
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    defaultFiles: List<MusicFile>,
    songs: List<MusicFile>,
    queueName: String
): QueueActions {
    val scope = rememberCoroutineScope()
    return remember(songs, queueName) {
        QueueActions(
            onSetQueue = {
                playerViewModel.queueManager.setQueue(songs, queueName)
                scope.launch { snackbarHostState.showSnackbar("Queue set to $queueName") }
            },
            onAddToQueue = {
                playerViewModel.queueManager.addToQueue(songs)
                scope.launch { snackbarHostState.showSnackbar("$queueName added to queue") }
            },
            onRemoveFromQueue = {
                playerViewModel.queueManager.removeFromQueue(songs)
                scope.launch { snackbarHostState.showSnackbar("$queueName removed from queue") }
            },
            onClearQueue = {
                playerViewModel.queueManager.clearQueue()
                scope.launch { snackbarHostState.showSnackbar("Queue cleared") }
            },
            onResetQueue = {
                playerViewModel.queueManager.resetQueue(defaultFiles)
                scope.launch { snackbarHostState.showSnackbar("Queue reset to default") }
            },
            onSaveQueue = {
                scope.launch { snackbarHostState.showSnackbar("Not implemented yet") }
            },
            onGoToQueue = {
                navController.navigate("queue") {
                    launchSingleTop = true
                }
            }
        )
    }
}
