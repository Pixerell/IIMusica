package com.example.iimusica.components.mediacomponents

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.components.buttons.ButtonNext
import com.example.iimusica.components.buttons.ButtonPlayPause
import com.example.iimusica.components.buttons.ButtonPrevious
import com.example.iimusica.components.buttons.ButtonRepeat
import com.example.iimusica.components.buttons.ButtonShuffle
import com.example.iimusica.components.ux.MarqueeText
import com.example.iimusica.screens.PlayerViewModel
import com.example.iimusica.types.MusicFile
import com.example.iimusica.ui.theme.AppColors
import com.example.iimusica.ui.theme.QUEUE_PANEL_OFFSET
import com.example.iimusica.utils.parseDuration

@OptIn(UnstableApi::class)
@Composable
fun MusicScreenMainContent(isLandscape: Boolean, playerViewModel: PlayerViewModel, musicFile: MusicFile?, painter: Painter, appColors: AppColors) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(
                vertical = if (isLandscape) 24.dp else 64.dp
            )
            .padding(end = if (isLandscape) 32.dp else 0.dp)

    ) {
        val imageSize =
            if (isLandscape) this.maxWidth * 0.25f else this.maxWidth * 0.95f
        Image(
            painter = painter,
            contentDescription = "Album Art",
            modifier = Modifier
                .apply {
                    if (isLandscape) {
                        height(maxHeight - QUEUE_PANEL_OFFSET)
                    }
                }
                .size(imageSize)
                .align(Alignment.Center))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = if (isLandscape) 24.dp else 0.dp)
    ) {
        MarqueeText(
            text = musicFile!!.name, modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "by ${musicFile.artist}",
            color = appColors.secondaryFont,
            fontSize = 18.sp
        )

        if (!isLandscape) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        DurationBar(
            duration = parseDuration(musicFile.duration.toString()),
            playerViewModel = playerViewModel
        )

        Row(
            modifier = Modifier
                .padding(vertical = if (isLandscape) 20.dp else 32.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonShuffle(
                playerViewModel, modifier = Modifier
                    .weight(1f)
                    .size(28.dp)
            )
            ButtonPrevious(
                playerViewModel, modifier = Modifier
                    .weight(1f)
                    .size(28.dp)
            )
            ButtonPlayPause(playerViewModel)
            ButtonNext(
                playerViewModel, modifier = Modifier
                    .weight(1f)
                    .size(28.dp)
            )
            ButtonRepeat(
                playerViewModel, modifier = Modifier
                    .weight(1f)
                    .size(28.dp)
            )
        }
    }
}