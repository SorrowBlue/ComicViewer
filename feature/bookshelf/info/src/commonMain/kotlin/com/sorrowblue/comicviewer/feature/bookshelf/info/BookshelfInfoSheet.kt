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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoMainContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BottomActions
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.ErrorContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.LoadingContents
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffold
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_title
import org.jetbrains.compose.resources.stringResource

interface BookshelfInfoSheetNavigator {
    fun notificationRequest(type: ScanType)
    fun onEditClick(id: BookshelfId)
    fun onRemoveClick(bookshelfId: BookshelfId)
}

@Composable
fun BookshelfInfoSheet(
    bookshelfId: BookshelfId,
    onCloseClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    navigator: BookshelfInfoSheetNavigator,
    deleteNavResultReceiver: NavResultReceiver<BookshelfDelete, Boolean>,
    notificationNavResultReceiver: NavResultReceiver<NotificationRequest, NotificationRequestResult>,
    modifier: Modifier = Modifier,
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
                    notificationNavResultReceiver = notificationNavResultReceiver,
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

    deleteNavResultReceiver.onNavResult(state::onRemoveResult)

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
internal fun BookshelfInfoSheet(
    onAction: (BookshelfInfoSheetAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.(PaddingValues) -> Unit,
) {
    ExtraPaneScaffold(
        title = { Text(text = stringResource(Res.string.bookshelf_info_title)) },
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
