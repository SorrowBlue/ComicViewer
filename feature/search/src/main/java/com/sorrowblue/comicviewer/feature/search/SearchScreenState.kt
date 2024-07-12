package com.sorrowblue.comicviewer.feature.search

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
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBarAction
import com.sorrowblue.comicviewer.feature.search.section.SearchContentsAction
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoSheetState
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.folder.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal sealed interface SearchScreenEvent {
    data object Back : SearchScreenEvent

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
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState> = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
): SearchScreenState = rememberSaveableScreenState {
    SearchScreenStateImpl(
        savedStateHandle = it,
        args = args,
        navigator = navigator,
        viewModel = viewModel,
        getFileAttributeUseCase = viewModel.getFileAttributeUseCase,
        addReadLaterUseCase = viewModel.addReadLaterUseCase,
        existsReadlaterUseCase = viewModel.existsReadlaterUseCase,
        deleteReadLaterUseCase = viewModel.deleteReadLaterUseCase,
        scope = scope,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
private class SearchScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    private val args: SearchArgs,
    private val viewModel: SearchViewModel,
    override val getFileAttributeUseCase: GetFileAttributeUseCase,
    override val existsReadlaterUseCase: ExistsReadlaterUseCase,
    override val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    override val addReadLaterUseCase: AddReadLaterUseCase,
    override val snackbarHostState: SnackbarHostState,
    override val scope: CoroutineScope,
) : SearchScreenState {

    override val event = MutableSharedFlow<SearchScreenEvent>()

    override var fileInfoJob: Job? = null
    override var uiState by savedStateHandle.saveable { mutableStateOf(SearchScreenUiState()) }
        private set
    override val lazyPagingItems = viewModel.pagingDataFlow
    override var isScrollableTop by mutableStateOf(false)
    override var isSkipFirstRefresh by mutableStateOf(true)

    init {
        viewModel.searchCondition = {
            uiState.searchTopAppBarUiState.searchCondition.copy(
                range = when (val range = uiState.searchTopAppBarUiState.searchCondition.range) {
                    SearchCondition.Range.Bookshelf -> range
                    is SearchCondition.Range.InFolder -> range.copy(args.path)
                    is SearchCondition.Range.SubFolder -> range.copy(args.path)
                },
            )
        }
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
        }
        update()
    }

    private fun copySearchCondition(action: (SearchCondition) -> SearchCondition): SearchScreenUiState {
        return uiState.copy(
            searchTopAppBarUiState = uiState.searchTopAppBarUiState.copy(
                searchCondition = action(uiState.searchTopAppBarUiState.searchCondition)
            )
        )
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
            is SearchContentsAction.FileInfo -> {
                fetchFileInfo(action.file) {
                    navigator.navigateTo(
                        SupportingPaneScaffoldRole.Extra,
                        it.copy(isOpenFolderEnabled = true)
                    )
                }
            }
        }
    }
}
