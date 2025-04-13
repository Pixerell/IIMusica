package com.example.iimusica.components.mediacomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.iimusica.R
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.types.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicTopBar(
    searchQuery: String,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    selectedSortOption: SortOption,
    isDescending: Boolean,
    toggleTheme: () -> Unit

) {
    var expanded by remember { mutableStateOf(false) } // Controls the dropdown visibility
    val appColors = LocalAppColors.current

    TopAppBar(
        title = {
            if (isSearching) {
                SearchBar(searchQuery, onSearchQueryChange)
            } else {
                Text(
                    text = "IIMusica",
                    style = LocalTextStyle.current.copy(
                        fontSize = Typography.headlineMedium.fontSize,
                        color = appColors.font,
                        fontFamily = Typography.headlineLarge.fontFamily,
                        fontWeight = Typography.headlineLarge.fontWeight
                    ),
                    modifier = Modifier.clickable { toggleTheme() }
                )
            }
        },
        actions = {
            IconButton(onClick = {
                if (isSearching && searchQuery.isNotEmpty()) {
                    onSearchQueryChange("")
                } else {
                    onToggleSearch()
                }
            }) {
                Icon(
                    painter = painterResource(id = if (isSearching) R.drawable.cancelico else R.drawable.searchico),
                    contentDescription = if (isSearching) "Clear Search" else "Search",
                    tint = appColors.icon,
                    modifier = Modifier.size(28.dp)
                )
            }
            // Sort Button
            IconButton(onClick = { expanded = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.moreico),
                    contentDescription = "Sort",
                    tint = appColors.icon,
                    modifier = Modifier.size(28.dp)

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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = appColors.background),
        modifier = Modifier
            .shadow(
                16.dp,
                shape = RectangleShape,
                ambientColor = appColors.font,
                spotColor = appColors.font
            )
    )

}
