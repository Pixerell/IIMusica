package com.example.iimusica.components.mediacomponents.topbars


import android.app.Activity
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.R
import com.example.iimusica.components.innerShadow
import com.example.iimusica.core.player.PlaybackService
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun QueueDropDownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onReshuffle: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val appColors = LocalAppColors.current
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .innerShadow(
                shape = RectangleShape,
                color = appColors.font.copy(alpha = 0.4f),
                blur = 8.dp,
                offsetY = 6.dp,
                offsetX = 0.dp,
                spread = 0.dp
            )
            .padding(8.dp),
        containerColor = appColors.backgroundDarker,
    ) {
        HorizontalDivider(
            modifier = Modifier
                .alpha(0.4f)
                .fillMaxWidth(),
            color = appColors.font,
            thickness = 0.6.dp
        )
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.shuffleico),
                        contentDescription = "Reshuffling",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),
                        tint = appColors.icon
                    )
                    Text(
                        text = "Reshuffle",
                        color = appColors.font,
                        fontSize = Typography.bodyMedium.fontSize,
                        fontFamily = Typography.bodyMedium.fontFamily
                    )
                }
            },
            onClick = {
                onReshuffle()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Queue reshuffled",
                        withDismissAction = true
                    )
                }
            }
        )

        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close Button",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = appColors.icon
                    )
                    Text(
                        text = "Close",
                        color = appColors.font,
                        fontSize = Typography.bodyMedium.fontSize,
                        fontFamily = Typography.bodyMedium.fontFamily
                    )
                }
            },
            onClick = {
                if (context is Activity) {
                    val serviceIntent = Intent(context, PlaybackService::class.java)
                    context.stopService(serviceIntent)
                    context.finish()
                }
            }
        )
    }
}