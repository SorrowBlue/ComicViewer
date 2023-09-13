package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

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
    state: LazyGridState = rememberLazyGridState()
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
    state: LazyGridState
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(AppMaterialTheme.dimens.margin),
        columns = GridCells.Adaptive(
            when (size) {
                FileContentLayout.GridSize.Small -> 88.dp
                FileContentLayout.GridSize.Medium -> 120.dp
                FileContentLayout.GridSize.Large -> 180.dp
            }
        ),
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
    state: LazyGridState
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(AppMaterialTheme.dimens.margin),
        columns = GridCells.Fixed(1),
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            val item = lazyPagingItems[it]
            FileList(
                file = item,
                onClick = { onClickItem(item!!) },
                onLongClick = { onLongClickItem(item!!) },
            )
        }
    }
}
