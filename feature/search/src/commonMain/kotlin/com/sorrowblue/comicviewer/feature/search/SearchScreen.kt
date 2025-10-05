package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBar
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBarAction
import com.sorrowblue.comicviewer.feature.search.section.SearchContents
import com.sorrowblue.comicviewer.feature.search.section.SearchContentsAction
import com.sorrowblue.comicviewer.feature.search.section.SearchContentsUiState
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.KSerializableSaver
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

interface SearchScreenNavigator {
    fun navigateUp()
    fun onFileClick(file: File)
    fun onCollectionAddClick(bookshelfId: BookshelfId, path: String)
    fun onOpenFolderClick(bookshelfId: BookshelfId, parent: String)
    fun onSettingsClick()
    fun onSmartCollectionClick(bookshelfId: BookshelfId, searchCondition: SearchCondition)
}

@Serializable
data class Search(val bookshelfId: BookshelfId, val path: String)

@Destination<Search>
@Composable
internal fun SearchScreen(
    route: Search,
    navigator: SearchScreenNavigator = koinInject(),
    state: SearchScreenState = rememberSearchScreenState(route),
) {
    val lazyPagingItems = state.lazyPagingItems.collectAsLazyPagingItems()
    SearchScreen(
        uiState = state.uiState,
        lazyPagingItems = lazyPagingItems,
        navigator = state.navigator,
        lazyGridState = state.lazyGridState,
        onSearchTopAppBarAction = state::onSearchTopAppBarAction,
        onFileInfoSheetAction = state::onFileInfoSheetAction,
        onSearchContentsAction = state::onSearchContentsAction
    )

    val currentNavigator by rememberUpdatedState(navigator)
    EventEffect(state.events) {
        when (it) {
            is SearchScreenEvent.Collection ->
                currentNavigator.onCollectionAddClick(it.bookshelfId, it.path)

            is SearchScreenEvent.OpenFolder ->
                currentNavigator.onOpenFolderClick(it.bookshelfId, it.parent)

            SearchScreenEvent.Back -> currentNavigator.navigateUp()
            is SearchScreenEvent.File -> currentNavigator.onFileClick(it.file)
            SearchScreenEvent.Settings -> currentNavigator.onSettingsClick()
            is SearchScreenEvent.SmartCollection -> currentNavigator.onSmartCollectionClick(
                it.bookshelfId,
                it.searchCondition
            )
        }
    }

    LaunchedEffect(state.uiState.searchCondition) {
        if (!state.isSkipFirstRefresh) {
            delay(WaitLoadPage)
            lazyPagingItems.refresh()
        }
    }
    LaunchedEffect(lazyPagingItems.loadState) {
        if (lazyPagingItems.isLoadedData && state.isScrollableTop) {
            state.isScrollableTop = false
            state.lazyGridState.scrollToItem(0)
        }
    }
}

@Serializable
internal data class SearchScreenUiState(
    val searchCondition: SearchCondition = SearchCondition(),
    val searchContentsUiState: SearchContentsUiState = SearchContentsUiState(),
) {
    object Saver : KSerializableSaver<SearchScreenUiState>(serializer())
}

@Composable
internal fun SearchScreen(
    uiState: SearchScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    navigator: ThreePaneScaffoldNavigator<File.Key>,
    lazyGridState: LazyGridState,
    onSearchTopAppBarAction: (SearchTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetNavigator) -> Unit,
    onSearchContentsAction: (SearchContentsAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            SearchTopAppBar(
                searchCondition = uiState.searchCondition,
                onAction = onSearchTopAppBarAction,
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
        SearchContents(
            uiState = uiState.searchContentsUiState,
            lazyPagingItems = lazyPagingItems,
            lazyListState = lazyGridState,
            onAction = onSearchContentsAction,
            contentPadding = contentPadding
        )
    }
}

private const val WaitLoadPage = 350L
