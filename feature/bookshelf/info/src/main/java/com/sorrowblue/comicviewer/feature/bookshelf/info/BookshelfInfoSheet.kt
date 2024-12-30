package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.navargs.primitives.booleanNavType
import com.ramcosta.composedestinations.result.ResultRecipient
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.info.destinations.BookshelfRemoveDialogDestination
import com.sorrowblue.comicviewer.feature.bookshelf.info.destinations.NotificationRequestDialogDestination
import com.sorrowblue.comicviewer.feature.bookshelf.info.navtype.notificationRequestResultEnumNavType
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfBookThumbnailsCarousel
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoActionChips
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoContent
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BottomActions
import com.sorrowblue.comicviewer.feature.bookshelf.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeSmbServer
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCanonicalScaffold

@Composable
fun BookshelfInfoSheet(
    bookshelfId: BookshelfId,
    navigator: BookshelfInfoSheetNavigator,
) {
    BookshelfInfoSheetWrapper(bookshelfId = bookshelfId) {
        BookshelfInfoSheet(
            bookshelfFolder = it,
            navigator = navigator,
        )
    }
}

internal data class BookshelfInfoSheetUiState(
    val bookshelf: Bookshelf,
    val folder: Folder,
    val enabled: Boolean = true,
    val isProgressScan: Boolean = false,
)

internal sealed interface BookshelfInfoSheetAction {
    data object Remove : BookshelfInfoSheetAction
    data object Edit : BookshelfInfoSheetAction
    data object Scan : BookshelfInfoSheetAction
    data object ThumbnailRegeneration : BookshelfInfoSheetAction
    data object Close : BookshelfInfoSheetAction
}

interface BookshelfInfoSheetNavigator {
    fun notificationRequest()
    fun edit(id: BookshelfId)
    fun remove(bookshelfId: BookshelfId)
    fun navigateBack()
}

@Composable
private fun BookshelfInfoSheet(
    bookshelfFolder: BookshelfFolder,
    navigator: BookshelfInfoSheetNavigator,
    removeDialogResultRecipient: ResultRecipient<BookshelfRemoveDialogDestination, Boolean> =
        resultRecipient(booleanNavType),
    notificationResultRecipient: ResultRecipient<NotificationRequestDialogDestination, NotificationRequestResult> =
        resultRecipient(notificationRequestResultEnumNavType),
    state: BookshelfInfoSheetState = rememberBookshelfInfoSheetState(bookshelfFolder = bookshelfFolder),
) {
    BookshelfInfoSheet(
        uiState = state.uiState,
        onAction = state::onAction,
        lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems(),
    )
    removeDialogResultRecipient.onNavResult(state::onRemoveResult)
    notificationResultRecipient.onNavResult(state::onNotificationRequestResult)

    LaunchedEventEffect(state.event) {
        when (it) {
            is BookshelfInfoSheetStateEvent.Edit -> navigator.edit(it.id)
            is BookshelfInfoSheetStateEvent.ShowRequestPermissionRationale -> navigator.notificationRequest()
            is BookshelfInfoSheetStateEvent.Remove -> navigator.remove(it.bookshelfId)
            BookshelfInfoSheetStateEvent.Back -> navigator.navigateBack()
        }
    }
}

@Composable
private fun BookshelfInfoSheet(
    uiState: BookshelfInfoSheetUiState,
    lazyPagingItems: LazyPagingItems<BookThumbnail>,
    onAction: (BookshelfInfoSheetAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtraPaneScaffold(
        title = { Text(text = stringResource(id = R.string.bookshelf_info_title)) },
        onCloseClick = { onAction(BookshelfInfoSheetAction.Close) },
        actions = {
            BottomActions(
                enabled = uiState.enabled,
                onEditClick = { onAction(BookshelfInfoSheetAction.Edit) },
                onRemoveClick = { onAction(BookshelfInfoSheetAction.Remove) },
            )
        },
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val scrollState = rememberScrollState()
            if (LocalNavigationState.current !is NavigationState.NavigationBar && scrollState.canScrollBackward) {
                HorizontalDivider()
            }
            Column(
                Modifier
                    .weight(1f)
                    .drawVerticalScrollbar(scrollState)
                    .verticalScroll(scrollState)
            ) {
                BookshelfBookThumbnailsCarousel(
                    pagingItems = lazyPagingItems,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                BookshelfInfoActionChips(
                    isProgressScan = uiState.isProgressScan,
                    enabled = uiState.enabled,
                    onScanClick = { onAction(BookshelfInfoSheetAction.Scan) },
                    onReThumbnailsClick = { onAction(BookshelfInfoSheetAction.ThumbnailRegeneration) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
                BookshelfInfoContent(bookshelf = uiState.bookshelf, folder = uiState.folder)
            }
        }
    }
}

@Composable
@PreviewMultiScreen
private fun BookshelfInfoSheetPreview() {
    val navigator = rememberSupportingPaneScaffoldNavigator(
        initialDestinationHistory = listOf(
            ThreePaneScaffoldDestinationItem(
                SupportingPaneScaffoldRole.Extra,
                BookshelfFolder(fakeSmbServer(), fakeFolder())
            )
        )
    )
    PreviewCanonicalScaffold(
        navigator = navigator,
        extraPane = { contentKey ->
            val lazyPagingItems =
                PagingData.flowData { BookThumbnail.from(fakeBookFile(it)) }
                    .collectAsLazyPagingItems()
            BookshelfInfoSheet(
                uiState = BookshelfInfoSheetUiState(
                    bookshelf = contentKey.bookshelf,
                    folder = contentKey.folder,
                    enabled = true,
                    isProgressScan = false
                ),
                lazyPagingItems = lazyPagingItems,
                onAction = {},
            )
        }
    ) {}
}

@Composable
@PreviewMultiScreen
private fun BookshelfInfoSheet2Preview() {
    val navigator = rememberSupportingPaneScaffoldNavigator(
        initialDestinationHistory = listOf(
            ThreePaneScaffoldDestinationItem(
                SupportingPaneScaffoldRole.Extra,
                BookshelfFolder(fakeSmbServer(), fakeFolder())
            )
        )
    )
    PreviewCanonicalScaffold(
        navigator = navigator,
        extraPane = {
            LoadingScreen()
        }
    ) {}
}
