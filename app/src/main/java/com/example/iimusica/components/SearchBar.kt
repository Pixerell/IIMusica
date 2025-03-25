package com.example.iimusica.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SearchBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    BasicTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        textStyle = TextStyle(color = Color.White, fontSize = MaterialTheme.typography.bodyMedium.fontSize, fontFamily = MaterialTheme.typography.bodyMedium.fontFamily),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(),
        modifier = Modifier.fillMaxWidth()
    )

    if (searchQuery.isEmpty()) {
        Text(
            text = "Search Music...",
            style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize, color = Color.Gray, fontFamily = MaterialTheme.typography.bodyMedium.fontFamily)
        )
    }
}
