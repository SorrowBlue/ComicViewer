package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.domain.model.file.File as FileModel

internal sealed interface CollectionContentsAction {

    data class File(val file: FileModel) : CollectionContentsAction

    data class FileInfo(val file: FileModel) : CollectionContentsAction
}

@Composable
internal fun CollectionContents(
    fileLazyVerticalGridUiState: FileLazyVerticalGridUiState,
    lazyPagingItems: LazyPagingItems<FileModel>,
    lazyGridState: LazyGridState,
    onAction: (CollectionContentsAction) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawResumeFolder,
            text = "No contents",
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    } else {
        FileLazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            uiState = fileLazyVerticalGridUiState,
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding,
            onItemClick = { onAction(CollectionContentsAction.File(it)) },
            onItemInfoClick = { onAction(CollectionContentsAction.FileInfo(it)) },
            state = lazyGridState
        )
    }
}
