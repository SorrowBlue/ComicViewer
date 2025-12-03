package com.sorrowblue.comicviewer.feature.history

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.history.section.HistoryBookList
import com.sorrowblue.comicviewer.feature.history.section.HistoryTopAppBar
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState

@Composable
internal fun AdaptiveNavigationSuiteScaffoldState.HistoryScreen(
    lazyPagingItems: LazyPagingItems<Book>,
    lazyGridState: LazyGridState,
    onDeleteAllClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onBookInfoClick: (Book) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    AdaptiveNavigationSuiteScaffold {
        Scaffold(
            topBar = {
                HistoryTopAppBar(
                    onDeleteAllClick = onDeleteAllClick,
                    onSettingsClick = onSettingsClick,
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { contentPadding ->
            HistoryBookList(
                lazyPagingItems = lazyPagingItems,
                onItemClick = onBookClick,
                onItemInfoClick = onBookInfoClick,
                lazyGridState = lazyGridState,
                contentPadding = contentPadding,
            )
        }
    }
}
