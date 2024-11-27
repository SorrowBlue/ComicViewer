package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalExtraPaneScaffold

internal sealed interface BookshelfInfoSheetWrapperUiState {
    data object Loading : BookshelfInfoSheetWrapperUiState
    data class Loaded(val bookshelfFolder: BookshelfFolder) : BookshelfInfoSheetWrapperUiState
}

@Composable
internal fun BookshelfInfoSheetWrapper(
    bookshelfId: BookshelfId,
    content: @Composable (BookshelfFolder) -> Unit,
) {
    val state = rememberBookshelfInfoSheetWrapperState(
        bookshelfId = bookshelfId,
    )
    when (val uiState = state.uiState) {
        is BookshelfInfoSheetWrapperUiState.Loaded -> content(uiState.bookshelfFolder)
        BookshelfInfoSheetWrapperUiState.Loading ->
            CanonicalExtraPaneScaffold(title = {}, onCloseClick = {}) { }
    }
}
