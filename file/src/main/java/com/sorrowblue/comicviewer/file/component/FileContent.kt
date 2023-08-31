package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.entity.file.File

data class FileContentUiState(
    val layout: FileContentLayout = FileContentLayout.Grid()
)

@Composable
fun FileContent(
    uiState: FileContentUiState,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState()
) {
    when (uiState.layout) {
        is FileContentLayout.Grid -> FileGridContent(
            size = uiState.layout.size,
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding,
            onClickItem = onClickItem,
            onLongClickItem = onLongClickItem,
            state = state
        )

        FileContentLayout.List -> FileListContent(
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding,
            onClickItem = onClickItem,
            onLongClickItem = onLongClickItem,
            state = state
        )
    }
}

@Composable
private fun FileGridContent(
    size: FileContentLayout.GridSize,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
    state: LazyStaggeredGridState
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(
            when (size) {
                FileContentLayout.GridSize.Small -> 88.dp
                FileContentLayout.GridSize.Medium -> 120.dp
                FileContentLayout.GridSize.Large -> 180.dp
            }
        ),
        state = state,
        contentPadding = contentPadding,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            val item = lazyPagingItems[it]
            FileGrid(
                file = item,
                onClick = { onClickItem(item!!) },
                onLongClick = { onLongClickItem(item!!) },
            )
        }
    }
}


@Composable
private fun FileListContent(
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
    state: LazyStaggeredGridState
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(1),
        state = state,
        contentPadding = contentPadding,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            val item = lazyPagingItems[it]
            FileGrid(
                file = item,
                onClick = { onClickItem(item!!) },
                onLongClick = { onLongClickItem(item!!) },
            )
        }
    }
}
