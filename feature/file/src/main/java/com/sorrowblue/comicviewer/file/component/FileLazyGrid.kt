package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File

enum class FileListType {
    Grid, List
}

@Composable
fun <T : File> FileLazyGrid(
    fileListType: FileListType,
    lazyPagingItems: LazyPagingItems<T>,
    contentPadding: PaddingValues,
    onFileClick: (T) -> Unit,
    onInfoClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    isThumbnailEnabled: Boolean = true,
    state: LazyGridState = rememberLazyGridState(),
) {
    when (fileListType) {
        FileListType.Grid -> {
            GridFileLazyGrid(
                lazyPagingItems = lazyPagingItems,
                columns = GridCells.Fixed(1),
                isThumbnailEnabled = isThumbnailEnabled,
                contentPadding = contentPadding,
                modifier = modifier,
                onClickItem = onFileClick,
                onLongClickItem = onInfoClick,
                state = state,
            )
        }

        FileListType.List ->
            ListFileLazyGrid(
                lazyPagingItems = lazyPagingItems,
                isThumbnailEnabled = isThumbnailEnabled,
                contentPadding = contentPadding,
                modifier = modifier,
                onClickItem = onFileClick,
                onLongClickItem = onInfoClick,
                state = state,
            )
    }
}
