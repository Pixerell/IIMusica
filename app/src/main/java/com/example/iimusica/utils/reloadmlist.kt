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
    playerViewModel.stopPlay()
    viewModel.animationComplete.value = false
    viewModel.loadMusicFiles(context)
    viewModel.isFirstTimeEnteredMusic = true
    viewModel.miniPlayerVisible.value = false
    viewModel.isSearching.value = false
    viewModel.searchQuery.value = ""

}