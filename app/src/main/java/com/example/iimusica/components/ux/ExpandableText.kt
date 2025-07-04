package com.example.iimusica.components.ux

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.iimusica.types.ANIM_SPEED_VERYSHORT


@Composable
fun ExpandableText(
    text: String,
    color: Color,
    style: TextStyle,
    minLines : Int = 1,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        style = style,
        maxLines = if (expanded) Int.MAX_VALUE else minLines,
        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
        modifier = modifier
            .clickable { expanded = !expanded }
            .animateContentSize(
                animationSpec = tween(durationMillis = ANIM_SPEED_VERYSHORT)
            )
    )
}
