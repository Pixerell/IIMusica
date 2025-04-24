package com.example.iimusica.components.mediacomponents

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography

@OptIn(UnstableApi::class)
@Composable
fun SearchBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    val appColors = LocalAppColors.current

    BasicTextField(
        value = searchQuery,
        onValueChange = {
            onSearchQueryChange(it)
        },
        textStyle = TextStyle(
            color = appColors.font,
            fontSize = Typography.bodyMedium.fontSize,
            fontFamily = Typography.bodyMedium.fontFamily
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(),
        modifier = Modifier.fillMaxWidth()
    )

    if (searchQuery.isEmpty()) {
        Text(
            text = "Search Music...",
            style = TextStyle(
                fontSize = Typography.bodyMedium.fontSize,
                color = appColors.secondaryFont,
                fontFamily = Typography.bodyMedium.fontFamily
            )
        )
    }
}
