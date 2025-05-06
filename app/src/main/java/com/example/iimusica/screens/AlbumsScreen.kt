package com.example.iimusica.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.components.mediacomponents.AlbumItem
import com.example.iimusica.components.ux.InfoBox
import com.example.iimusica.components.ux.Loader
import com.example.iimusica.components.ux.MessageType
import com.example.iimusica.ui.theme.LocalAppColors

@OptIn(UnstableApi::class)
@Composable
fun AlbumsScreen(
    albumViewModel: AlbumViewModel,
    navController: NavController
) {
    val appColors = LocalAppColors.current
    val albums = albumViewModel.albums.value
    val isLoading by albumViewModel.isLoading
    val errorMessage = albumViewModel.errorMessage
    val gridState = rememberLazyGridState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.accentGradient)
    )
    {
        if (isLoading) {
            Loader(modifier = Modifier.align(Alignment.Center))
        } else if (errorMessage.isNotEmpty()) {
            InfoBox(
                message = "There was an error $errorMessage",
                type = MessageType.Error,
            )
        } else if (albums.isEmpty()) {
            InfoBox(
                message = "No suitable Albums found, perhaps check the track's metadata? " +
                        "They need to contain AlbumID's",
                type = MessageType.Warning,
            )
        } else {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2), // 2 columns
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(albums) { album ->
                    Log.d(
                        "albumz",
                        "albums - ${album.name} and artist ${album.artist} its song? ${album.representativeSong}"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 4.dp,
                                shape = RectangleShape,
                                ambientColor = appColors.font,
                                spotColor = appColors.font
                            )
                            .aspectRatio(0.78f) // this adjusts height
                            .background(appColors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        AlbumItem(album, navController)
                    }
                }
            }
        }
    }
}