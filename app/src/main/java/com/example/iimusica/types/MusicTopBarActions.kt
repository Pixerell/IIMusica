package com.example.iimusica.types

data class MusicTopBarActions(
    val onSearchQueryChange: (String) -> Unit,
    val onToggleSearch: () -> Unit,
    val onSortOptionSelected: (SortOption) -> Unit,
    val toggleTheme: () -> Unit,
    val onReshuffle: () -> Unit,
    val onReloadLocalFiles: () -> Unit,
)
