package com.sorrowblue.comicviewer.feature.search.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.material3.SingleChoiceChipRow

data class SearchConditionSheetUiState(
    val range: Range = Range.BOOKSHELF,
    val period: Period = Period.NONE,
    val order: Order = Order.NAME,
    val sort: Sort = Sort.ASC
) {

    enum class Range {
        BOOKSHELF, IN_FOLDER, FOLDER_BELOW
    }

    enum class Period {
        NONE, HOUR_24, WEEK_1, MONTH_1
    }

    enum class Order {
        NAME, TIMESTAMP, SIZE
    }

    enum class Sort {
        ASC, DESC
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchConditionSheet(
    uiState: SearchConditionSheetUiState = SearchConditionSheetUiState(),
    onChangeRange: (SearchConditionSheetUiState.Range) -> Unit = {},
    onChangePeriod: (SearchConditionSheetUiState.Period) -> Unit= {},
    onChangeSort: (SearchConditionSheetUiState.Sort) -> Unit= {},
    onChangeOrder: (SearchConditionSheetUiState.Order) -> Unit= {}
) {
    Column(Modifier.padding(AppMaterialTheme.dimens.margin)) {
        SingleChoiceChipRow {
            SearchConditionSheetUiState.Range.entries.forEachIndexed { index, searchRange ->
                FilterChip(
                    selected = index == uiState.range.ordinal,
                    onClick = { onChangeRange(SearchConditionSheetUiState.Range.entries[index]) },
                    label = { Text(searchRange.name) }
                )
            }
        }
        SingleChoiceChipRow {
            SearchConditionSheetUiState.Period.entries.forEachIndexed { index, searchPeriod ->
                FilterChip(
                    selected = index == uiState.period.ordinal,
                    onClick = { onChangePeriod(SearchConditionSheetUiState.Period.entries[index]) },
                    label = { Text(searchPeriod.name) }
                )
            }
        }
        SingleChoiceChipRow {
            SearchConditionSheetUiState.Order.entries.forEachIndexed { index, searchOrder ->
                FilterChip(
                    selected = index == uiState.order.ordinal,
                    onClick = { onChangeOrder(SearchConditionSheetUiState.Order.entries[index]) },
                    label = { Text(searchOrder.name) }
                )
            }
        }
        SingleChoiceChipRow {
            SearchConditionSheetUiState.Sort.entries.forEachIndexed { index, searchSort ->
                FilterChip(
                    selected = index == uiState.sort.ordinal,
                    onClick = { onChangeSort(SearchConditionSheetUiState.Sort.entries[index]) },
                    label = { Text(searchSort.name) }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearchConditionSheet() {
    AppMaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            SearchConditionSheet()
        }
    }
}
