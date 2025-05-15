package com.example.iimusica.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.iimusica.R
import com.example.iimusica.ui.theme.LocalAppColors


@Composable
fun ButtonBack(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    val appColors = LocalAppColors.current
    Box(
        modifier = modifier
            .size(48.dp)
            .clickable { onBackClick() },
        contentAlignment = Alignment.Center
    ) {

        Icon(
            painter = painterResource(id = R.drawable.backicon),
            contentDescription = "Back",
            tint = appColors.icon,
            modifier = Modifier.size(32.dp)
        )
    }
}