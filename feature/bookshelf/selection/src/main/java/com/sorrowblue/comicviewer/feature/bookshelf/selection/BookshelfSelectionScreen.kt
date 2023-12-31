package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.section.BookshelfSourceList
import com.sorrowblue.comicviewer.framework.ui.ResponsiveDialogScaffold
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun BookshelfSelectionScreen(
    onCloseClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    contentPadding: PaddingValues,
    state: BookshelfSelectionScreenState = rememberBookshelfSelectionScreenState(),
) {
    val uiState = state.uiState
    BookshelfSelectionScreen(
        uiState = uiState,
        lazyListState = state.lazyListState,
        onCloseClick = onCloseClick,
        onSourceClick = onSourceClick,
        contentPadding = contentPadding
    )
}

internal data class BookshelfSelectionScreenUiState(
    val list: PersistentList<BookshelfType> = List(10) {
        BookshelfType.entries
    }.flatten().toPersistentList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfSelectionScreen(
    uiState: BookshelfSelectionScreenUiState,
    lazyListState: LazyListState,
    onCloseClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    contentPadding: PaddingValues,
) {
    ResponsiveDialogScaffold(
        title = { Text(text = stringResource(id = R.string.bookshelf_selection_title)) },
        onCloseClick = onCloseClick,
        contentPadding = contentPadding,
        scrollableState = lazyListState
    ) { innerPadding ->
        BookshelfSourceList(
            items = uiState.list,
            onSourceClick = onSourceClick,
            state = lazyListState,
            contentPadding = innerPadding
        )
    }
}
