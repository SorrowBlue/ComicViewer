package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBarAction
import com.sorrowblue.comicviewer.feature.search.section.SearchContentsAction
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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
    ScreenStateEvent<SearchScreenEvent> {
    val lazyGridState: LazyGridState
    val uiState: SearchScreenUiState
    val lazyPagingItems: Flow<PagingData<File>>
    var isSkipFirstRefresh: Boolean
    var isScrollableTop: Boolean
    val navigator: ThreePaneScaffoldNavigator<File>
    fun onFileInfoSheetAction(action: FileInfoSheetNavigator)
    fun onSearchTopAppBarAction(action: SearchTopAppBarAction)
    fun onSearchContentsAction(action: SearchContentsAction)
}

@Composable
internal fun rememberSearchScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SearchViewModel = hiltViewModel(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    navigator: ThreePaneScaffoldNavigator<File> = rememberSupportingPaneScaffoldNavigator<File>(),
): SearchScreenState = rememberSaveableScreenState {
    SearchScreenStateImpl(
        manageFolderDisplaySettingsUseCase = viewModel.manageFolderDisplaySettingsUseCase,
        savedStateHandle = it,
        navigator = navigator,
        scope = scope,
        lazyGridState = lazyGridState,
        viewModel = viewModel
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
private class SearchScreenStateImpl(
    manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<File>,
    override val scope: CoroutineScope,
    override val lazyGridState: LazyGridState,
    private val viewModel: SearchViewModel,
) : SearchScreenState {

    override val event = MutableSharedFlow<SearchScreenEvent>()

    override var uiState by savedStateHandle.saveable { mutableStateOf(SearchScreenUiState()) }
        private set
    override val lazyPagingItems = viewModel.pagingDataFlow

    override var isScrollableTop by mutableStateOf(false)
    override var isSkipFirstRefresh by mutableStateOf(true)

    init {
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
                uiState = copySearchCondition {
                    it.copy(
                        range = when (action.range) {
                            SearchCondition.Range.Bookshelf -> SearchCondition.Range.Bookshelf
                            is SearchCondition.Range.InFolder -> SearchCondition.Range.InFolder(it.query)
                            is SearchCondition.Range.SubFolder -> SearchCondition.Range.SubFolder(it.query)
                        }
                    )
                }

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
            viewModel.searchCondition = it.searchTopAppBarUiState.searchCondition
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetNavigator) {
        when (action) {
            FileInfoSheetNavigator.Back -> scope.launch {
                navigator.navigateBack()
            }

            is FileInfoSheetNavigator.Favorite -> sendEvent(
                SearchScreenEvent.Favorite(navigator.currentDestination!!.contentKey!!)
            )

            is FileInfoSheetNavigator.OpenFolder -> sendEvent(
                SearchScreenEvent.OpenFolder(navigator.currentDestination!!.contentKey!!)
            )
        }
    }

    override fun onSearchContentsAction(action: SearchContentsAction) {
        when (action) {
            is SearchContentsAction.File -> sendEvent(SearchScreenEvent.File(action.file))
            is SearchContentsAction.FileInfo -> scope.launch {
                navigator.navigateTo(SupportingPaneScaffoldRole.Extra, action.file)
            }
        }
    }
}
