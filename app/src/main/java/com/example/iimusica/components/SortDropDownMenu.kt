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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    selectedSortOption: SortOption,
    isDescending: Boolean
) {
    // Sort dropdown
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(8.dp),
        containerColor = Color(0xFF000000),


    ) {
        SortOption.entries.forEach { option ->
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = option.displayName,
                            color = Color.White,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                        )
                        // Add the sorting direction indicator
                        if (selectedSortOption == option) {
                            Icon(
                                if (isDescending) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                contentDescription = if (isDescending) "Descending" else "Ascending",
                                modifier = Modifier.padding(start = 8.dp),
                                tint = Color.White
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
