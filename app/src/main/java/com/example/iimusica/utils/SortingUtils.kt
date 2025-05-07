package com.example.iimusica.utils

import com.example.iimusica.types.SortOption

fun <T> List<T>.sortByOption(
    sortOption: SortOption,
    isDescending: Boolean,
    selector: (T) -> String?,
    numericSelector: (T) -> Long?
): List<T> {
    return when (sortOption) {
        SortOption.NAME, SortOption.ARTIST -> {
            if (isDescending)
                this.sortedWith(compareByDescending { selector(it)?.lowercase() ?: "" })
            else
                this.sortedWith(compareBy { selector(it)?.lowercase() ?: "" })
        }

        SortOption.SIZE, SortOption.DURATION, SortOption.DATE -> {
            if (isDescending)
                this.sortedWith(compareBy { numericSelector(it) ?: 0L })
            else
                this.sortedWith(compareByDescending { numericSelector(it) ?: 0L })
        }
    }
}
