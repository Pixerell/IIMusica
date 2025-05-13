package com.example.iimusica.utils

import com.example.iimusica.core.viewmodels.SearchSortState
import com.example.iimusica.types.SortOption

fun <T> filterAndSortList(
    originalList: List<T>,
    state: SearchSortState,
    querySelector: (T) -> String,
    stringSelector: (T) -> String?,
    numericSelector: (T) -> Long?
): List<T> {
    val filtered = if (state.query.isBlank()) {
        originalList
    } else {
        originalList.filter { querySelector(it).contains(state.query, ignoreCase = true) }
    }

    return filtered.sortByOption(
        sortOption = state.sortOption,
        isDescending = state.isDescending,
        selector = {
            when (state.sortOption) {
                SortOption.NAME, SortOption.ARTIST -> stringSelector(it)
                else -> null
            }
        },
        numericSelector = {
            when (state.sortOption) {
                SortOption.SIZE, SortOption.DURATION, SortOption.DATE -> numericSelector(it)
                else -> null
            }
        }
    )
}