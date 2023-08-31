package com.sorrowblue.comicviewer.folder.section

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.file.FileListType
import com.sorrowblue.comicviewer.folder.component.FileItem
import com.sorrowblue.comicviewer.folder.component.FileList
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.copy

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListSheet(
    fileListType: FileListType,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
    state: LazyGridState = rememberLazyGridState(),
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(fileListType.spanCount),
        state = state,
        contentPadding = when (fileListType) {
            is FileListType.Grid -> contentPadding.copy(AppMaterialTheme.dimens.margin)
            FileListType.List -> contentPadding
        },
        verticalArrangement = when (fileListType) {
            is FileListType.Grid -> Arrangement.spacedBy(
                AppMaterialTheme.dimens.spacer
            )

            FileListType.List -> Arrangement.Top
        },
        horizontalArrangement = when (fileListType) {
            is FileListType.Grid -> Arrangement.spacedBy(
                AppMaterialTheme.dimens.spacer
            )

            FileListType.List -> Arrangement.Start
        }
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            val item = lazyPagingItems[it]
            when (fileListType) {
                is FileListType.Grid -> FileItem(
                    file = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = { onLongClickItem(item!!) },
                            onClick = { onClickItem(item!!) }
                        )
                )

                FileListType.List -> FileList(
                    file = lazyPagingItems[it],
                    modifier = Modifier
                        .combinedClickable(
                            onLongClick = { onLongClickItem(item!!) },
                            onClick = { onClickItem(item!!) }
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListSheet2(
    fileListType: FileListType,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(fileListType.spanCount),
        state = state,
        contentPadding = when (fileListType) {
            is FileListType.Grid -> contentPadding.copy(AppMaterialTheme.dimens.margin)
            FileListType.List -> contentPadding
        },
        verticalItemSpacing = when (fileListType) {
            is FileListType.Grid -> AppMaterialTheme.dimens.spacer
            FileListType.List -> 0.dp
        },
        horizontalArrangement = when (fileListType) {
            is FileListType.Grid -> Arrangement.spacedBy(
                AppMaterialTheme.dimens.spacer
            )

            FileListType.List -> Arrangement.Start
        }
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            val item = lazyPagingItems[it]
            when (fileListType) {
                is FileListType.Grid -> FileItem(
                    file = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = { onLongClickItem(item!!) },
                            onClick = { onClickItem(item!!) }
                        )
                )

                FileListType.List -> FileList(
                    file = lazyPagingItems[it],
                    modifier = Modifier
                        .combinedClickable(
                            onLongClick = { onLongClickItem(item!!) },
                            onClick = { onClickItem(item!!) }
                        )
                )
            }
        }
    }
}
