package com.example.iimusica.components.mediacomponents.topbars

import android.app.Activity
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.iimusica.core.player.PlaybackService
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.types.SortOption
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun SettingsDropDownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    selectedSortOption: SortOption,
    isDescending: Boolean,
    onReshuffle: () -> Unit,
    onReloadLocalFiles: () -> Unit,
    onToggleDescending: () -> Unit,
    onNavToQueue: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val appColors = LocalAppColors.current
    var isSortByExpanded by remember { mutableStateOf(false) }
    val sortOptions = remember { SortOption.entries }
    var lastToggleTime by remember { mutableLongStateOf(0L) }
    val context = LocalContext.current

    val rotation by animateFloatAsState(
        targetValue = if (isSortByExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "SortArrowRotation"
    )

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
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.expanderico),
                        contentDescription = "Sort expander",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp)
                            .rotate(rotation),
                        tint = appColors.icon
                    )
                    Text(
                        text = "Sort by",
                        color = appColors.font,
                        fontWeight = if (isSortByExpanded) Typography.headlineMedium.fontWeight else Typography.bodyMedium.fontWeight,
                        fontSize = Typography.bodyMedium.fontSize,
                        fontFamily = Typography.bodyMedium.fontFamily
                    )
                }
            },
            onClick = {
                val now = System.currentTimeMillis()
                if (now - lastToggleTime > 200) {
                    isSortByExpanded = !isSortByExpanded
                    lastToggleTime = now
                }
            },
        )
        // Once the Sort by section is expanded, show options
        AnimatedVisibility(
            isSortByExpanded,
            enter = expandVertically(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .animateContentSize()
            ) {
                sortOptions.forEach { option ->
                    SortOptionItem(
                        option = option,
                        selected = option == selectedSortOption,
                        descending = isDescending,
                        onClick = {
                            onSortOptionSelected(option)
                            onToggleDescending()
                        }
                    )
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
                        painter = painterResource(R.drawable.queueico),
                        contentDescription = "Navigate To Queue Screen",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),
                        tint = appColors.icon
                    )
                    Text(
                        text = "View Queue",
                        color = appColors.font,
                        fontSize = Typography.bodyMedium.fontSize,
                        fontFamily = Typography.bodyMedium.fontFamily
                    )
                }
            },
            onClick = {
                onDismissRequest()
                onNavToQueue()
            }
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
                        painter = painterResource(id = R.drawable.refreshico),
                        contentDescription = "Reload mfiles",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),
                        tint = appColors.icon
                    )
                    Text(
                        text = "Reload local files",
                        color = appColors.font,
                        fontSize = Typography.bodyMedium.fontSize,
                        fontFamily = Typography.bodyMedium.fontFamily
                    )
                }
            },
            onClick = {
                onReloadLocalFiles()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Reloading Files",
                        withDismissAction = true
                    )
                }
            }
        )

        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.cancelico),
                        contentDescription = "Close button",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),
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