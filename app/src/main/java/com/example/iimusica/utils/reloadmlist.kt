package com.example.iimusica.utils

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.core.viewmodels.MusicViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.core.viewmodels.SharedViewModel
import com.example.iimusica.types.DEFAULT_QUEUE_NAME


@OptIn(UnstableApi::class)
fun reloadmlist(
    playerViewModel: PlayerViewModel,
    musicViewModel: MusicViewModel,
    sharedViewModel: SharedViewModel,
    context: Context,
) {
    val activePath = playerViewModel.currentPath.value
    val wasPlaying = playerViewModel.isPlaying
    playerViewModel.stopPlay()
    sharedViewModel.animationComplete.value = false
    musicViewModel.loadMusicFiles(context)
    sharedViewModel.isFirstTimeEnteredMusic = true
    sharedViewModel.miniPlayerVisible.value = false
    sharedViewModel.clearAllSearches()

    // Only reset queue if necessary
    if (playerViewModel.queueManager.getQueue() != musicViewModel.mFiles.value) {
        playerViewModel.queueManager.setQueue(musicViewModel.mFiles.value, DEFAULT_QUEUE_NAME)
    }

    if (wasPlaying && activePath != null) {
        val restoredIndex = playerViewModel.queueManager.findIndexByPath(activePath)
        playerViewModel.queueManager.setCurrentIndex(restoredIndex)
        playerViewModel.queueManager.setShuffledIndex(restoredIndex)
        playerViewModel.setCurrentPath(activePath, true)
        playerViewModel.pause()
        sharedViewModel.isFirstTimeEnteredMusic = false
    }
}