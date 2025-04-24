package com.example.iimusica.components.mediacomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.types.SortOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SortDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    selectedSortOption: SortOption,
    isDescending: Boolean
) {
    val appColors = LocalAppColors.current
    var isSortByExpanded by remember { mutableStateOf(false) }
    val sortOptions = remember { mutableStateOf<List<SortOption>>(emptyList()) }
    LaunchedEffect(expanded) {
        // If expanded, load the sort options in the background to avoid blocking the main thread
        if (expanded) {
            withContext(Dispatchers.IO) {
                // Simulate a delay or heavy loading work (e.g., fetching from a database or network)
                val loadedOptions = SortOption.entries // Here we use the predefined entries, you could fetch from a repository
                withContext(Dispatchers.Main) {
                    // Update the UI with the data once loading is complete
                    sortOptions.value = loadedOptions
                }
            }
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(8.dp),
        containerColor = appColors.backgroundDarker,
    ) {
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = if (isDescending) "Descending" else "Ascending",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = appColors.icon
                    )
                    Text(
                        text = "Sort by",
                        color = appColors.font,
                        fontSize = Typography.bodyMedium.fontSize,
                        fontFamily = Typography.bodyMedium.fontFamily
                    )
                }
            },
            onClick = {
                isSortByExpanded = !isSortByExpanded
            },
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .animateContentSize()
        ) {
        // Once the Sort by section is expanded, show options
        if (isSortByExpanded) {
            // Preload the dropdown items, just display them immediately without recomposition

                sortOptions.value.forEach { option ->
                    key(option) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = option.displayName,
                                        color = appColors.font,
                                        fontSize = Typography.bodyMedium.fontSize,
                                        fontFamily = Typography.bodyMedium.fontFamily
                                    )
                                    // Add the sorting direction indicator
                                    if (selectedSortOption == option) {
                                        Icon(
                                            if (isDescending) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                            contentDescription = if (isDescending) "Descending" else "Ascending",
                                            modifier = Modifier.padding(start = 8.dp),
                                            tint = appColors.icon
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onSortOptionSelected(option)
                                onDismissRequest()
                            }
                        )
                    }
                }
            }
        }

        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = if (isDescending) "Descending" else "Ascending",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = appColors.icon
                    )
                    Text(
                        text = "new dropdown tab",
                        color = appColors.font,
                        fontSize = Typography.bodyMedium.fontSize,
                        fontFamily = Typography.bodyMedium.fontFamily
                    )
                }
            },
            onClick = {}
        )
    }
}
