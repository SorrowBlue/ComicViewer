package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeBottom
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeTop
import com.sorrowblue.comicviewer.framework.ui.add
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
    val isCompat = rememberMobile()
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
            contentPadding = contentPadding.add(PaddingValues(if (isCompat) 0.dp else ComicTheme.dimension.margin)),
            onClickItem = onFileClick,
            onLongClickItem = onInfoClick,
            state = state
        )
    }
}

@Composable
fun <T : File> FileContent2(
    type: FileContentType,
    lazyPagingItems: LazyPagingItems<T>,
    contentPadding: PaddingValues,
    onFileClick: (T) -> Unit,
    onInfoClick: (T) -> Unit,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    val isCompat = rememberMobile()
    when (type) {
        is FileContentType.Grid -> FileGridContent2(
            columns = type.columns2,
            state = state,
            contentPadding = contentPadding,
            lazyPagingItems = lazyPagingItems,
            onFileClick = onFileClick,
            onInfoClick = onInfoClick
        )

        FileContentType.List -> FileListContent2(
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding.add(PaddingValues(if (isCompat) 0.dp else ComicTheme.dimension.margin)),
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
    LazyVerticalGrid(
        columns = columns,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T : File> FileGridContent2(
    columns: StaggeredGridCells,
    state: LazyStaggeredGridState,
    contentPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<T>,
    onFileClick: (T) -> Unit,
    onInfoClick: (T) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        columns = columns,
        state = state,
        contentPadding = contentPadding,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            lazyPagingItems[it]?.let { item ->
                FileGrid(
                    file = item,
                    onClick = { onFileClick(item) },
                    onInfoClick = { onInfoClick(item) },
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
        contentPadding = contentPadding,
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
                    onLongClick = { onLongClickItem(item!!) },
                    modifier = when (it) {
                        0 -> Modifier.clip(ComicTheme.shapes.largeTop)
                        lazyPagingItems.itemCount - 1 -> Modifier.clip(ComicTheme.shapes.largeBottom)
                        else -> Modifier
                    }
                )
            }
        }
    }
}

@Composable
fun <T : File> FileListContent2(
    state: LazyStaggeredGridState,
    contentPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<T>,
    onClickItem: (T) -> Unit,
    onLongClickItem: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompat = rememberMobile()
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(1),
        state = state,
        contentPadding = contentPadding,
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
                    onLongClick = { onLongClickItem(item!!) },
                    modifier = when (it) {
                        0 -> Modifier.clip(ComicTheme.shapes.largeTop)
                        lazyPagingItems.itemCount - 1 -> Modifier.clip(ComicTheme.shapes.largeBottom)
                        else -> Modifier
                    }
                )
            }
        }
    }
}
