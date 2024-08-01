package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBarAction
import com.sorrowblue.comicviewer.feature.search.section.SearchContentsAction
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal sealed interface SearchScreenEvent {
    data object Back : SearchScreenEvent
    data object Settings : SearchScreenEvent

    data class Favorite(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        SearchScreenEvent

    data class OpenFolder(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        SearchScreenEvent

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) : SearchScreenEvent
}

@Stable
internal interface SearchScreenState :
    SaveableScreenState,
    FileInfoSheetState,
    ScreenStateEvent<SearchScreenEvent> {
    val lazyGridState: LazyGridState
    val uiState: SearchScreenUiState
    val lazyPagingItems: Flow<PagingData<File>>
    var isSkipFirstRefresh: Boolean
    var isScrollableTop: Boolean
    fun onFileInfoSheetAction(action: FileInfoSheetAction)
    fun onSearchTopAppBarAction(action: SearchTopAppBarAction)
    fun onSearchContentsAction(action: SearchContentsAction)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberSearchScreenState(
    args: SearchArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: SearchViewModel = hiltViewModel(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState> = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
): SearchScreenState = rememberSaveableScreenState {
    SearchScreenStateImpl(
        manageFolderDisplaySettingsUseCase = viewModel.manageFolderDisplaySettingsUseCase,
        args = args,
        savedStateHandle = it,
        navigator = navigator,
        getFileAttributeUseCase = viewModel.getFileAttributeUseCase,
        existsReadlaterUseCase = viewModel.existsReadlaterUseCase,
        deleteReadLaterUseCase = viewModel.deleteReadLaterUseCase,
        addReadLaterUseCase = viewModel.addReadLaterUseCase,
        snackbarHostState = snackbarHostState,
        scope = scope,
        lazyGridState = lazyGridState,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
private class SearchScreenStateImpl(
    manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val args: SearchArgs,
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    override val getFileAttributeUseCase: GetFileAttributeUseCase,
    override val existsReadlaterUseCase: ExistsReadlaterUseCase,
    override val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    override val addReadLaterUseCase: AddReadLaterUseCase,
    override val snackbarHostState: SnackbarHostState,
    override val scope: CoroutineScope,
    override val lazyGridState: LazyGridState,
    private val viewModel: SearchViewModel,
) : SearchScreenState {

    override val event = MutableSharedFlow<SearchScreenEvent>()

    override var fileInfoJob: Job? = null
    override var uiState by savedStateHandle.saveable { mutableStateOf(SearchScreenUiState()) }
        private set
    override val lazyPagingItems = viewModel.pagingDataFlow

    init {
        navigator.currentDestination?.content?.let { fileInfoUiState ->
            navigateToFileInfo(fileInfoUiState.file)
        }
    }

    override var isScrollableTop by mutableStateOf(false)
    override var isSkipFirstRefresh by mutableStateOf(true)

    init {
        navigator.currentDestination?.content?.let {
            navigateToFileInfo(it.file)
        }
        manageFolderDisplaySettingsUseCase.settings.onEach {
            uiState = uiState.copy(
                searchContentsUiState = uiState.searchContentsUiState.copy(
                    fileLazyVerticalGridUiState = uiState.searchContentsUiState.fileLazyVerticalGridUiState.copy(
                        fileListDisplay = FileListDisplay.List,
                        showThumbnails = it.showThumbnails,
                        fontSize = it.fontSize,
                        imageScale = it.imageScale,
                        imageFilterQuality = it.imageFilterQuality,
                    )
                )
            )
        }.launchIn(scope)
    }

    private fun update() {
        isScrollableTop = true
        if (isSkipFirstRefresh) {
            isSkipFirstRefresh = false
        }
    }

    override fun onSearchTopAppBarAction(action: SearchTopAppBarAction) {
        when (action) {
            SearchTopAppBarAction.BackClick -> sendEvent(SearchScreenEvent.Back)
            is SearchTopAppBarAction.PeriodClick ->
                uiState = copySearchCondition { it.copy(period = action.period) }

            is SearchTopAppBarAction.QueryChange ->
                uiState = copySearchCondition { it.copy(query = action.value) }

            is SearchTopAppBarAction.RangeClick ->
                uiState = copySearchCondition { it.copy(range = action.range) }

            is SearchTopAppBarAction.SortTypeClick ->
                uiState = copySearchCondition { it.copy(sortType = action.sortType) }

            is SearchTopAppBarAction.ShowHiddenClick ->
                uiState = copySearchCondition { it.copy(showHidden = action.value) }

            SearchTopAppBarAction.Settings -> sendEvent(SearchScreenEvent.Settings)
        }
        update()
    }

    private fun copySearchCondition(action: (SearchCondition) -> SearchCondition): SearchScreenUiState {
        return uiState.copy(
            searchTopAppBarUiState = uiState.searchTopAppBarUiState.copy(
                searchCondition = action(uiState.searchTopAppBarUiState.searchCondition)
            )
        ).also {
            viewModel.searchCondition = uiState.searchTopAppBarUiState.searchCondition.copy(
                range = when (val range = uiState.searchTopAppBarUiState.searchCondition.range) {
                    SearchCondition.Range.Bookshelf -> range
                    is SearchCondition.Range.InFolder -> range.copy(args.path)
                    is SearchCondition.Range.SubFolder -> range.copy(args.path)
                },
            )
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetAction) {
        when (action) {
            FileInfoSheetAction.Close -> navigator.navigateBack()
            FileInfoSheetAction.Favorite -> sendEvent(
                SearchScreenEvent.Favorite(navigator.currentDestination!!.content!!.file)
            )

            FileInfoSheetAction.OpenFolder -> sendEvent(
                SearchScreenEvent.OpenFolder(navigator.currentDestination!!.content!!.file)
            )

            FileInfoSheetAction.ReadLater -> onReadLaterClick()
        }
    }

    override fun onSearchContentsAction(action: SearchContentsAction) {
        when (action) {
            is SearchContentsAction.File -> sendEvent(SearchScreenEvent.File(action.file))
            is SearchContentsAction.FileInfo -> navigateToFileInfo(action.file)
        }
    }

    private fun navigateToFileInfo(file: File) {
        fetchFileInfo(file) {
            navigator.navigateTo(
                SupportingPaneScaffoldRole.Extra,
                it.copy(isOpenFolderEnabled = true)
            )
        }
    }
}
