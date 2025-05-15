package com.example.iimusica.components.mediacomponents.topbars

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.iimusica.R
import com.example.iimusica.components.buttons.ButtonBack
import com.example.iimusica.components.innerShadow
import com.example.iimusica.components.ux.MarqueeText
import com.example.iimusica.components.ux.ShadowBox
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun QueueTopBar(
    onBackClick: () -> Unit,
    onReshuffle: () -> Unit,
    queueName: String = "Current Queue",
    snackbarHostState: SnackbarHostState
) {
    val appColors = LocalAppColors.current
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var dropDownExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(appColors.background)
            .innerShadow(
                shape = RectangleShape,
                color = appColors.font.copy(alpha = 0.4f),
                blur = 8.dp,
                offsetY = 6.dp
            )
    ) {
        ShadowBox(modifier = Modifier.align(Alignment.BottomCenter))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (isLandscape) 8.dp else 24.dp)
        ) {
            ButtonBack(onBackClick)

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                MarqueeText(
                    text = queueName,
                    style = Typography.bodyLarge,
                    isCentered = true
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp)
                    .clickable { dropDownExpanded = !dropDownExpanded },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.queueico),
                    contentDescription = "Queue Options",
                    tint = appColors.icon,
                    modifier = Modifier.size(32.dp)
                )

                QueueDropDownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = { dropDownExpanded = false },
                    onReshuffle = onReshuffle,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}