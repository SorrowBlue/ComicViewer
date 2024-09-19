package com.sorrowblue.comicviewer.feature.history

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.feature.history.section.HistoryTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoSheetState
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal sealed interface HistoryScreenEvent {
    data class Favorite(val file: File) :
        HistoryScreenEvent

    data class Book(val book: com.sorrowblue.comicviewer.domain.model.file.Book) :
        HistoryScreenEvent

    data object Back : HistoryScreenEvent
    data object Settings : HistoryScreenEvent
}

internal interface HistoryScreenState :
    SaveableScreenState,
    FileInfoSheetState,
    ScreenStateEvent<HistoryScreenEvent> {
    val pagingDataFlow: Flow<PagingData<Book>>
    fun onHistoryTopAppBarAction(action: HistoryTopAppBarAction)
    fun onFileInfoSheetAction(action: FileInfoSheetAction)
    fun onHistoryContentsAction(action: HistoryContentsAction)
}

@Composable
internal fun rememberHistoryScreenState(
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState> = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: HistoryViewModel = hiltViewModel(),
): HistoryScreenState = rememberSaveableScreenState {
    HistoryScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        snackbarHostState = snackbarHostState,
        scope = scope,
        addReadLaterUseCase = viewModel.addReadLaterUseCase,
        existsReadlaterUseCase = viewModel.existsReadlaterUseCase,
        deleteReadLaterUseCase = viewModel.deleteReadLaterUseCase,
        getFileAttributeUseCase = viewModel.getFileAttributeUseCase,
        viewModel = viewModel
    )
}

private class HistoryScreenStateImpl(
    viewModel: HistoryViewModel,
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    override val snackbarHostState: SnackbarHostState,
    override val scope: CoroutineScope,
    override val getFileAttributeUseCase: GetFileAttributeUseCase,
    override val existsReadlaterUseCase: ExistsReadlaterUseCase,
    override val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    override val addReadLaterUseCase: AddReadLaterUseCase,
) : HistoryScreenState {

    override val pagingDataFlow = viewModel.pagingDataFlow
    override val event = MutableSharedFlow<HistoryScreenEvent>()
    override var fileInfoJob: Job? = null

    override fun onHistoryTopAppBarAction(action: HistoryTopAppBarAction) {
        when (action) {
            HistoryTopAppBarAction.Back -> sendEvent(HistoryScreenEvent.Back)
            HistoryTopAppBarAction.Settings -> sendEvent(HistoryScreenEvent.Settings)
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetAction) {
        when (action) {
            FileInfoSheetAction.Close -> navigator.navigateBack()
            FileInfoSheetAction.Favorite -> navigator.currentDestination?.contentKey?.file?.let {
                sendEvent(HistoryScreenEvent.Favorite(it))
            }

            FileInfoSheetAction.OpenFolder ->
                TODO("Not yet implemented")

            FileInfoSheetAction.ReadLater -> onReadLaterClick()
        }
    }

    override fun onHistoryContentsAction(action: HistoryContentsAction) {
        when (action) {
            is HistoryContentsAction.Book -> sendEvent(HistoryScreenEvent.Book(action.book))
            is HistoryContentsAction.FileInfo -> navigateToFileInfo(action.file)
        }
    }

    private fun navigateToFileInfo(file: File) {
        fetchFileInfo(file) {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, it)
        }
    }
}
