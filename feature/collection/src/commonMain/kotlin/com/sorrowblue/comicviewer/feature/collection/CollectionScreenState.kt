package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.usecase.collection.DeleteCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBarAction
import com.sorrowblue.comicviewer.feature.collection.section.CollectionContentsAction
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.rememberCanonicalScaffoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import com.sorrowblue.comicviewer.domain.model.collection.Collection as CollectionModel

internal sealed interface CollectionScreenEvent {
    data class CollectionAddClick(val bookshelfId: BookshelfId, val path: String) :
        CollectionScreenEvent

    data class FileClick(val file: File) : CollectionScreenEvent
    data class OpenFolderClick(val file: File) : CollectionScreenEvent
    data class EditClick(val collection: CollectionModel) : CollectionScreenEvent
    data object Back : CollectionScreenEvent
    data object SettingsClick : CollectionScreenEvent
}

internal interface CollectionScreenState {
    val scaffoldState: CanonicalScaffoldState<File.Key>
    val uiState: SmartCollectionScreenUiState
    val events: EventFlow<CollectionScreenEvent>
    val pagingDataFlow: Flow<PagingData<File>>
    val lazyGridState: LazyGridState
    fun onNavClick()
    fun onAppBarAction(action: CollectionAppBarAction)
    fun onSheetAction(action: FileInfoSheetNavigator)
    fun onContentsAction(action: CollectionContentsAction)
}

@Composable
internal fun rememberCollectionScreenState(
    route: Collection,
    scaffoldState: CanonicalScaffoldState<File.Key> = rememberCanonicalScaffoldState(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    getCollectionUseCase: GetCollectionUseCase = koinInject(),
    displaySettingsUseCase: ManageFolderDisplaySettingsUseCase = koinInject(),
    deleteCollectionUseCase: DeleteCollectionUseCase = koinInject(),
    viewModel: CollectionViewModel = koinViewModel { parametersOf(route) },
): CollectionScreenState {
    return remember {
        CollectionScreenStateImpl(
            getCollectionUseCase = getCollectionUseCase,
            scaffoldState = scaffoldState,
            pagingDataFlow = viewModel.pagingDataFlow,
            lazyGridState = lazyGridState,
            route = route,
            scope = scope,
            displaySettingsUseCase = displaySettingsUseCase,
            deleteCollectionUseCase = deleteCollectionUseCase
        )
    }
}

private class CollectionScreenStateImpl(
    getCollectionUseCase: GetCollectionUseCase,
    override val scaffoldState: CanonicalScaffoldState<File.Key>,
    override val pagingDataFlow: Flow<PagingData<File>>,
    override val lazyGridState: LazyGridState,
    private val route: Collection,
    private val scope: CoroutineScope,
    private val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
) : CollectionScreenState {

    override val events = EventFlow<CollectionScreenEvent>()

    override var uiState by mutableStateOf(SmartCollectionScreenUiState())
    lateinit var collection: CollectionModel

    init {
        getCollectionUseCase(GetCollectionUseCase.Request(route.id)).mapNotNull { it.dataOrNull() }
            .onEach {
                collection = it
                uiState = uiState.copy(appBarUiState = uiState.appBarUiState.copy(title = it.name))
            }.launchIn(scope)
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }

    override fun onAppBarAction(action: CollectionAppBarAction) {
        when (action) {
            CollectionAppBarAction.Back -> events.tryEmit(CollectionScreenEvent.Back)
            CollectionAppBarAction.Delete -> delete()
            CollectionAppBarAction.Edit -> events.tryEmit(CollectionScreenEvent.EditClick(collection))
            CollectionAppBarAction.FileListDisplay -> updateFolderDisplaySettings {
                it.copy(
                    fileListDisplay = when (uiState.appBarUiState.fileListDisplay) {
                        FileListDisplay.Grid -> FileListDisplay.List
                        FileListDisplay.List -> FileListDisplay.Grid
                    }
                )
            }

            CollectionAppBarAction.GridSize ->
                if (uiState.appBarUiState.fileListDisplay == FileListDisplay.Grid) {
                    updateFolderDisplaySettings {
                        it.copy(
                            gridColumnSize = when (it.gridColumnSize) {
                                GridColumnSize.Medium -> GridColumnSize.Large
                                GridColumnSize.Large -> GridColumnSize.Medium
                            }
                        )
                    }
                }

            CollectionAppBarAction.Settings -> events.tryEmit(CollectionScreenEvent.SettingsClick)
        }
    }

    override fun onSheetAction(action: FileInfoSheetNavigator) {
        when (action) {
            FileInfoSheetNavigator.Back -> scope.launch { scaffoldState.navigator.navigateBack() }
            is FileInfoSheetNavigator.Collection -> scaffoldState.navigator.currentDestination?.contentKey?.let {
                events.tryEmit(CollectionScreenEvent.CollectionAddClick(it.bookshelfId, it.path))
            }

            is FileInfoSheetNavigator.OpenFolder ->
                events.tryEmit(CollectionScreenEvent.OpenFolderClick(action.file))
        }
    }

    override fun onContentsAction(action: CollectionContentsAction) {
        when (action) {
            is CollectionContentsAction.File ->
                events.tryEmit(CollectionScreenEvent.FileClick(action.file))

            is CollectionContentsAction.FileInfo -> scope.launch {
                scaffoldState.navigator.navigateTo(
                    SupportingPaneScaffoldRole.Extra,
                    action.file.key()
                )
            }
        }
    }

    private fun updateFolderDisplaySettings(edit: (FolderDisplaySettings) -> FolderDisplaySettings) {
        scope.launch {
            displaySettingsUseCase.edit(edit)
        }
    }

    private fun delete() {
        scope.launch {
            deleteCollectionUseCase(DeleteCollectionUseCase.Request(route.id))
            events.emit(CollectionScreenEvent.Back)
        }
    }
}
