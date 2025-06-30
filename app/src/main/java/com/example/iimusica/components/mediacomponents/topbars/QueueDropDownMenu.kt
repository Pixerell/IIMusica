package com.example.iimusica.components.mediacomponents.topbars


import android.app.Activity
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.R
import com.example.iimusica.components.innerShadow
import com.example.iimusica.components.ux.animations.rememberRotationAnimation
import com.example.iimusica.core.player.PlaybackService
import com.example.iimusica.types.ANIM_SPEED_VERYSHORT
import com.example.iimusica.types.QueueActions
import com.example.iimusica.types.QueueOption
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import kotlinx.coroutines.launch


@OptIn(UnstableApi::class)
@Composable
fun QueueDropDownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onReshuffle: () -> Unit,
    queueActions: QueueActions,
    snackbarHostState: SnackbarHostState
) {
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var queueOptionsExpanded by remember { mutableStateOf(false) }
    val rotation = rememberRotationAnimation(queueOptionsExpanded)

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

        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.albumico),
                        contentDescription = "Play Album",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),
                        tint = appColors.icon
                    )
                    Text(
                        text = "Play Album",
                        color = appColors.font,
                        fontSize = Typography.bodyMedium.fontSize,
                        fontFamily = Typography.bodyMedium.fontFamily
                    )
                }
            },
            onClick = {
                // play album and set as the queue.
            }
        )
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.queueico),
                        contentDescription = "Queue options",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .rotate(rotation)
                            .size(20.dp),
                        tint = appColors.icon
                    )
                    Text(
                        text = "Queue Options",
                        color = appColors.font,
                        fontSize = Typography.bodyMedium.fontSize,
                        fontFamily = Typography.bodyMedium.fontFamily
                    )
                }
            },
            onClick = {
                queueOptionsExpanded = !queueOptionsExpanded
            }
        )
        AnimatedContent(
            targetState = queueOptionsExpanded,
            transitionSpec = {
                fadeIn(tween(ANIM_SPEED_VERYSHORT)).togetherWith(fadeOut(tween(ANIM_SPEED_VERYSHORT)))
            },
            label = "QueueDropdownTransition"
        ) { expanded ->
            if (expanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    QueueOption.entries.forEach { option ->
                        QueueOptionItem(option = option, onClick = {
                            when (option) {
                                QueueOption.SET -> queueActions.onSetQueue()
                                QueueOption.ADD -> queueActions.onAddToQueue()
                                QueueOption.REMOVE -> queueActions.onRemoveFromQueue()
                                QueueOption.CLEAR -> queueActions.onClearQueue()
                                QueueOption.RESET -> queueActions.onResetQueue()
                                QueueOption.SAVE -> queueActions.onSaveQueue()
                                QueueOption.GO -> queueActions.onGoToQueue()
                            }
                            queueOptionsExpanded = false
                            onDismissRequest()
                        })
                    }
                }
            }
        }


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