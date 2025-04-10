package com.sorrowblue.comicviewer.feature.history

import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.sorrowblue.cmpdestinations.result.NavResult
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.ClearAllHistoryUseCase
import com.sorrowblue.comicviewer.feature.history.section.HistoryContentsAction
import com.sorrowblue.comicviewer.feature.history.section.HistoryTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.rememberCanonicalScaffoldNavigator
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

internal sealed interface HistoryScreenEvent {
    data class Collection(val bookshelfId: BookshelfId, val path: String) : HistoryScreenEvent
    data class OpenFolder(val file: File) : HistoryScreenEvent
    data class Book(val book: com.sorrowblue.comicviewer.domain.model.file.Book) :
        HistoryScreenEvent

    data object Back : HistoryScreenEvent
    data object Settings : HistoryScreenEvent
    data object DeleteAll : HistoryScreenEvent
}

internal interface HistoryScreenState :
    SaveableScreenState {
    val pagingDataFlow: Flow<PagingData<Book>>
    val events: EventFlow<HistoryScreenEvent>
    val navigator: ThreePaneScaffoldNavigator<File.Key>
    fun onHistoryTopAppBarAction(action: HistoryTopAppBarAction)
    fun onFileInfoSheetAction(action: FileInfoSheetNavigator)
    fun onHistoryContentsAction(action: HistoryContentsAction)
    fun onNavResult(result: NavResult<Boolean>)
}

@Composable
internal fun rememberHistoryScreenState(
    navigator: ThreePaneScaffoldNavigator<File.Key> = rememberCanonicalScaffoldNavigator(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: HistoryViewModel = koinViewModel(),
    clearAllHistoryUseCase: ClearAllHistoryUseCase = koinInject(),
): HistoryScreenState {
    return rememberSaveableScreenState {
        HistoryScreenStateImpl(
            pagingDataFlow = viewModel.pagingDataFlow,
            savedStateHandle = it,
            navigator = navigator,
            scope = scope,
            clearAllHistoryUseCase = clearAllHistoryUseCase
        )
    }
}

private class HistoryScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    private val scope: CoroutineScope,
    override val pagingDataFlow: Flow<PagingData<Book>>,
    override val navigator: ThreePaneScaffoldNavigator<File.Key>,
    private val clearAllHistoryUseCase: ClearAllHistoryUseCase,
) : HistoryScreenState {

    override val events = EventFlow<HistoryScreenEvent>()

    override fun onHistoryTopAppBarAction(action: HistoryTopAppBarAction) {
        when (action) {
            HistoryTopAppBarAction.Back -> events.tryEmit(HistoryScreenEvent.Back)
            HistoryTopAppBarAction.Settings -> events.tryEmit(HistoryScreenEvent.Settings)
            HistoryTopAppBarAction.DeleteAll -> events.tryEmit(HistoryScreenEvent.DeleteAll)
        }
    }

    override fun onNavResult(result: NavResult<Boolean>) {
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                if (result.value) {
                    clearAll()
                }
            }
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetNavigator) {
        when (action) {
            FileInfoSheetNavigator.Back -> scope.launch { navigator.navigateBack() }
            is FileInfoSheetNavigator.Collection -> navigator.currentDestination?.contentKey?.let {
                events.tryEmit(HistoryScreenEvent.Collection(it.bookshelfId, it.path))
            }

            is FileInfoSheetNavigator.OpenFolder -> events.tryEmit(
                HistoryScreenEvent.OpenFolder(
                    action.file
                )
            )
        }
    }

    override fun onHistoryContentsAction(action: HistoryContentsAction) {
        when (action) {
            is HistoryContentsAction.Book -> events.tryEmit(HistoryScreenEvent.Book(action.book))
            is HistoryContentsAction.FileInfo -> navigateToFileInfo(action.file)
        }
    }

    private fun navigateToFileInfo(file: File) {
        scope.launch {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, file.key())
        }
    }

    fun clearAll() {
        scope.launch {
            clearAllHistoryUseCase(ClearAllHistoryUseCase.Request)
        }
    }
}
