package com.example.iimusica.components.mediacomponents.topbars

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.iimusica.R
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
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (descending) 90f else 270f,
                        label = "SortIconRotation"
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.pointerico),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(16.dp)
                            .rotate(rotationAngle),
                        tint = appColors.icon
                    )
                }
            }
        },
        onClick = onClick,
    )
}
