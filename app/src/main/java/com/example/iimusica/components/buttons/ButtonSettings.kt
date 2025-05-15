package com.example.iimusica.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.iimusica.R
import com.example.iimusica.components.mediacomponents.topbars.SettingsDropDownMenu
import com.example.iimusica.types.SortOption
import com.example.iimusica.ui.theme.LocalAppColors

@Composable
fun ButtonSettings(
    selectedSortOption: SortOption,
    isDescending: Boolean,
    onSortOptionSelected: (SortOption) -> Unit,
    onReshuffle: () -> Unit,
    onReloadLocalFiles: () -> Unit,
    onToggleDescending: () -> Unit,
    onNavToQueue: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val appColors = LocalAppColors.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable { expanded = !expanded },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.moreico),
            contentDescription = "Settings",
            tint = appColors.icon,
            modifier = Modifier.size(32.dp)
        )
        SettingsDropDownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            onSortOptionSelected = onSortOptionSelected,
            selectedSortOption = selectedSortOption,
            isDescending = isDescending,
            onReshuffle = {
                onReshuffle()
                expanded = false
            },
            onReloadLocalFiles = {
                onReloadLocalFiles()
                expanded = false
            },
            onToggleDescending = onToggleDescending,
            onNavToQueue = onNavToQueue,
            snackbarHostState = snackbarHostState
        )
    }
}