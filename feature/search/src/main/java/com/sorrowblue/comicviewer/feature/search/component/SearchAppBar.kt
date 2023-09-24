package com.sorrowblue.comicviewer.feature.search.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.FilterAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sorrowblue.comicviewer.feature.search.SearchScreenUiState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SearchAppBar(
    uiState: SearchScreenUiState,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    toggleSearchFilter: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {
            TextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(text = "Search") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.TwoTone.ArrowBack, "")
            }
        },
        actions = {
            IconButton(onClick = toggleSearchFilter) {
                Icon(Icons.TwoTone.FilterAlt, "")
            }
        },
        scrollBehavior = scrollBehavior
    )
}
