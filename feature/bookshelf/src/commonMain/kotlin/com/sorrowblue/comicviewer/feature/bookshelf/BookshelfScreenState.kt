package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch

interface BookshelfScreenState {
    val lazyPagingItems: LazyPagingItems<BookshelfFolder>
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyGridState: LazyGridState
}

@Composable
internal fun rememberBookshelfScreenState(
    viewModel: BookshelfViewModel = metroViewModel(),
): BookshelfScreenState {
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState(onNavigationReSelect = {
        if (lazyGridState.canScrollBackward) {
            coroutineScope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    })
    val lazyPagingItems = viewModel.bookshelfPagingFlow.collectAsLazyPagingItems()
    return remember(viewModel) {
        BookshelfScreenStateImpl(
            lazyGridState = lazyGridState,
            scaffoldState = scaffoldState,
            lazyPagingItems = lazyPagingItems,
        )
    }
}

private class BookshelfScreenStateImpl(
    override val lazyGridState: LazyGridState,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    override val lazyPagingItems: LazyPagingItems<BookshelfFolder>,
) : BookshelfScreenState

private const val PageSize = 20

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class BookshelfViewModel(pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase) :
    ViewModel() {
    val bookshelfPagingFlow =
        pagingBookshelfFolderUseCase(
            PagingBookshelfFolderUseCase.Request(PagingConfig(PageSize)),
        ).cachedIn(viewModelScope)
}
