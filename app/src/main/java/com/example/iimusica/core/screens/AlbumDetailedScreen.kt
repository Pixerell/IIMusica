package com.example.iimusica.core.screens

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.mediacomponents.AlbumDetailsMainContent
import com.example.iimusica.components.mediacomponents.topbars.AlbumDetailsTopBar
import com.example.iimusica.components.ux.Loader
import com.example.iimusica.core.viewmodels.AlbumViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.types.Album
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(UnstableApi::class)
@Composable
fun AlbumDetailedScreen(
    albumId: String,
    navController: NavController,
    albumViewModel: AlbumViewModel,
    playerViewModel: PlayerViewModel,
    snackbarHostState: SnackbarHostState
) {
    val albumIdLong = albumId.toLongOrNull() ?: return
    val albumState = produceState<Album?>(initialValue = null, albumIdLong) {
        value = albumViewModel.getAlbumById(albumIdLong)
    }
    val album = albumState.value


    val appColors = LocalAppColors.current
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE


    if (album == null) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Loader(modifier = Modifier.padding(bottom = 16.dp))
                Text(
                    text = "Loading...",
                    fontStyle = Typography.bodyLarge.fontStyle,
                    fontSize = Typography.bodyLarge.fontSize,
                    fontWeight = Typography.bodyLarge.fontWeight,
                    color = appColors.secondaryFont,
                )
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        AlbumDetailsTopBar(
            album = album,
            onReshuffle = { playerViewModel.queueManager.regenerateShuffleOrder() },
            onBackClick = { navController.navigateUp() },
            snackbarHostState = snackbarHostState
        )
        AlbumDetailsMainContent(isLandscape, playerViewModel,albumViewModel, album, navController)
    }
}