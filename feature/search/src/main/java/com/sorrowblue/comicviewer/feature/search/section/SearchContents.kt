package com.sorrowblue.comicviewer.feature.search.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.feature.search.R
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFileSearching
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

internal sealed interface SearchContentsAction {
    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        SearchContentsAction

    data class FileInfo(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        SearchContentsAction
}

internal data class SearchContentsUiState(
    val query: String = "",
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
)

@Composable
internal fun SearchContents(
    uiState: SearchContentsUiState,
    lazyPagingItems: LazyPagingItems<File>,
    lazyListState: LazyGridState,
    onAction: (SearchContentsAction) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawFileSearching,
            text = stringResource(id = R.string.search_label_not_found, uiState.query),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .imePadding()
        )
    } else {
//        FileLazyVerticalGrid(
//            uiState = FileLazyVerticalGridUiState(fileListDisplay = FileListDisplay.List),
//            state = lazyListState,
//            contentPadding = contentPadding,
//            lazyPagingItems = lazyPagingItems,
//            onItemClick = { onAction(SearchContentsAction.File(it)) },
//            onItemInfoClick = { onAction(SearchContentsAction.FileInfo(it)) }
//        )
    }
}
