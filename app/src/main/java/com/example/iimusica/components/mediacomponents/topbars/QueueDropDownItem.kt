package com.example.iimusica.components.mediacomponents.topbars


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iimusica.types.QueueOption
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun QueueOptionItem(
    option: QueueOption,
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
            }
        },
        onClick = onClick,
    )
}
