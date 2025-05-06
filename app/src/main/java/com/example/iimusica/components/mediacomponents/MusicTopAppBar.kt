package com.example.iimusica.components.mediacomponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.iimusica.R
import com.example.iimusica.components.innerShadow
import com.example.iimusica.types.MusicTopBarActions
import com.example.iimusica.types.PAGE_TITLES
import com.example.iimusica.ui.theme.LocalAppColors
import com.example.iimusica.ui.theme.Typography
import com.example.iimusica.types.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicTopBar(
    searchQuery: String,
    isSearching: Boolean,
    selectedSortOption: SortOption,
    isDescending: Boolean,
    actions: MusicTopBarActions,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    onToggleDescending: () -> Unit,
    snackbarHostState: SnackbarHostState
) {




    var expanded by remember { mutableStateOf(false) } // Controls the dropdown visibility
    val appColors = LocalAppColors.current
    val pageTitles = PAGE_TITLES

    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val animatedOffsetPx by animateFloatAsState(
        targetValue = currentPage * (screenWidthPx / pageTitles.size),
        animationSpec = tween(333)
    )

    Column(
        modifier = Modifier
            .shadow(
                16.dp,
                shape = RectangleShape,
                ambientColor = appColors.font,
                spotColor = appColors.font
            )
            .innerShadow(
                shape = RectangleShape,
                color = appColors.font.copy(alpha = 0.4f),
                blur = 8.dp,
                offsetY = 6.dp,
                offsetX = 0.dp,
                spread = 0.dp
            )
    ) {
        TopAppBar(
            title = {
                if (isSearching) {
                    SearchBar(searchQuery, actions.onSearchQueryChange)
                } else {
                    Text(
                        text = "IIMusica",
                        style = LocalTextStyle.current.copy(
                            fontSize = Typography.headlineMedium.fontSize,
                            color = appColors.font,
                            fontFamily = Typography.headlineLarge.fontFamily,
                            fontWeight = Typography.headlineLarge.fontWeight
                        ),
                        modifier = Modifier.clickable { actions.toggleTheme() }
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    if (isSearching && searchQuery.isNotEmpty()) {
                        actions.onSearchQueryChange("")
                    } else {
                        actions.onToggleSearch()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = if (isSearching) R.drawable.cancelico else R.drawable.searchico),
                        contentDescription = if (isSearching) "Clear Search" else "Search",
                        tint = appColors.icon,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.moreico),
                        contentDescription = "Sort",
                        tint = appColors.icon,
                        modifier = Modifier.size(28.dp)
                    )
                }
                SettingsDropDownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    onSortOptionSelected = actions.onSortOptionSelected,
                    selectedSortOption = selectedSortOption,
                    isDescending = isDescending,
                    onReshuffle = {
                        actions.onReshuffle()
                        expanded = false
                    },
                    onReloadLocalFiles = {
                        actions.onReloadLocalFiles()
                        expanded = false
                    },
                    onToggleDescending = onToggleDescending,
                    snackbarHostState = snackbarHostState
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = appColors.background),
        )
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(appColors.background)
            ) {
                pageTitles.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPageSelected(index) }
                            .padding(bottom = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = Typography.bodySmall.copy(
                                color = if (index == currentPage) appColors.active else appColors.activeDarker,
                                fontWeight = if (index == currentPage) Typography.headlineLarge.fontWeight else Typography.bodyLarge.fontWeight,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
            // For lines underneath
            Column(
                modifier = Modifier
                    .height(3.dp)
                    .clip(RectangleShape)
                    .background(appColors.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    pageTitles.forEachIndexed { _, _ ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(appColors.activeDarker)
                                .zIndex(1f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(with(density) { (screenWidthPx / pageTitles.size).toDp() }) // width of one tab
                        .offset { IntOffset(animatedOffsetPx.toInt(), 0) } // x animation
                        .offset(y = (-2).dp)
                        .height(3.dp)
                        .background(appColors.active)
                        .zIndex(10f)
                )
            }

        }
    }
}
