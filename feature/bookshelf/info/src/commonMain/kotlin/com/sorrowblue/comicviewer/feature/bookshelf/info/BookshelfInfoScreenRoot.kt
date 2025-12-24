package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfLoadingContents

@Composable
context(context: BookshelfInfoScreenContext)
internal fun BookshelfInfoScreenRoot(
    bookshelfId: BookshelfId,
    onBackClick: () -> Unit,
    onRemoveClick: () -> Unit,
    showNotificationPermissionRationale: (ScanType) -> Unit,
    onEditClick: (BookshelfId, BookshelfType) -> Unit,
) {
    val state = rememberBookshelfInfoScreenState(bookshelfId = bookshelfId)

    val scrollState: ScrollState = rememberScrollState()
    BookshelfInfoScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onRemoveClick = onRemoveClick,
        onEditClick = {
            if (state.uiState is BookshelfInfoSheetUiState.Loaded) {
                onEditClick(
                    bookshelfId,
                    requireNotNull(
                        (state.uiState as BookshelfInfoSheetUiState.Loaded).bookshelfFolder.bookshelf.type,
                    ),
                )
            }
        },
        modifier = Modifier.testTag("BookshelfInfoScreenRoot"),
    ) { contentPadding ->
        when (val uiState = state.uiState) {
            is BookshelfInfoSheetUiState.Loaded -> {
                BookshelfInfoContents(
                    bookshelfFolder = uiState.bookshelfFolder,
                    showNotificationPermissionRationale = showNotificationPermissionRationale,
                    contentPadding = contentPadding,
                    modifier = Modifier.verticalScroll(scrollState),
                )
            }

            BookshelfInfoSheetUiState.Loading ->
                BookshelfLoadingContents(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )

            BookshelfInfoSheetUiState.Error ->
                BookshelfErrorContents(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
        }
    }
}
