package com.sorrowblue.comicviewer.feature.search

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.search.component.SearchAppBar
import com.sorrowblue.comicviewer.feature.search.section.SearchConditions
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionsUiState
import com.sorrowblue.comicviewer.feature.search.section.SearchResultSheet
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SearchScreenUiState(
    val query: String = "",
    val searchConditionsUiState: SearchConditionsUiState = SearchConditionsUiState(),
) : Parcelable

private const val WaitLoadPage = 500L

interface SearchScreenNavigator {
    fun navigateUp()
    fun onFileClick(file: File)
    fun onFavoriteClick(file: File)
    fun onOpenFolderClick(file: File)
}

class SearchArgs(val bookshelfId: BookshelfId, val path: String)

@Destination(navArgsDelegate = SearchArgs::class)
@Composable
internal fun SearchScreen(args: SearchArgs, navigator: SearchScreenNavigator) {
    SearchScreen(
        args = args,
        onBackClick = navigator::navigateUp,
        onFileClick = navigator::onFileClick,
        onFavoriteClick = navigator::onFavoriteClick,
        onOpenFolderClick = navigator::onOpenFolderClick
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun SearchScreen(
    args: SearchArgs,
    onBackClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    state: SearchScreenState = rememberSearchScreenState(args = args),
) {
    val uiState = state.uiState
    val lazyPagingItems = state.lazyPagingItems.collectAsLazyPagingItems()
    val lazyGridState = rememberLazyGridState()
    SearchScreen(
        uiState = uiState,
        lazyGridState = lazyGridState,
        navigator = state.navigator,
        lazyPagingItems = lazyPagingItems,
        onQueryChange = state::onQueryChange,
        onBackClick = onBackClick,
        onChangeSearchCondition = state::onChangeSearchCondition,
        onFileClick = onFileClick,
        onFileLongClick = state::onFileInfoClick,
        onFileInfoCloseClick = state::onFileInfoCloseClick,
        onReadLaterClick = state::onReadLaterClick,
        onFavoriteClick = onFavoriteClick,
        onOpenFolderClick = onOpenFolderClick
    )
    LaunchedEffect(uiState) {
        if (!state.isSkipFirstRefresh) {
            delay(WaitLoadPage)
            lazyPagingItems.refresh()
        }
    }
    LaunchedEffect(lazyPagingItems.loadState) {
        if (lazyPagingItems.isLoadedData && state.isScrollableTop) {
            state.isScrollableTop = false
            lazyGridState.scrollToItem(0)
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    uiState: SearchScreenUiState,
    navigator: ThreePaneScaffoldNavigator<File>,
    lazyGridState: LazyGridState,
    lazyPagingItems: LazyPagingItems<File>,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onChangeSearchCondition: (SearchConditionsUiState.SearchCondition) -> Unit,
    onFileClick: (File) -> Unit,
    onFileLongClick: (File) -> Unit,
    onFileInfoCloseClick: () -> Unit,
    onReadLaterClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            Column {
                SearchAppBar(
                    query = uiState.query,
                    onBackClick = onBackClick,
                    onQueryChange = onQueryChange,
                    scrollBehavior = scrollBehavior
                )
                SearchConditions(
                    uiState = uiState.searchConditionsUiState,
                    onChangeSearchCondition = onChangeSearchCondition,
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                        .padding(horizontal = ComicTheme.dimension.margin)
                        .padding(top = ComicTheme.dimension.padding * 2)
                )
            }
        },
        extraPane = { innerPadding ->
            var file by remember { mutableStateOf(navigator.currentDestination?.content) }
            LaunchedEffect(key1 = navigator.currentDestination) {
                navigator.currentDestination?.content?.let { file = it }
            }
            file?.let {
                FileInfoSheet(
                    file = it,
                    onCloseClick = onFileInfoCloseClick,
                    onReadLaterClick = { onReadLaterClick(it) },
                    onFavoriteClick = { onFavoriteClick(it) },
                    onOpenFolderClick = { onOpenFolderClick(it) },
                    contentPadding = innerPadding,
                    scaffoldDirective = navigator.scaffoldState.scaffoldDirective
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        SearchResultSheet(
            query = uiState.query,
            lazyPagingItems = lazyPagingItems,
            contentPadding = innerPadding,
            lazyListState = lazyGridState,
            onFileClick = onFileClick,
            onFileLongClick = onFileLongClick,
        )
    }
}
