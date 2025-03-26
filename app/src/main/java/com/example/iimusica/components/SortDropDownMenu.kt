package com.example.iimusica.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    selectedSortOption: SortOption,
    isDescending: Boolean
) {
    val appColors = LocalAppColors.current

    // Sort dropdown
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(8.dp),
        containerColor = appColors.backgroundDarker,


    ) {
        SortOption.entries.forEach { option ->
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = option.displayName,
                            color =  appColors.font,
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
