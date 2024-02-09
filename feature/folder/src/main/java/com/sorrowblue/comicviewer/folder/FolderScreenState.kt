package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.file.component.toFileContentLayout
import com.sorrowblue.comicviewer.folder.section.SortItem
import com.sorrowblue.comicviewer.folder.section.SortOrder
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Stable
internal interface FolderScreenState : SaveableScreenState {

    fun onSortOrderClick(sortOrder: SortOrder)
    fun onSortItemClick(sortItem: SortItem)
    fun onFileInfoClick(file: File)
    fun onExtraPaneCloseClick()
    fun onSortSheetDismissRequest()
    fun onReadLaterClick(file: File)
    fun openSort()
    fun onGridSizeChange()
    fun toggleFileListType()
    val lazyGridState: LazyGridState
    val uiState: FolderScreenUiState
    var isScrollableTop: Boolean
    var isSkipFirstRefresh: Boolean
    var restorePath: String?
    val pagingDataFlow: Flow<PagingData<File>>
    val navigator: ThreePaneScaffoldNavigator<File>
    val bookshelfId: BookshelfId
    val path: String
    val sort: StateFlow<SortType>
    fun onNavClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberFolderScreenState(
    args: FolderArgs,
    navigator: ThreePaneScaffoldNavigator<File> = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
    viewModel: FolderViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
): FolderScreenState = rememberSaveableScreenState {
    FolderScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        lazyGridState = lazyGridState,
        args = args,
        viewModel = viewModel,
        scope = scope
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
private class FolderScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<File>,
    override val lazyGridState: LazyGridState,
    private val args: FolderArgs,
    private val viewModel: FolderViewModel,
    private val scope: CoroutineScope,
) : FolderScreenState {

    override val pagingDataFlow = viewModel.pagingDataFlow(args.bookshelfId, args.path)
    override var restorePath by savedStateHandle.saveable("restorePath", stateSaver = autoSaver()) {
        mutableStateOf(args.restorePath)
    }
    override var isSkipFirstRefresh by savedStateHandle.saveable { mutableStateOf(true) }
    override var isScrollableTop by savedStateHandle.saveable { mutableStateOf(false) }

    override var uiState by savedStateHandle.saveable { mutableStateOf(FolderScreenUiState()) }
        private set

    init {
        viewModel.displaySettings.map(FolderDisplaySettings::toFileContentLayout)
            .distinctUntilChanged().onEach {
                uiState = uiState.copy(
                    folderAppBarUiState = uiState.folderAppBarUiState.copy(fileContentType = it),
                    fileContentType = it
                )
            }.launchIn(scope)
        viewModel.displaySettings.map { it.sortType }
            .distinctUntilChanged().onEach {
                val sortItem = when (it) {
                    is SortType.DATE -> SortItem.Date
                    is SortType.NAME -> SortItem.Name
                    is SortType.SIZE -> SortItem.Size
                }
                val sortOrder = if (it.isAsc) SortOrder.Asc else SortOrder.Desc
                uiState = uiState.copy(
                    sortItem = sortItem,
                    sortOrder = sortOrder,
                    folderAppBarUiState = uiState.folderAppBarUiState.copy(
                        sortItem = sortItem,
                        sortOrder = sortOrder
                    )
                )
            }.launchIn(scope)
        scope.launch {
            viewModel.getFileUseCase.execute(GetFileUseCase.Request(bookshelfId, path)).first()
                .onSuccess {
                    uiState = uiState.copy(
                        folderAppBarUiState = uiState.folderAppBarUiState.copy(title = it.name)
                    )
                }
        }
    }

    override val bookshelfId get() = args.bookshelfId
    override val path get() = args.path

    override val sort = viewModel.sort

    override fun toggleFileListType() {
        viewModel.updateDisplay(
            when (uiState.fileContentType) {
                is FileContentType.Grid -> FolderDisplaySettings.Display.LIST
                FileContentType.List -> FolderDisplaySettings.Display.GRID
            }
        )
    }

    override fun onGridSizeChange() {
        if (uiState.fileContentType is FileContentType.Grid) {
            viewModel.updateGridSize()
        }
    }

    override fun openSort() {
        uiState = uiState.copy(openSortSheet = true)
    }

    override fun onReadLaterClick(file: File) {
        viewModel.addToReadLater(file = file)
    }

    override fun onSortSheetDismissRequest() {
        uiState = uiState.copy(openSortSheet = false)
    }

    override fun onExtraPaneCloseClick() {
        navigator.navigateBack()
    }

    override fun onFileInfoClick(file: File) {
        navigator.navigateTo(SupportingPaneScaffoldRole.Extra, file)
    }

    override fun onSortItemClick(sortItem: SortItem) {
        isScrollableTop = true
        isSkipFirstRefresh = false
        onSortSheetDismissRequest()
        viewModel.updateDisplaySettings {
            it.copy(
                sortType = when (sortItem) {
                    SortItem.Date -> SortType.DATE(uiState.sortOrder == SortOrder.Asc)
                    SortItem.Name -> SortType.NAME(uiState.sortOrder == SortOrder.Asc)
                    SortItem.Size -> SortType.SIZE(uiState.sortOrder == SortOrder.Asc)
                }
            )
        }
    }

    override fun onSortOrderClick(sortOrder: SortOrder) {
        isScrollableTop = true
        isSkipFirstRefresh = false
        onSortSheetDismissRequest()
        viewModel.updateDisplaySettings {
            it.copy(
                sortType = when (sortOrder) {
                    SortOrder.Asc -> it.sortType.copy2(isAsc = true)
                    SortOrder.Desc -> it.sortType.copy2(isAsc = false)
                }
            )
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
