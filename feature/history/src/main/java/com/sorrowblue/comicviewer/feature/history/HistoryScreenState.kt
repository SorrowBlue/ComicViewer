package com.sorrowblue.comicviewer.feature.history

import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.section.HistoryTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal sealed interface HistoryScreenEvent {
    data class Favorite(val bookshelfId: BookshelfId, val path: String) : HistoryScreenEvent
    data class OpenFolder(val file: File) : HistoryScreenEvent
    data class Book(val book: com.sorrowblue.comicviewer.domain.model.file.Book) :
        HistoryScreenEvent

    data object Back : HistoryScreenEvent
    data object Settings : HistoryScreenEvent
}

internal interface HistoryScreenState :
    SaveableScreenState,
    ScreenStateEvent<HistoryScreenEvent> {
    val pagingDataFlow: Flow<PagingData<Book>>
    val navigator: ThreePaneScaffoldNavigator<File.Key>
    fun onHistoryTopAppBarAction(action: HistoryTopAppBarAction)
    fun onFileInfoSheetAction(action: FileInfoSheetNavigator)
    fun onHistoryContentsAction(action: HistoryContentsAction)
}

@Composable
internal fun rememberHistoryScreenState(
    navigator: ThreePaneScaffoldNavigator<File.Key> = rememberSupportingPaneScaffoldNavigator<File.Key>(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: HistoryViewModel = hiltViewModel(),
): HistoryScreenState = rememberSaveableScreenState {
    HistoryScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        scope = scope,
        viewModel = viewModel
    )
}

private class HistoryScreenStateImpl(
    viewModel: HistoryViewModel,
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<File.Key>,
    override val scope: CoroutineScope,
) : HistoryScreenState {

    override val pagingDataFlow = viewModel.pagingDataFlow
    override val event = MutableSharedFlow<HistoryScreenEvent>()

    override fun onHistoryTopAppBarAction(action: HistoryTopAppBarAction) {
        when (action) {
            HistoryTopAppBarAction.Back -> sendEvent(HistoryScreenEvent.Back)
            HistoryTopAppBarAction.Settings -> sendEvent(HistoryScreenEvent.Settings)
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetNavigator) {
        when (action) {
            FileInfoSheetNavigator.Back -> scope.launch { navigator.navigateBack() }
            is FileInfoSheetNavigator.Favorite -> navigator.currentDestination?.contentKey?.let {
                sendEvent(HistoryScreenEvent.Favorite(it.bookshelfId, it.path))
            }

            is FileInfoSheetNavigator.OpenFolder -> sendEvent(HistoryScreenEvent.OpenFolder(action.file))
        }
    }

    override fun onHistoryContentsAction(action: HistoryContentsAction) {
        when (action) {
            is HistoryContentsAction.Book -> sendEvent(HistoryScreenEvent.Book(action.book))
            is HistoryContentsAction.FileInfo -> navigateToFileInfo(action.file)
        }
    }

    private fun navigateToFileInfo(file: File) {
        scope.launch {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, file.key())
        }
    }
}
