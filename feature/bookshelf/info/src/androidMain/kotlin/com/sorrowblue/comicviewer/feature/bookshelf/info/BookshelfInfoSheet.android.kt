package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoMainContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoMainContentsUiState
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.ErrorContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.LoadingContents
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeSmbServer
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCanonicalScaffold

@PreviewMultiScreen
@Composable
private fun BookshelfInfoSheetPreview(
    @PreviewParameter(BookshelfInfoSheetConfig::class) uiState: BookshelfInfoSheetUiState,
) {
    val navigator = rememberSupportingPaneScaffoldNavigator(
        initialDestinationHistory = listOf(
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "")
        )
    )
    PreviewCanonicalScaffold(
        navigator = navigator,
        extraPane = {
            BookshelfInfoSheet(onAction = {}) { contentPadding ->
                when (uiState) {
                    BookshelfInfoSheetUiState.Loading ->
                        LoadingContents(
                            Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                        )

                    BookshelfInfoSheetUiState.Error ->
                        ErrorContents(
                            Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                        )

                    is BookshelfInfoSheetUiState.Loaded ->
                        BookshelfInfoMainContents(
                            uiState = BookshelfInfoMainContentsUiState(
                                uiState.bookshelfFolder.bookshelf,
                                uiState.bookshelfFolder.folder
                            ),
                            lazyPagingItems = PagingData.flowData(10) {
                                BookThumbnail(
                                    BookshelfId(),
                                    "$it",
                                    0,
                                    0,
                                    0
                                )
                            }.collectAsLazyPagingItems(),
                            onScanFileClick = {},
                            onScanThumbnailClick = {},
                            contentPadding = contentPadding,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                }
            }
        }
    ) {
    }
}

private class BookshelfInfoSheetConfig : PreviewParameterProvider<BookshelfInfoSheetUiState> {
    override val values: Sequence<BookshelfInfoSheetUiState> = sequenceOf(
        BookshelfInfoSheetUiState.Loading,
        BookshelfInfoSheetUiState.Error,
        BookshelfInfoSheetUiState.Loaded(BookshelfFolder(fakeSmbServer(), fakeFolder()))
    )
}
