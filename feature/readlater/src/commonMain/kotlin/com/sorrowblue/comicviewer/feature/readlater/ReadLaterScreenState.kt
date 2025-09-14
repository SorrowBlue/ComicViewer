package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.rememberCanonicalScaffoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

internal sealed interface ReadLaterScreenEvent {

    data class Collection(val bookshelfId: BookshelfId, val path: String) : ReadLaterScreenEvent

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        ReadLaterScreenEvent

    data class OpenFolder(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        ReadLaterScreenEvent

    data object Settings : ReadLaterScreenEvent
}

internal interface ReadLaterScreenState {
    val lazyPagingItems: LazyPagingItems<File>
    val lazyGridState: LazyGridState
    val events: EventFlow<ReadLaterScreenEvent>

    val scaffoldState: CanonicalScaffoldState<File.Key>

    fun onTopAppBarAction(action: ReadLaterTopAppBarAction)
    fun onFileInfoSheetAction(action: FileInfoSheetNavigator)
    fun onContentsAction(action: ReadLaterContentsAction)
}

@Composable
internal fun rememberReadLaterScreenState(
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ReadLaterViewModel = koinViewModel(),
): ReadLaterScreenState {
    val state = remember {
        ReadLaterScreenStateImpl(
            lazyGridState = lazyGridState,
            scope = scope,
            deleteAllReadLaterUseCase = viewModel.deleteAllReadLaterUseCase,
        )
    }

    state.lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    state.scaffoldState = rememberCanonicalScaffoldState<File.Key>(
        onReSelect = state::onReSelected
    )
    return state
}

private class ReadLaterScreenStateImpl(
    override val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
    private val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
) : ReadLaterScreenState {

    override lateinit var scaffoldState: CanonicalScaffoldState<File.Key>
    override val events = EventFlow<ReadLaterScreenEvent>()

    override lateinit var lazyPagingItems: LazyPagingItems<File>

    override fun onTopAppBarAction(action: ReadLaterTopAppBarAction) {
        when (action) {
            ReadLaterTopAppBarAction.ClearAll -> scope.launch {
                deleteAllReadLaterUseCase(DeleteAllReadLaterUseCase.Request)
            }

            ReadLaterTopAppBarAction.Settings -> events.tryEmit(ReadLaterScreenEvent.Settings)
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetNavigator) {
        when (action) {
            FileInfoSheetNavigator.Back -> scope.launch {
                scaffoldState.navigator.navigateBack()
            }

            is FileInfoSheetNavigator.Collection -> scaffoldState.navigator.currentDestination?.contentKey?.let {
                events.tryEmit(ReadLaterScreenEvent.Collection(it.bookshelfId, it.path))
            }

            is FileInfoSheetNavigator.OpenFolder -> {
                events.tryEmit(ReadLaterScreenEvent.OpenFolder(action.file))
            }
        }
    }

    override fun onContentsAction(action: ReadLaterContentsAction) {
        when (action) {
            is ReadLaterContentsAction.File -> events.tryEmit(ReadLaterScreenEvent.File(action.file))
            is ReadLaterContentsAction.FileInfo -> scope.launch {
                scaffoldState.navigator.navigateTo(
                    SupportingPaneScaffoldRole.Extra,
                    action.file.key()
                )
            }
        }
    }

    fun onReSelected() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    }
}
