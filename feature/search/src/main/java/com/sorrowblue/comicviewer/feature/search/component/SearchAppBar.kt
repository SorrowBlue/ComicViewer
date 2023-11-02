package com.sorrowblue.comicviewer.feature.search.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.search.SearchScreenUiState
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionSheetUiState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveTopAppBar
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun SearchAppBar(
    uiState: SearchScreenUiState,
    onBackClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onChangeRange: (SearchConditionSheetUiState.Range) -> Unit,
    onChangePeriod: (SearchConditionSheetUiState.Period) -> Unit,
    onChangeSort: (SearchConditionSheetUiState.Sort) -> Unit,
    onChangeOrder: (SearchConditionSheetUiState.Order) -> Unit,
    contentPadding: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    Column {
        ResponsiveTopAppBar(
            title = {
                TextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    placeholder = { Text(text = "Search") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            onBackClick = onBackClick,
            windowInsets = contentPadding.asWindowInsets(),
            scrollBehavior = scrollBehavior
        )
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .windowInsetsPadding(
                    contentPadding
                        .asWindowInsets()
                        .only(WindowInsetsSides.Horizontal)
                )
                .padding(horizontal = ComicTheme.dimension.margin)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DropdownMenuChip(
                text = uiState.range.name,
                onChangeSelected = onChangeRange,
                menus = remember { SearchConditionSheetUiState.Range.entries.toPersistentList() },
                menu = SearchConditionSheetUiState.Range::name,
                scrollBehavior = scrollBehavior
            )
            DropdownMenuChip(
                text = uiState.period.name,
                onChangeSelected = onChangePeriod,
                menus = remember { SearchConditionSheetUiState.Period.entries.toPersistentList() },
                menu = SearchConditionSheetUiState.Period::name,
                scrollBehavior = scrollBehavior
            )
            DropdownMenuChip(
                text = uiState.order.name,
                onChangeSelected = onChangeOrder,
                menus = remember { SearchConditionSheetUiState.Order.entries.toPersistentList() },
                menu = SearchConditionSheetUiState.Order::name,
                scrollBehavior = scrollBehavior
            )
            DropdownMenuChip(
                text = uiState.sort.name,
                onChangeSelected = onChangeSort,
                menus = remember { SearchConditionSheetUiState.Sort.entries.toPersistentList() },
                menu = SearchConditionSheetUiState.Sort::name,
                scrollBehavior = scrollBehavior
            )
        }
    }

}
