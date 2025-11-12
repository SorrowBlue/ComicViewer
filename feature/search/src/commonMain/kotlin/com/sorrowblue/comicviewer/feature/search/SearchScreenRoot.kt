package com.sorrowblue.comicviewer.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import kotlinx.coroutines.delay

@Composable
context(context: SearchScreenContext)
fun SearchScreenRoot(
    bookshelfId: BookshelfId,
    path: PathString,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSmartCollectionClick: (BookshelfId, SearchCondition) -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
) {
    val state = rememberSearchScreenState(bookshelfId, path)
    val lazyPagingItems = state.lazyPagingItems
    SearchScreen(
        uiState = state.uiState,
        lazyPagingItems = lazyPagingItems,
        lazyGridState = state.lazyGridState,
        onBackClick = onBackClick,
        onSmartCollectionClick = {
            onSmartCollectionClick(bookshelfId, state.uiState.searchCondition)
        },
        onSettingsClick = onSettingsClick,
        onQueryChange = state::onQueryChange,
        onRangeClick = state::onRangeClick,
        onPeriodClick = state::onPeriodClick,
        onSortTypeClick = state::onSortTypeClick,
        onShowHiddenClick = state::onShowHiddenClick,
        onItemClick = onFileClick,
        onItemInfoClick = onFileInfoClick,
    )

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

private const val WaitLoadPage = 350L
