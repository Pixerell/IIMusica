package com.example.iimusica.screens

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.iimusica.types.SortOption


data class SearchSortState(
    val query: String = "",
    val sortOption: SortOption = SortOption.NAME,
    val isSearching: Boolean = false,
    val isDescending: Boolean = false,
    val previousSortOption: SortOption = SortOption.NAME
)


enum class ScreenKey {
    Songs, Albums, Artists
}

fun pageToScreenKey(page: Int): ScreenKey = when (page) {
    0 -> ScreenKey.Songs
    1 -> ScreenKey.Albums
    2 -> ScreenKey.Artists
    else -> ScreenKey.Songs
}

class SharedSearchViewModel : ViewModel() {
    private val _screenStates = mutableStateMapOf<ScreenKey, SearchSortState>().apply {
        ScreenKey.entries.forEach { put(it, SearchSortState()) }
    }

    val screenStates: Map<ScreenKey, SearchSortState> = _screenStates

    fun toggleSearch(screen: ScreenKey) {
        val current = _screenStates[screen] ?: SearchSortState()
        _screenStates[screen] = current.copy(isSearching = !current.isSearching)
    }


    fun toggleDescending(screen: ScreenKey) {
        val current = _screenStates[screen] ?: SearchSortState()
        val newDescending = if (current.sortOption == current.previousSortOption) {
            !current.isDescending
        } else {
            false
        }
        _screenStates[screen] = current.copy(isDescending = newDescending, previousSortOption = current.sortOption)
    }
    fun disableSearch(screen: ScreenKey) {
        val current = _screenStates[screen] ?: SearchSortState()
        _screenStates[screen] = current.copy(isSearching = false)
    }


    fun updateQuery(screen: ScreenKey, query: String) {
        _screenStates[screen] = _screenStates[screen]?.copy(query = query) ?: SearchSortState(query = query)
    }

    fun updateSort(screen: ScreenKey, sortOption: SortOption) {
        _screenStates[screen] = _screenStates[screen]?.copy(sortOption = sortOption) ?: SearchSortState(sortOption = sortOption)
    }

    fun getState(screen: ScreenKey): SearchSortState {
        return _screenStates[screen] ?: SearchSortState()
    }
}
