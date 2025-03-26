package com.example.iimusica.components

import com.example.iimusica.MusicFile

fun List<MusicFile>.sortFiles(selectedSortOption: SortOption, isDescending: Boolean): List<MusicFile> {
    return when (selectedSortOption) {
        SortOption.NAME -> this.sortedWith(
            if (isDescending) compareByDescending { it.name.lowercase() }
            else compareBy { it.name.lowercase() }
        )
        SortOption.ARTIST -> this.sortedWith(
            if (isDescending) compareByDescending { it.artist.lowercase() }
            else compareBy { it.artist.lowercase() }
        )
        SortOption.SIZE -> this.sortedWith(
            if (isDescending) compareBy { it.size }
            else compareByDescending { it.size }
        )
        SortOption.DURATION -> this.sortedWith(
            if (isDescending) compareBy { it.duration }
            else compareByDescending { it.duration }
        )

        SortOption.DATE -> this.sortedWith(
            if (isDescending) compareBy { it.dateAdded }
            else compareByDescending { it.dateAdded }
        )
    }
}