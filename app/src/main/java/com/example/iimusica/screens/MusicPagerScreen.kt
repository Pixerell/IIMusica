package com.example.iimusica.screens

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController

@OptIn(UnstableApi::class)
@Composable
fun MusicPagerScreen(
    navController: NavController,
    toggleTheme: () -> Unit,
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    context: Context
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 }) // 2 pages: list + another screen

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> MusicListScreen(
                navController = navController,
                context = context,
                toggleTheme = toggleTheme,
                viewModel = musicViewModel,
                playerViewModel = playerViewModel
            )
            1 -> PlaylistScreen()
        }
    }
}