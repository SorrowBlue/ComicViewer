package com.sorrowblue.comicviewer.feature.history

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.section.FavoriteContents
import com.sorrowblue.comicviewer.feature.history.section.HistoryContentsAction
import com.sorrowblue.comicviewer.feature.history.section.HistoryTopAppBar
import com.sorrowblue.comicviewer.feature.history.section.HistoryTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldLayout
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.NavigationSuiteScaffold2State
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

interface HistoryScreenNavigator {
    fun navigateUp()
    fun onSettingsClick()
    fun navigateToBook(book: Book)
    fun onCollectionAddClick(bookshelfId: BookshelfId, path: String)
    fun navigateToFolder(file: File)
    fun onClearAllClick()
}

@Serializable
data object History

@Destination<History>
@Composable
internal fun HistoryScreen(
    clearAllResult: NavResultReceiver<ClearAllHistory, Boolean>,
    navigator: HistoryScreenNavigator = koinInject(),
    state: HistoryScreenState = rememberHistoryScreenState(),
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val lazyGridState = rememberLazyGridState()
    HistoryScreen(
        lazyPagingItems = lazyPagingItems,
        scaffoldState = state.scaffoldState,
        onHistoryTopAppBarAction = state::onHistoryTopAppBarAction,
        onFileInfoSheetAction = state::onFileInfoSheetAction,
        onHistoryContentsAction = state::onHistoryContentsAction,
        lazyGridState = lazyGridState,
    )

    clearAllResult.onNavResult { state.onNavResult(it) }

    val currentNavigator by rememberUpdatedState(navigator)
    EventEffect(state.events) {
        when (it) {
            is HistoryScreenEvent.Collection ->
                currentNavigator.onCollectionAddClick(it.bookshelfId, it.path)

            is HistoryScreenEvent.Book -> currentNavigator.navigateToBook(it.book)
            is HistoryScreenEvent.OpenFolder -> currentNavigator.navigateToFolder(it.file)
            HistoryScreenEvent.Back -> currentNavigator.navigateUp()
            HistoryScreenEvent.Settings -> currentNavigator.onSettingsClick()
            HistoryScreenEvent.DeleteAll -> currentNavigator.onClearAllClick()
        }
    }
}

@Composable
internal fun HistoryScreen(
    lazyPagingItems: LazyPagingItems<Book>,
    scaffoldState: NavigationSuiteScaffold2State<File.Key>,
    onHistoryTopAppBarAction: (HistoryTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetNavigator) -> Unit,
    onHistoryContentsAction: (HistoryContentsAction) -> Unit,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    scaffoldState.appBarState.scrollBehavior = scrollBehavior
    scaffoldState.CanonicalScaffoldLayout(
        topBar = {
            HistoryTopAppBar(onAction = onHistoryTopAppBarAction)
        },
        extraPane = { contentKey ->
            FileInfoSheet(
                fileKey = contentKey,
                onAction = onFileInfoSheetAction,
                isOpenFolderEnabled = true
            )
        },
    ) { contentPadding ->
        FavoriteContents(
            lazyGridState = lazyGridState,
            lazyPagingItems = lazyPagingItems,
            onAction = onHistoryContentsAction,
            contentPadding = contentPadding
        )
    }
}
