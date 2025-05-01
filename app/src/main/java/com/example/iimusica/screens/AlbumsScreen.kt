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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.components.mediacomponents.AlbumItem
import com.example.iimusica.components.ux.InfoBox
import com.example.iimusica.components.ux.Loader
import com.example.iimusica.components.ux.MessageType
import com.example.iimusica.ui.theme.LocalAppColors

@OptIn(UnstableApi::class)
@Composable
fun AlbumsScreen(
    albumViewModel: AlbumViewModel,
) {
    val appColors = LocalAppColors.current
    val albums = albumViewModel.albums.value
    val isLoading by albumViewModel.isLoading
    val errorMessage = albumViewModel.errorMessage

    Box(
        modifier = Modifier.fillMaxSize().background(appColors.accentGradient)
    )
    {
        if (isLoading) {
            Loader(modifier = Modifier.align(Alignment.Center))
        } else if (errorMessage.isNotEmpty()) {
            InfoBox(
                message = "There was an error $errorMessage",
                type = MessageType.Error,
            )
        }
        else if (albums.isEmpty()) {
            InfoBox(
                message = "No suitable Albums found, perhaps check the metadata?",
                type = MessageType.Warning,
            )
        }
        else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columns
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(albums) { album ->
                    Log.d("albumz", "albums - ${album.name} and artist ${album.artist} its song? ${album.representativeSong}")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // Makes it square-ish
                            .background(appColors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        AlbumItem(album)
                    }
                }
            }
        }
    }
}