package com.example.iimusica.components.ux


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.iimusica.R
import com.example.iimusica.components.innerShadow
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun CustomSnackBar(data: SnackbarData) {
    val appColors = LocalAppColors.current

    Box(
        modifier = Modifier
            .background(
                color = appColors.backgroundDarker,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
            .innerShadow(
                shape = RoundedCornerShape(16.dp),
                color = appColors.font.copy(alpha = 0.4f),
                blur = 4.dp,
                offsetY = 6.dp,
                offsetX = 0.dp,
                spread = 0.dp
            )
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.infobox),
                contentDescription = "Info",
                tint = appColors.font,
                modifier = Modifier
                    .padding(end = 32.dp)
                    .size(20.dp)
            )
            Text(
                text = data.visuals.message,
                style = Typography.bodyMedium.copy(color = appColors.font),
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.cancelico),
                contentDescription = "Close",
                tint = appColors.font,
                modifier = Modifier
                    .clickable { data.dismiss() }
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }
    }
}