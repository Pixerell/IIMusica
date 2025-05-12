package com.example.iimusica.components.buttons


import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.R
import com.example.iimusica.components.innerShadow
import com.example.iimusica.core.viewmodels.MusicViewModel
import com.example.iimusica.core.viewmodels.PlayerViewModel
import com.example.iimusica.core.viewmodels.SharedViewModel
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.utils.reloadmlist

@UnstableApi
@Composable
fun ButtonReload(
    playerViewModel: PlayerViewModel,
    viewModel: MusicViewModel,
    sharedViewModel: SharedViewModel,
    context: Context
) {
    val appColors = LocalAppColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = {
                reloadmlist(playerViewModel, viewModel, sharedViewModel, context)
            },
            modifier = Modifier
                .padding(16.dp)
                .innerShadow(
                    shape = RoundedCornerShape(16.dp),
                    color = appColors.font.copy(alpha = 0.4f),
                    blur = 4.dp,
                    offsetY = 6.dp,
                    offsetX = 0.dp,
                    spread = 0.dp
                )
                .align(Alignment.BottomCenter),
            contentColor = appColors.icon,
            containerColor = appColors.backgroundDarker,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.refreshico),
                modifier = Modifier.size(20.dp),
                contentDescription = "Refresh Music Files"
            )
        }
    }
}