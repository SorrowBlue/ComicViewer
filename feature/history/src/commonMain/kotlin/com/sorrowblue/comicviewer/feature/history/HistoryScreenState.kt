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
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import com.sorrowblue.comicviewer.framework.ui.rememberAdaptiveNavigationSuiteScaffoldState
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
internal fun rememberHistoryScreenState(
): HistoryScreenState {
    return remember {
        HistoryScreenStateImpl(clearAllHistoryUseCase = context.clearAllHistoryUseCase)
    }.apply {
        this.lazyGridState = rememberLazyGridState()
        this.scope = rememberCoroutineScope()
        this.lazyPagingItems = rememberPagingItems {
            context.pagingHistoryBookUseCase(
                PagingHistoryBookUseCase.Request(PagingConfig(20))
            )
        }
        this.scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    }
}

private class HistoryScreenStateImpl(
    private val clearAllHistoryUseCase: ClearAllHistoryUseCase,
) : HistoryScreenState {

    override lateinit var lazyGridState: LazyGridState
    override lateinit var lazyPagingItems: LazyPagingItems<Book>
    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState
    lateinit var scope: CoroutineScope

    override fun onNavResult(result: Boolean) {
        if (result) {
            clearAll()
        }
    }

    fun clearAll() {
        scope.launch {
            clearAllHistoryUseCase(ClearAllHistoryUseCase.Request)
        }
    }
}
