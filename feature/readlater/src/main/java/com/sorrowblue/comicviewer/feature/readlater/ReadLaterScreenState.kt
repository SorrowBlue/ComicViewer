package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal sealed interface ReadLaterScreenEvent {

    data class Favorite(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        ReadLaterScreenEvent

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        ReadLaterScreenEvent

    data object Settings : ReadLaterScreenEvent
}

internal interface ReadLaterScreenState :
    SaveableScreenState,
    ScreenStateEvent<ReadLaterScreenEvent> {
    val pagingDataFlow: Flow<PagingData<File>>
    val lazyGridState: LazyGridState
    val navigator: ThreePaneScaffoldNavigator<File>
    fun onNavClick()
    fun onTopAppBarAction(action: ReadLaterTopAppBarAction)
    fun onFileInfoSheetAction(action: FileInfoSheetNavigator)
    fun onContentsAction(action: ReadLaterContentsAction)
}

@Composable
internal fun rememberReadLaterScreenState(
    navigator: ThreePaneScaffoldNavigator<File> = rememberSupportingPaneScaffoldNavigator<File>(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ReadLaterViewModel = hiltViewModel(),
): ReadLaterScreenState = rememberSaveableScreenState {
    ReadLaterScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        lazyGridState = lazyGridState,
        scope = scope,
        viewModel = viewModel,
        deleteAllReadLaterUseCase = viewModel.deleteAllReadLaterUseCase,
    )
}

private class ReadLaterScreenStateImpl(
    viewModel: ReadLaterViewModel,
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<File>,
    override val lazyGridState: LazyGridState,
    override val scope: CoroutineScope,
    private val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
) : ReadLaterScreenState {

    override val event = MutableSharedFlow<ReadLaterScreenEvent>()
    override val pagingDataFlow = viewModel.pagingDataFlow

    override fun onTopAppBarAction(action: ReadLaterTopAppBarAction) {
        when (action) {
            ReadLaterTopAppBarAction.ClearAll -> scope.launch {
                deleteAllReadLaterUseCase(DeleteAllReadLaterUseCase.Request)
            }

            ReadLaterTopAppBarAction.Settings -> sendEvent(ReadLaterScreenEvent.Settings)
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetNavigator) {
        when (action) {
            FileInfoSheetNavigator.Back -> navigator.navigateBack()
            is FileInfoSheetNavigator.Favorite -> navigator.currentDestination?.contentKey?.let {
                sendEvent(ReadLaterScreenEvent.Favorite(it))
            }

            is FileInfoSheetNavigator.OpenFolder -> TODO("Not yet implemented")
        }
    }

    override fun onContentsAction(action: ReadLaterContentsAction) {
        when (action) {
            is ReadLaterContentsAction.File -> sendEvent(ReadLaterScreenEvent.File(action.file))
            is ReadLaterContentsAction.FileInfo ->
                navigator.navigateTo(SupportingPaneScaffoldRole.Extra, action.file)
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
