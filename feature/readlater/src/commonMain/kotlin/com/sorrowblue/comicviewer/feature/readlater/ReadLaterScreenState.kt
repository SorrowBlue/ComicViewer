package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.PagingReadLaterFileUseCase
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface ReadLaterScreenState {
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyPagingItems: LazyPagingItems<File>
    val lazyGridState: LazyGridState

    fun onClearAllClick()
}

@Composable
context(context: ReadLaterScreenContext)
internal fun rememberReadLaterScreenState(): ReadLaterScreenState {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val lazyPagingItems = rememberPagingItems {
        context.pagingReadLaterFileUseCase(
            PagingReadLaterFileUseCase.Request(PagingConfig(20)),
        )
    }
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState(
        onNavigationReSelect = {
            if (lazyGridState.canScrollBackward) {
                scope.launch {
                    lazyGridState.animateScrollToItem(0)
                }
            }
        },
    )

    return remember(lazyGridState, lazyPagingItems, scaffoldState) {
        ReadLaterScreenStateImpl(
            deleteAllReadLaterUseCase = context.deleteAllReadLaterUseCase,
            lazyGridState = lazyGridState,
            lazyPagingItems = lazyPagingItems,
            scaffoldState = scaffoldState,
            scope = scope,
        )
    }
}

private class ReadLaterScreenStateImpl(
    private val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
    override val lazyGridState: LazyGridState,
    override val lazyPagingItems: LazyPagingItems<File>,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    private val scope: CoroutineScope,
) : ReadLaterScreenState {
    override fun onClearAllClick() {
        scope.launch {
            deleteAllReadLaterUseCase(DeleteAllReadLaterUseCase.Request)
        }
    }
}
