package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.rememberCanonicalScaffoldNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
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
    val pagingDataFlow: Flow<PagingData<File>>
    val lazyGridState: LazyGridState
    val events: EventFlow<ReadLaterScreenEvent>
    val navigator: ThreePaneScaffoldNavigator<File.Key>
    fun onNavClick()
    fun onTopAppBarAction(action: ReadLaterTopAppBarAction)
    fun onFileInfoSheetAction(action: FileInfoSheetNavigator)
    fun onContentsAction(action: ReadLaterContentsAction)
}

@Composable
internal fun rememberReadLaterScreenState(
    navigator: ThreePaneScaffoldNavigator<File.Key> = rememberCanonicalScaffoldNavigator(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ReadLaterViewModel = koinViewModel(),
): ReadLaterScreenState = remember {
    ReadLaterScreenStateImpl(
        navigator = navigator,
        lazyGridState = lazyGridState,
        scope = scope,
        viewModel = viewModel,
        deleteAllReadLaterUseCase = viewModel.deleteAllReadLaterUseCase,
    )
}

private class ReadLaterScreenStateImpl(
    viewModel: ReadLaterViewModel,
    override val navigator: ThreePaneScaffoldNavigator<File.Key>,
    override val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
    private val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
) : ReadLaterScreenState {

    override val events = EventFlow<ReadLaterScreenEvent>()
    override val pagingDataFlow = viewModel.pagingDataFlow

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
                navigator.navigateBack()
            }

            is FileInfoSheetNavigator.Collection -> navigator.currentDestination?.contentKey?.let {
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
                navigator.navigateTo(SupportingPaneScaffoldRole.Extra, action.file.key())
            }
        }
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }
}
