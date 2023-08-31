package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

sealed interface FileContentLayout {

    val columns: StaggeredGridCells

    data object List : FileContentLayout {
        override val columns get() = StaggeredGridCells.Fixed(1)
    }

    data class Grid(val size: GridSize) : FileContentLayout {

        override val columns: StaggeredGridCells
            get() = StaggeredGridCells.Adaptive(
                when (size) {
                    GridSize.Small -> 88.dp
                    GridSize.Medium -> 120.dp
                    GridSize.Large -> 180.dp
                }
            )
    }

    enum class GridSize {
        Small, Medium, Large
    }
}

data class FileContentUiState(
    val layout: FileContentLayout
)

@Composable
fun FileContent(
    uiState: FileContentUiState,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    LazyVerticalStaggeredGrid(
        columns = uiState.layout.columns,
        state = state,
        modifier = Modifier.padding(horizontal = AppMaterialTheme.dimens.margin),
        contentPadding = contentPadding,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            val item = lazyPagingItems[it]
            when (uiState.layout) {
                is FileContentLayout.Grid -> FileGrid(
                    file = item,
                    onClick = { onClickItem(item!!) },
                    onLongClick = { onLongClickItem(item!!) },
                )

                FileContentLayout.List -> FileList(
                    file = lazyPagingItems[it],
                    onClick = { onClickItem(item!!) },
                    onLongClick = { onLongClickItem(item!!) },
                )
            }
        }
    }
}

@Composable
private fun FileGridContent(
    size: FileContentLayout.GridSize,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
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
