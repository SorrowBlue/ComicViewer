package com.sorrowblue.comicviewer.feature.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.result.ResultRecipient
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.feature.history.destinations.ClearAllHistoryDialogDestination
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryGraph
import com.sorrowblue.comicviewer.feature.history.section.HistoryTopAppBar
import com.sorrowblue.comicviewer.feature.history.section.HistoryTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.copy
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

interface HistoryScreenNavigator {
    fun navigateUp()
    fun onSettingsClick()
    fun navigateToBook(book: Book)
    fun onFavoriteClick(bookshelfId: BookshelfId, path: String)
    fun navigateToFolder(file: File)
    fun onClearAllClick()
}

@Destination<HistoryGraph>(start = true, visibility = CodeGenVisibility.INTERNAL)
@Composable
internal fun HistoryScreen(
    navigator: HistoryScreenNavigator,
    clearAllResult: ResultRecipient<ClearAllHistoryDialogDestination, Boolean>,
) {
    HistoryScreen(
        navigator = navigator,
        state = rememberHistoryScreenState(clearAllResult = clearAllResult),
    )
}

@Composable
private fun HistoryScreen(
    navigator: HistoryScreenNavigator,
    state: HistoryScreenState,
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val lazyGridState = rememberLazyGridState()
    HistoryScreen(
        lazyPagingItems = lazyPagingItems,
        navigator = state.navigator,
        onHistoryTopAppBarAction = state::onHistoryTopAppBarAction,
        onFileInfoSheetAction = state::onFileInfoSheetAction,
        onHistoryContentsAction = state::onHistoryContentsAction,
        lazyGridState = lazyGridState,
    )

    val currentNavigator by rememberUpdatedState(navigator)
    LaunchedEventEffect(state.event) {
        when (it) {
            is HistoryScreenEvent.Favorite -> currentNavigator.onFavoriteClick(
                it.bookshelfId,
                it.path
            )

            is HistoryScreenEvent.Book -> currentNavigator.navigateToBook(it.book)
            is HistoryScreenEvent.OpenFolder -> currentNavigator.navigateToFolder(it.file)
            HistoryScreenEvent.Back -> currentNavigator.navigateUp()
            HistoryScreenEvent.Settings -> currentNavigator.onSettingsClick()
            HistoryScreenEvent.DeleteAll -> currentNavigator.onClearAllClick()
        }
    }
}

@Composable
private fun HistoryScreen(
    lazyPagingItems: LazyPagingItems<Book>,
    navigator: ThreePaneScaffoldNavigator<File.Key>,
    onHistoryTopAppBarAction: (HistoryTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetNavigator) -> Unit,
    onHistoryContentsAction: (HistoryContentsAction) -> Unit,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            HistoryTopAppBar(
                onAction = onHistoryTopAppBarAction,
                scrollBehavior = scrollBehavior,
                scrollableState = lazyGridState
            )
        },
        extraPane = { contentKey ->
            FileInfoSheet(
                fileKey = contentKey,
                onAction = onFileInfoSheetAction,
                isOpenFolderEnabled = true
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        FavoriteContents(
            lazyGridState = lazyGridState,
            lazyPagingItems = lazyPagingItems,
            onAction = onHistoryContentsAction,
            contentPadding = contentPadding
        )
    }
}

internal sealed interface HistoryContentsAction {

    data class Book(val book: com.sorrowblue.comicviewer.domain.model.file.Book) :
        HistoryContentsAction

    data class FileInfo(val file: File) :
        HistoryContentsAction
}

@Composable
private fun FavoriteContents(
    lazyPagingItems: LazyPagingItems<Book>,
    lazyGridState: LazyGridState,
    onAction: (HistoryContentsAction) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawResumeFolder,
            text = stringResource(id = R.string.history_label_no_history),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    } else {
        FileLazyVerticalGrid(
            uiState = FileLazyVerticalGridUiState(fileListDisplay = FileListDisplay.List),
            state = lazyGridState,
            lazyPagingItems = lazyPagingItems,
            onItemClick = { onAction(HistoryContentsAction.Book(it)) },
            onItemInfoClick = { onAction(HistoryContentsAction.FileInfo(it)) },
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize()
        )
    }
}
