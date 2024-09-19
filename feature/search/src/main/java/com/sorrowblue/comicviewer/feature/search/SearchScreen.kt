package com.sorrowblue.comicviewer.feature.search

import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBar
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBarAction
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBarUiState
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraphTransitions
import com.sorrowblue.comicviewer.feature.search.section.SearchContents
import com.sorrowblue.comicviewer.feature.search.section.SearchContentsAction
import com.sorrowblue.comicviewer.feature.search.section.SearchContentsUiState
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.flowData
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

interface SearchScreenNavigator {
    fun navigateUp()
    fun onFileClick(file: File)
    fun onFavoriteClick(file: File)
    fun onOpenFolderClick(file: File)
    fun onNavigateSettings()
}

@Parcelize
class SearchArgs(val bookshelfId: BookshelfId, val path: String) : Parcelable

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
            is SearchScreenEvent.Favorite -> currentNavigator.onFavoriteClick(it.file)
            is SearchScreenEvent.OpenFolder -> currentNavigator.onOpenFolderClick(it.file)
            SearchScreenEvent.Back -> currentNavigator.navigateUp()
            is SearchScreenEvent.File -> currentNavigator.onFileClick(it.file)
            SearchScreenEvent.Settings -> currentNavigator.onNavigateSettings()
        }
    }

    LaunchedEffect(state.uiState.searchTopAppBarUiState.searchCondition) {
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

@Parcelize
internal data class SearchScreenUiState(
    val searchTopAppBarUiState: SearchTopAppBarUiState = SearchTopAppBarUiState(),
    val searchContentsUiState: SearchContentsUiState = SearchContentsUiState(),
) : Parcelable

@Composable
private fun SearchScreen(
    uiState: SearchScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    lazyGridState: LazyGridState,
    onSearchTopAppBarAction: (SearchTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetAction) -> Unit,
    onSearchContentsAction: (SearchContentsAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            SearchTopAppBar(
                uiState = uiState.searchTopAppBarUiState,
                onAction = onSearchTopAppBarAction,
                scrollBehavior = scrollBehavior
            )
        },
        extraPane = { innerPadding, fileInfoUiState ->
            FileInfoSheet(
                uiState = fileInfoUiState,
                onAction = onFileInfoSheetAction,
                contentPadding = innerPadding,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        SearchContents(
            uiState = uiState.searchContentsUiState,
            lazyPagingItems = lazyPagingItems,
            lazyListState = lazyGridState,
            onAction = onSearchContentsAction,
            contentPadding = innerPadding,
        )
    }
}

private const val WaitLoadPage = 350L

@Preview
@Composable
private fun SearchScreenPreview() {
    PreviewTheme {
        val pagingDataFlow = PagingData.flowData<File> { fakeBookFile(it) }
        val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
        SearchScreen(
            uiState = SearchScreenUiState(),
            lazyPagingItems = lazyPagingItems,
            navigator = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
            lazyGridState = rememberLazyGridState(),
            onSearchTopAppBarAction = {},
            onFileInfoSheetAction = {},
            onSearchContentsAction = {}
        )
    }
}
