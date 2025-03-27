package com.example.iimusica.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import com.example.iimusica.utils.MusicFile
import com.example.iimusica.R
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun MusicItem(music: MusicFile, navController: NavController, isLastItem: Boolean) {

    val appColors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("music_detail/${Uri.encode(music.path)}")
            }
            .padding(vertical = 2.dp)
            .then(
                if (isLastItem) Modifier.shadow(
                    8
                        .dp, shape = RectangleShape, ambientColor = appColors.font, spotColor = appColors.font
                ) else Modifier
            )            .background(color = appColors.background),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberAsyncImagePainter(
            model = music.albumArtUri ?: R.drawable.default_image
        )

        val imageModifier = if (music.albumArtUri == null) {
            Modifier.size(60.dp) // Smaller size for the default image
        } else {
            Modifier.size(80.dp) // Regular size for other images
        }


        Box(
            modifier = Modifier
                .size(80.dp)  // Fixed size for the Box that wraps the image
                .wrapContentSize(Alignment.Center)
        ) {
            Image(
                painter = painter,
                contentDescription = "Album Art",
                modifier = imageModifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentScale = ContentScale.FillBounds
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)

        ) {
            Text(
                text = music.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = Typography.bodyMedium.fontSize, color = appColors.font, fontFamily = Typography.bodyLarge.fontFamily),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Text(text = music.artist, style = TextStyle(fontSize = Typography.bodySmall.fontSize, color = appColors.font, fontFamily = Typography.bodySmall.fontFamily),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
        }
    }
}