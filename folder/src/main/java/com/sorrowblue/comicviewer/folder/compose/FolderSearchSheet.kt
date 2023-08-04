package com.sorrowblue.comicviewer.folder.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.FilterAlt
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.folder.SearchOrder
import com.sorrowblue.comicviewer.folder.SearchPeriod
import com.sorrowblue.comicviewer.folder.SearchRange
import com.sorrowblue.comicviewer.folder.SearchSort
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderSearchSheet(
    query: String, onChangeQuery: (String) -> Unit,
    range: SearchRange, onChangeRange: (SearchRange) -> Unit,
    period: SearchPeriod, onChangePeriod: (SearchPeriod) -> Unit,
    order: SearchOrder, onChangeOrder: (SearchOrder) -> Unit,
    sort: SearchSort, onChangeSort: (SearchSort) -> Unit,
    searchResultItems: LazyPagingItems<File>,
    onCloseSearchView: () -> Unit,
    onClick: (File) -> Unit,
    bottomSheetScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded,
            skipHiddenState = false
        )
    )
) {
    val scope = rememberCoroutineScope()

    Backdrop(
        frontLayer = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (bottomSheetScaffoldState.bottomSheetState.targetValue != SheetValue.Expanded) 0.75f else 1f)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${searchResultItems.itemCount} result",
                            modifier = Modifier
                                .weight(1f, true)
                                .padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(
                            onClick = { scope.launch { bottomSheetScaffoldState.bottomSheetState.expand() } }
                        ) {
                            if (bottomSheetScaffoldState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded) {
                                Icon(Icons.TwoTone.KeyboardArrowUp, "")
                            }
                        }
                    }
                    Divider(Modifier.padding(horizontal = AppMaterialTheme.dimens.margin))
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                    ) {
                        items(
                            searchResultItems.itemCount,
                            key = searchResultItems.itemKey { "${it.bookshelfId}${it.path}" }
                        ) { index ->
                            val file = searchResultItems[index]
                            FileList(file, onClick)
                        }
                    }
                }
                if (bottomSheetScaffoldState.bottomSheetState.targetValue != SheetValue.Expanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                scope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
                            })
                }
            }
        },
        toolbar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        modifier = Modifier.border(0.dp, Color.Transparent),
                        value = query,
                        onValueChange = onChangeQuery,
                        placeholder = {
                            Text(text = "Search")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (bottomSheetScaffoldState.bottomSheetState.targetValue != SheetValue.Expanded) {
                            scope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        } else {
                            onCloseSearchView()
                        }
                    }) {
                        if (bottomSheetScaffoldState.bottomSheetState.targetValue != SheetValue.Expanded) {
                            Icon(Icons.TwoTone.Close, "")
                        } else {
                            Icon(Icons.TwoTone.ArrowBack, "")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            bottomSheetScaffoldState.bottomSheetState.partialExpand()
                        }
                    }) {
                        Icon(Icons.TwoTone.FilterAlt, "")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        backLayer = {
            Column(
                Modifier.padding(AppMaterialTheme.dimens.margin)
            ) {
                FilterChipGroup(
                    "検索場所",
                    range.ordinal,
                    {
                        onChangeRange(SearchRange.values()[it])
                    },
                    remember { listOf("本棚全体", "フォルダ内", "フォルダ以下") }
                )
                FilterChipGroup(
                    "検索期間",
                    period.ordinal,
                    { onChangePeriod(SearchPeriod.values()[it]) },
                    remember { listOf("期間指定なし", "24時間以内", "1週間以内", "一ヶ月以内") }
                )
                FilterChipGroup(
                    "Order by",
                    order.ordinal,
                    { onChangeOrder(SearchOrder.values()[it]) },
                    remember { listOf("File name", "Timestamp", "File size") }
                )
                FilterChipGroup(
                    "Sort by",
                    sort.ordinal,
                    { onChangeSort(SearchSort.values()[it]) },
                    remember { listOf("ASC", "DESC") }
                )
            }
        },
        bottomSheetScaffoldState
    )
    LaunchedEffect(query, range, period, order, sort) {
        searchResultItems.refresh()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewFolderSearchSheet(
    @PreviewParameter(PreviewSheetValueProvider::class) sheetValue: SheetValue
) {
    val list = List(20) {
        BookFile(
            BookshelfId(it),
            "Name $it",
            "",
            "",
            0,
            0,
            "",
            0,
            it,
            0
        )
    }
    val data = PagingData.from<File>(list)
    val flow = MutableStateFlow(data)
    val lazyPagingItems = flow.collectAsLazyPagingItems()
    FolderSearchSheet(
        query = "Search query",
        onChangeQuery = {},
        range = SearchRange.BOOKSHELF,
        onChangeRange = {},
        period = SearchPeriod.NONE,
        onChangePeriod = {},
        order = SearchOrder.NAME,
        onChangeOrder = {},
        sort = SearchSort.ASC,
        onChangeSort = {},
        searchResultItems = lazyPagingItems,
        onCloseSearchView = { /*TODO*/ },
        onClick = {},
        bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(
                initialValue = sheetValue,
                skipHiddenState = false,
            )
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
class PreviewSheetValueProvider : PreviewParameterProvider<SheetValue> {
    override val values get() = sequenceOf(SheetValue.Expanded, SheetValue.PartiallyExpanded)
}
