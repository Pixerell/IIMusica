package com.example.iimusica.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.iimusica.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicTopBar(
    searchQuery: String,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    selectedSortOption: SortOption,
    isDescending: Boolean

) {
    var expanded by remember { mutableStateOf(false) } // Controls the dropdown visibility

    TopAppBar(
        title = {
            if (isSearching) {
                SearchBar(searchQuery, onSearchQueryChange)
            } else {
                Text(
                    text = "IIMusica",
                    style = LocalTextStyle.current.copy(
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        color = Color.White,
                        fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight
                    )
                )
            }
        },
        actions = {
            IconButton(onClick = {
                if (isSearching && searchQuery.isNotEmpty()) {
                    onSearchQueryChange("") // Clear search when X is pressed
                } else {
                    onToggleSearch()
                }
            }) {
                Icon(
                    painter = painterResource(id = if (isSearching) R.drawable.cancelico else R.drawable.searchico),
                    contentDescription = if (isSearching) "Clear Search" else "Search",
                    tint = Color.White
                )
            }
            // Sort Button
            IconButton(onClick = { expanded = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.moreico),
                    contentDescription = "Sort",
                    tint = Color.White

                )
            }

            // sort dropdown
            SortDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                onSortOptionSelected = onSortOptionSelected,
                selectedSortOption = selectedSortOption,
                isDescending = isDescending
            )

        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF030310))
    )
}
