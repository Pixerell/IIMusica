package com.example.iimusica.core.screens


import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.iimusica.core.viewmodels.PlaylistViewModel
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@OptIn(UnstableApi::class)
@Composable
fun PlaylistsScreen(
    playlistViewModel: PlaylistViewModel,
    navController: NavController
) {
    val appColors = LocalAppColors.current
    val gridState = rememberLazyGridState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.accentGradient)
    )
    {

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        }
        Text(
            text = "Playlist Screen",
            fontStyle = Typography.bodyMedium.fontStyle,
            fontSize = Typography.bodyMedium.fontSize,
            fontWeight = Typography.headlineMedium.fontWeight,
            color = appColors.font,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}