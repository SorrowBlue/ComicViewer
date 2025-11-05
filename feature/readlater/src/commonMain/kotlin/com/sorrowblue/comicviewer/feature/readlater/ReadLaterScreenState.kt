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
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import com.sorrowblue.comicviewer.framework.ui.rememberAdaptiveNavigationSuiteScaffoldState
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
internal fun rememberReadLaterScreenState(
): ReadLaterScreenState {
    val state = remember {
        ReadLaterScreenStateImpl(
            deleteAllReadLaterUseCase = context.deleteAllReadLaterUseCase,
        )
    }.apply {
        scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
        lazyPagingItems = rememberPagingItems {
            context.pagingReadLaterFileUseCase(
                PagingReadLaterFileUseCase.Request(PagingConfig(20))
            )
        }
        lazyGridState = rememberLazyGridState()
        coroutineScope = rememberCoroutineScope()
    }
    return state
}

private class ReadLaterScreenStateImpl(
    private val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
) : ReadLaterScreenState {
    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState
    override lateinit var lazyPagingItems: LazyPagingItems<File>
    override lateinit var lazyGridState: LazyGridState
    lateinit var coroutineScope: CoroutineScope

    override fun onClearAllClick() {
        coroutineScope.launch {
            deleteAllReadLaterUseCase(DeleteAllReadLaterUseCase.Request)
        }
    }
}
