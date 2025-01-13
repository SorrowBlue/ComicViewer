package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBar
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBarAction
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraphTransitions
import com.sorrowblue.comicviewer.feature.search.section.SearchContents
import com.sorrowblue.comicviewer.feature.search.section.SearchContentsAction
import com.sorrowblue.comicviewer.feature.search.section.SearchContentsUiState
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberCanonicalScaffoldNavigator
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCompliantNavigation
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface SearchScreenNavigator {
    fun navigateUp()
    fun onFileClick(file: File)
    fun onFavoriteClick(bookshelfId: BookshelfId, path: String)
    fun onOpenFolderClick(bookshelfId: BookshelfId, parent: String)
    fun onNavigateSettings()
}

data class SearchArgs(val bookshelfId: BookshelfId, val path: String)

@Destination<SearchGraph>(
    start = true,
    navArgs = SearchArgs::class,
    style = SearchGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun SearchScreen(navigator: SearchScreenNavigator) {
    SearchScreen(navigator = navigator, state = rememberSearchScreenState())
}

@Composable
internal fun SearchScreen(navigator: SearchScreenNavigator, state: SearchScreenState) {
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
    LaunchedEventEffect(state.event) {
        when (it) {
            is SearchScreenEvent.Favorite ->
                currentNavigator.onFavoriteClick(it.bookshelfId, it.path)

            is SearchScreenEvent.OpenFolder ->
                currentNavigator.onOpenFolderClick(it.bookshelfId, it.parent)

            SearchScreenEvent.Back -> currentNavigator.navigateUp()
            is SearchScreenEvent.File -> currentNavigator.onFileClick(it.file)
            SearchScreenEvent.Settings -> currentNavigator.onNavigateSettings()
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

internal data class SearchScreenUiState(
    val searchCondition: SearchCondition = SearchCondition(),
    val searchContentsUiState: SearchContentsUiState = SearchContentsUiState(),
) {
    object Saver :
        androidx.compose.runtime.saveable.Saver<SearchScreenUiState, String> {
        override fun restore(value: String): SearchScreenUiState {
            val searchCondition = Json.decodeFromString<SearchCondition>(value)
            return SearchScreenUiState(
                searchCondition = searchCondition,
                searchContentsUiState = SearchContentsUiState(searchCondition.query)
            )
        }

        override fun SaverScope.save(value: SearchScreenUiState) =
            Json.encodeToString<SearchCondition>(value.searchCondition)
    }
}

@Composable
private fun SearchScreen(
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

@PreviewMultiScreen
@Composable
private fun SearchScreenPreview() {
    PreviewCompliantNavigation {
        val pagingDataFlow = PagingData.flowData<File> { fakeBookFile(it) }
        val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
        SearchScreen(
            uiState = SearchScreenUiState(),
            lazyPagingItems = lazyPagingItems,
            navigator = rememberCanonicalScaffoldNavigator(),
            lazyGridState = rememberLazyGridState(),
            onSearchTopAppBarAction = {},
            onFileInfoSheetAction = {},
            onSearchContentsAction = {}
        )
    }
}
