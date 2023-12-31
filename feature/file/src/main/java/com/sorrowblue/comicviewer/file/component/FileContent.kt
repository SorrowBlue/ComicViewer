package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile

@Composable
fun <T : File> FileContent(
    type: FileContentType,
    lazyPagingItems: LazyPagingItems<T>,
    contentPadding: PaddingValues,
    onFileClick: (T) -> Unit,
    onInfoClick: (T) -> Unit,
    state: LazyGridState = rememberLazyGridState(),
) {
    when (type) {
        is FileContentType.Grid -> FileGridContent(
            columns = type.columns3,
            state = state,
            contentPadding = contentPadding,
            lazyPagingItems = lazyPagingItems,
            onClickItem = onFileClick,
            onLongClickItem = onInfoClick
        )

        FileContentType.List -> FileListContent(
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding,
            onClickItem = onFileClick,
            onLongClickItem = onInfoClick,
            state = state
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T : File> FileGridContent(
    columns: GridCells,
    state: LazyGridState,
    contentPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<T>,
    onClickItem: (T) -> Unit,
    onLongClickItem: (T) -> Unit,
) {
    var spanCount by remember { mutableStateOf(1) }
    LazyVerticalGrid(
        columns = columns,
        state = state,
        contentPadding = contentPadding.add(PaddingValues(ComicTheme.dimension.margin)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        modifier = Modifier.drawVerticalScrollbar(state, spanCount)
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path },
            span = {
                spanCount = maxLineSpan
                GridItemSpan(1)
            }) {
            lazyPagingItems[it]?.let { item ->
                FileGrid(
                    file = item,
                    onClick = { onClickItem(item) },
                    onInfoClick = { onLongClickItem(item) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

@Composable
fun <T : File> FileListContent(
    state: LazyGridState,
    contentPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<T>,
    onClickItem: (T) -> Unit,
    onLongClickItem: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompat = rememberMobile()
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        state = state,
        verticalArrangement = if (isCompat) Arrangement.Top else Arrangement.spacedBy(8.dp),
        contentPadding = if (isCompat) {
            contentPadding
        } else {
            contentPadding.add(
                PaddingValues(ComicTheme.dimension.margin)
            )
        },
        modifier = modifier,
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            val item = lazyPagingItems[it]
            if (isCompat) {
                FileListContent(
                    file = item,
                    onClick = { onClickItem(item!!) },
                    onLongClick = { onLongClickItem(item!!) },
                )
            } else {
                FileListMedium(
                    file = item,
                    onClick = { onClickItem(item!!) },
                    onLongClick = { onLongClickItem(item!!) }
                )
            }
        }
    }
}
