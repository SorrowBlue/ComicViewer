package com.sorrowblue.comicviewer.feature.search

import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
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
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.preview.flowData
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.material3.adaptive.navigation.BackHandlerForNavigator
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize

interface SearchScreenNavigator {
    fun navigateUp()
    fun onFileClick(file: File)
    fun onFavoriteClick(file: File)
    fun onOpenFolderClick(file: File)
    fun onNavigateSettings()
}

class SearchArgs(val bookshelfId: BookshelfId, val path: String)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Destination<SearchGraph>(
    start = true,
    navArgs = SearchArgs::class,
    style = SearchGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun SearchScreen(args: SearchArgs, navigator: SearchScreenNavigator) {
    SearchScreen(navigator = navigator, state = rememberSearchScreenState(args = args))
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        state.event.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.RESUMED).onEach {
            when (it) {
                is SearchScreenEvent.Favorite -> navigator.onFavoriteClick(it.file)
                is SearchScreenEvent.OpenFolder -> navigator.onOpenFolderClick(it.file)
                SearchScreenEvent.Back -> navigator.navigateUp()
                is SearchScreenEvent.File -> navigator.onFileClick(it.file)
                SearchScreenEvent.Settings -> navigator.onNavigateSettings()
            }
        }.launchIn(this)
    }
    BackHandlerForNavigator(navigator = state.navigator)

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

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
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
        extraPaneVisible = { innerPadding, fileInfoUiState ->
            FileInfoSheet(
                uiState = fileInfoUiState,
                onAction = onFileInfoSheetAction,
                contentPadding = innerPadding,
                scaffoldDirective = navigator.scaffoldDirective
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

private const val WaitLoadPage = 500L

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Preview
@Composable
private fun SearchScreenPreview() {
    PreviewTheme {
        val pagingDataFlow = PagingData.flowData<File> { fakeBookFile(BookshelfId(it)) }
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
