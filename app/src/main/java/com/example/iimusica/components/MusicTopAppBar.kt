package com.example.iimusica.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.example.iimusica.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicTopBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    val isSearching = remember { mutableStateOf(searchQuery.isNotEmpty()) }
    TopAppBar(
        title = {
            if (isSearching.value){
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { newQuery ->
                        onSearchQueryChange(newQuery)
                    },
                    textStyle = TextStyle(color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.W500),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(onSearch = {
                        isSearching.value = false
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search Music...",
                        style = TextStyle(
                            fontWeight = FontWeight.W500,
                            fontSize = 28.sp,
                            color = Color.Gray
                        )
                    )
                }

            }
            else {
                Text(
                    text = "Music Files",
                    style = TextStyle(
                        fontWeight = FontWeight.W500,
                        fontSize = 28.sp,
                        color = Color.White
                    )
                )
            }
        },

        actions = {
            if (isSearching.value) {
                // Show the "X" icon when searching is active
                IconButton(onClick = {

                    if (searchQuery.isNotEmpty()) {
                        onSearchQueryChange("")
                    }
                    else {
                        isSearching.value = false
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.cancelico),
                        contentDescription = "Clear Search",
                        tint = Color.White
                    )
                }
            } else {
                // Show search icon when not searching
                IconButton(onClick = {
                    isSearching.value = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.searchico),
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }
        },


        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF030310))
    )
}