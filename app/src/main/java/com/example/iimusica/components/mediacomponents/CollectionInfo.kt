package com.example.iimusica.components.mediacomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iimusica.components.ux.ExpandableText
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@Composable
fun CollectionInfo(
    bitrateKbps: String,
    genre: String,
    songCountText: String,
    year: String,
    storageSize: String,
    totalDuration: String,
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExpandableText(
                        text = bitrateKbps,
                        color = appColors.secondaryFont,
                        style = Typography.bodyMedium
                    )
                    ExpandableText(
                        text = genre,
                        color = appColors.secondaryFont,
                        style = Typography.bodyMedium
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExpandableText(
                        text = songCountText,
                        color = appColors.font,
                        style = Typography.bodyMedium
                    )
                    ExpandableText(
                        text = year,
                        color = appColors.secondaryFont,
                        style = Typography.bodyMedium
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExpandableText(
                        text = storageSize,
                        color = appColors.secondaryFont,
                        style = Typography.bodyMedium
                    )
                    ExpandableText(
                        text = totalDuration,
                        color = appColors.secondaryFont,
                        style = Typography.bodyMedium
                    )
                }
            }
        }
    }
}
