package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.navargs.primitives.booleanNavType
import com.ramcosta.composedestinations.result.ResultRecipient
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.destinations.BookshelfDeleteScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoMainContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoMainContentsUiState
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BottomActions
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.ErrorContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.LoadingContents
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.navigation.resultRecipient
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeSmbServer
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCanonicalScaffold

interface BookshelfInfoSheetNavigator {
    fun notificationRequest()
    fun onEditClick(id: BookshelfId)
    fun onRemoveClick(bookshelfId: BookshelfId)
}

@Composable
fun BookshelfInfoSheet(
    bookshelfId: BookshelfId,
    onCloseClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    navigator: BookshelfInfoSheetNavigator,
    modifier: Modifier = Modifier,
    removeDialogResultRecipient: ResultRecipient<BookshelfDeleteScreenDestination, Boolean> =
        resultRecipient(booleanNavType),
) {
    val state = rememberBookshelfInfoSheetState(
        bookshelfId = bookshelfId,
        snackbarHostState = snackbarHostState
    )
    val scrollState = rememberScrollState()
    BookshelfInfoSheet(
        onAction = state::onAction,
        scrollState = scrollState,
        modifier = modifier,
    ) { contentPadding ->
        when (val uiState = state.uiState) {
            is BookshelfInfoSheetUiState.Loaded ->
                BookshelfInfoMainContents(
                    bookshelfFolder = uiState.bookshelfFolder,
                    showNotificationPermissionRationale = navigator::notificationRequest,
                    snackbarHostState = snackbarHostState,
                    contentPadding = contentPadding,
                    modifier = Modifier.verticalScroll(scrollState)
                )

            BookshelfInfoSheetUiState.Loading ->
                LoadingContents(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                )

            BookshelfInfoSheetUiState.Error ->
                ErrorContents(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                )
        }
    }

    removeDialogResultRecipient.onNavResult(state::onRemoveResult)

    EventEffect(state.events) {
        when (it) {
            is BookshelfInfoSheetStateEvent.Edit -> navigator.onEditClick(it.id)
            is BookshelfInfoSheetStateEvent.Remove -> navigator.onRemoveClick(it.bookshelfId)
            BookshelfInfoSheetStateEvent.Back -> onCloseClick()
        }
    }
}

internal sealed interface BookshelfInfoSheetUiState {
    data object Loading : BookshelfInfoSheetUiState
    data object Error : BookshelfInfoSheetUiState
    data class Loaded(val bookshelfFolder: BookshelfFolder) : BookshelfInfoSheetUiState
}

internal sealed interface BookshelfInfoSheetAction {
    data object Close : BookshelfInfoSheetAction
    data object Edit : BookshelfInfoSheetAction
    data object Remove : BookshelfInfoSheetAction
}

@Composable
private fun BookshelfInfoSheet(
    onAction: (BookshelfInfoSheetAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.(PaddingValues) -> Unit,
) {
    ExtraPaneScaffold(
        title = { Text(text = stringResource(id = R.string.bookshelf_info_title)) },
        onCloseClick = { onAction(BookshelfInfoSheetAction.Close) },
        scrollState = scrollState,
        actions = {
            BottomActions(
                onEditClick = { onAction(BookshelfInfoSheetAction.Edit) },
                onRemoveClick = { onAction(BookshelfInfoSheetAction.Remove) }
            )
        },
        content = content,
        modifier = modifier
    )
}

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
