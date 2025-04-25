package com.example.iimusica.components.mediacomponents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iimusica.types.SortOption
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun SortOptionItem(
    option: SortOption,
    selected: Boolean,
    descending: Boolean,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = option.displayName,
                    color = appColors.font,
                    fontSize = Typography.bodySmall.fontSize,
                    modifier = Modifier.padding(start = 24.dp)
                )
                if (selected) {
                    Icon(
                        imageVector = if (descending) Icons.Filled.KeyboardArrowDown
                        else Icons.Filled.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp),
                        tint = appColors.icon
                    )
                }
            }
        },
        onClick = onClick,
    )
}
