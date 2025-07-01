@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.example.iimusica.components.mediacomponents

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.PositionalThreshold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.types.MusicFile
import com.example.iimusica.ui.theme.AppColors
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
@Composable
fun MusicListWithPull(
    musicFiles: List<MusicFile>,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    listState: LazyListState,
    isRefreshing: Boolean,
    pullState: PullToRefreshState,
    appColors: AppColors,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        state = pullState,
        isRefreshing = isRefreshing,
        indicator = {
            Indicator(
                isRefreshing = isRefreshing,
                containerColor = appColors.backgroundDarker,
                color = appColors.icon,
                state = pullState,
                threshold = PositionalThreshold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(90.dp)
                    .padding(16.dp)
            )
        },
        onRefresh = onRefresh
    ) {
        MusicList(
            musicFiles = musicFiles,
            navController = navController,
            playerViewModel = playerViewModel,
            listState = listState
        )
    }
}
