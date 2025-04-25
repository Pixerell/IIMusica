package com.example.iimusica.utils

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.screens.MusicViewModel
import com.example.iimusica.screens.PlayerViewModel

@OptIn(UnstableApi::class)
fun reloadmlist(
    playerViewModel: PlayerViewModel,
    viewModel: MusicViewModel,
    context: Context,
) {
    val activePath = playerViewModel.currentPath.value
    val wasPlaying = playerViewModel.isPlaying
    playerViewModel.stopPlay()
    viewModel.animationComplete.value = false
    viewModel.loadMusicFiles(context)
    viewModel.isFirstTimeEnteredMusic = true
    viewModel.miniPlayerVisible.value = false
    viewModel.isSearching.value = false
    viewModel.searchQuery.value = ""

    // Only reset queue if necessary
    if (playerViewModel.queueManager.getQueue() != viewModel.mFiles.value) {
        playerViewModel.queueManager.setQueue(viewModel.mFiles.value)
    }

    if (wasPlaying && activePath != null) {
        val restoredIndex = playerViewModel.queueManager.findIndexByPath(activePath)
        playerViewModel.queueManager.setCurrentIndex(restoredIndex)
        playerViewModel.queueManager.setShuffledIndex(restoredIndex)
        playerViewModel.setCurrentPath(activePath, true)
        playerViewModel.pause()
        viewModel.isFirstTimeEnteredMusic = false
    }
}