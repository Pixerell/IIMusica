package com.example.iimusica.components


import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.iimusica.R
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

enum class MessageType {
    Info, Warning, Error
}


@Composable
fun InfoBox(
    message: String,
    type: MessageType = MessageType.Info,
    mainBoxColor: Color? = null
) {
    val appColors = LocalAppColors.current
    var expanded by remember { mutableStateOf(true) }
    val rotateArrow by animateFloatAsState(if (expanded) 180f else 0f)

    val effectiveBackgroundColor = mainBoxColor ?: appColors.background

    val (icon) = when (type) {
        MessageType.Info -> painterResource(id = R.drawable.infobox) to appColors.icon
        MessageType.Warning -> painterResource(id = R.drawable.warningbox) to appColors.icon
        MessageType.Error -> painterResource(id = R.drawable.errorbox) to appColors.icon
    }
    val title = when (type) {
        MessageType.Info -> "Information"
        MessageType.Warning -> "Warning"
        MessageType.Error -> "Error"
    }

    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = appColors.font.copy(alpha = 0.25f),
                spotColor = appColors.font.copy(alpha = 0.3f),
                clip = false
            )
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(16.dp))
        ) {

            Box {
                Row(
                    modifier = Modifier
                        .background(appColors.backgroundDarker)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        color = appColors.font,
                        style = Typography.bodyLarge,
                        fontWeight = Typography.headlineLarge.fontWeight
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Dropdown infobox",
                        tint = appColors.icon,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotateArrow)
                            .clickable { expanded = !expanded }
                    )
                }

                // Fake bottom-only shadow
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(8.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = RectangleShape,
                            clip = true,
                            ambientColor = appColors.font,
                            spotColor = appColors.font,
                        )
                )
            }

            AnimatedVisibility(visible = expanded) {
                Row(
                    modifier = Modifier
                        .background(effectiveBackgroundColor)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = "InfoBox Icon",
                        tint = appColors.icon,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = message,
                        modifier = Modifier.fillMaxWidth(),
                        color = appColors.font,
                        style = Typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

