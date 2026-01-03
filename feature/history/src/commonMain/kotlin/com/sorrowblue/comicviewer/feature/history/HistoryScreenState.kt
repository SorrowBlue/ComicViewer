package com.sorrowblue.comicviewer.feature.history

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.usecase.file.ClearAllHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingHistoryBookUseCase
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface HistoryScreenState {
    val lazyPagingItems: LazyPagingItems<Book>
    val lazyGridState: LazyGridState
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState

    fun onNavResult(result: Boolean)
}

@Composable
context(context: HistoryScreenContext)
internal fun rememberHistoryScreenState(): HistoryScreenState {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val lazyPagingItems = rememberPagingItems {
        context.pagingHistoryBookUseCase(
            PagingHistoryBookUseCase.Request(PagingConfig(20)),
        )
    }
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()

    return remember(lazyGridState, lazyPagingItems, scaffoldState) {
        HistoryScreenStateImpl(
            clearAllHistoryUseCase = context.clearAllHistoryUseCase,
            lazyGridState = lazyGridState,
            lazyPagingItems = lazyPagingItems,
            scaffoldState = scaffoldState,
            scope = scope,
        )
    }
}

private class HistoryScreenStateImpl(
    private val clearAllHistoryUseCase: ClearAllHistoryUseCase,
    override val lazyGridState: LazyGridState,
    override val lazyPagingItems: LazyPagingItems<Book>,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    private val scope: CoroutineScope,
) : HistoryScreenState {
    override fun onNavResult(result: Boolean) {
        if (result) {
            clearAll()
        }
    }

    private fun clearAll() {
        scope.launch {
            clearAllHistoryUseCase(ClearAllHistoryUseCase.Request)
        }
    }
}
