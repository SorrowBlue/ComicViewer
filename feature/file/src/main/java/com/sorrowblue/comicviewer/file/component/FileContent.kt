package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeBottom
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeTop
import com.sorrowblue.comicviewer.framework.ui.add

@Stable
data class FileContentUiState(
    val fileContentType: FileContentType = FileContentType.Grid(),
)

@Composable
fun FileContent(
    type: FileContentType,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
    state: LazyGridState = rememberLazyGridState(),
) {
    when (type) {
        is FileContentType.Grid -> FileGridContent(
            columns = type.columns,
            state = state,
            contentPadding = contentPadding,
            lazyPagingItems = lazyPagingItems,
            onClickItem = onClickItem,
            onLongClickItem = onLongClickItem
        )

        FileContentType.List -> FileListContent(
            columns = type.columns,
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding,
            onClickItem = onClickItem,
            onLongClickItem = onLongClickItem,
            state = state
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileGridContent(
    columns: GridCells,
    state: LazyGridState,
    contentPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<File>,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
) {
    val isCompat = LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact
    LazyVerticalGrid(
        columns = columns,
        state = state,
        contentPadding = contentPadding.add(
            paddingValues = PaddingValues(
                top = if (isCompat) 0.dp else ComicTheme.dimension.margin,
                start = if (isCompat) ComicTheme.dimension.margin else 0.dp,
                end = ComicTheme.dimension.margin,
                bottom = ComicTheme.dimension.margin
            )
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            lazyPagingItems[it]?.let { item ->
                FileGrid(
                    file = item,
                    onClick = { onClickItem(item) },
                    onLongClick = { onLongClickItem(item) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}


@Composable
private fun FileListContent(
    columns: GridCells,
    state: LazyGridState,
    contentPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<File>,
    onClickItem: (File) -> Unit,
    onLongClickItem: (File) -> Unit,
) {
    val isCompat = LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact
    LazyVerticalGrid(
        columns = columns,
        state = state,
        contentPadding = contentPadding.add(
            paddingValues = PaddingValues(
                top = if (isCompat) 0.dp else ComicTheme.dimension.margin,
                end = if (isCompat) 0.dp else ComicTheme.dimension.margin,
                bottom = if (isCompat) 0.dp else ComicTheme.dimension.margin,
            )
        ),
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
                val modifier = when (it) {
                    0 -> Modifier.clip(ComicTheme.shapes.largeTop)
                    lazyPagingItems.itemCount - 1 -> Modifier.clip(ComicTheme.shapes.largeBottom)
                    else -> Modifier
                }
                FileListMedium(
                    file = item,
                    onClick = { onClickItem(item!!) },
                    onLongClick = { onLongClickItem(item!!) },
                    modifier = modifier
                )
            }
        }
    }
}
