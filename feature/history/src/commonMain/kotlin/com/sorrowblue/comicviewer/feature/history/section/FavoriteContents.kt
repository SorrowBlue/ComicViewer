package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.history.generated.resources.Res
import comicviewer.feature.history.generated.resources.history_label_no_history
import org.jetbrains.compose.resources.stringResource

internal sealed interface HistoryContentsAction {

    data class Book(val book: com.sorrowblue.comicviewer.domain.model.file.Book) :
        HistoryContentsAction

    data class FileInfo(val file: File) :
        HistoryContentsAction
}

@Composable
internal fun FavoriteContents(
    lazyPagingItems: LazyPagingItems<Book>,
    lazyGridState: LazyGridState,
    onAction: (HistoryContentsAction) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawResumeFolder,
            text = stringResource(Res.string.history_label_no_history),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    } else {
        FileLazyVerticalGrid(
            uiState = remember { FileLazyVerticalGridUiState(fileListDisplay = FileListDisplay.List) },
            state = lazyGridState,
            lazyPagingItems = lazyPagingItems,
            onItemClick = { onAction(HistoryContentsAction.Book(it)) },
            onItemInfoClick = { onAction(HistoryContentsAction.FileInfo(it)) },
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize()
        )
    }
}
