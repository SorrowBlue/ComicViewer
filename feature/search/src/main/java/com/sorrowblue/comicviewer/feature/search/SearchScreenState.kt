package com.sorrowblue.comicviewer.feature.search

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionsUiState
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Stable
internal interface SearchScreenState : SaveableScreenState {
    val navigator: ThreePaneScaffoldNavigator

    val uiState: SearchScreenUiState
    val lazyPagingItems: Flow<PagingData<File>>
    var isSkipFirstRefresh: Boolean
    var isScrollableTop: Boolean
    fun onChangeSearchCondition(searchCondition: SearchConditionsUiState.SearchCondition)
    fun onQueryChange(query: String)
    fun onReadLaterClick(file: File)
    fun onFileInfoClick(file: File)
    fun onFileInfoCloseClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberSearchScreenState(
    args: SearchArgs,
    viewModel: SearchViewModel = hiltViewModel(),
    navigator: ThreePaneScaffoldNavigator = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
): SearchScreenState = rememberSaveableScreenState {
    SearchScreenStateImpl(
        savedStateHandle = it,
        args = args,
        navigator = navigator,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
private class SearchScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator,
    private val args: SearchArgs,
    private val viewModel: SearchViewModel,
) : SearchScreenState {

    override var uiState by savedStateHandle.saveable { mutableStateOf(SearchScreenUiState()) }
        private set
    override val lazyPagingItems: Flow<PagingData<File>> = viewModel.pagingDataFlow
    override var isScrollableTop by mutableStateOf(false)
    override var isSkipFirstRefresh by mutableStateOf(true)

    init {
        viewModel.searchCondition = {
            SearchCondition(
                uiState.query,
                when (uiState.searchConditionsUiState.range) {
                    SearchConditionsUiState.Range.BOOKSHELF -> SearchCondition.Range.BOOKSHELF
                    SearchConditionsUiState.Range.IN_FOLDER ->
                        SearchCondition.Range.InFolder(args.path)

                    SearchConditionsUiState.Range.FOLDER_BELOW ->
                        SearchCondition.Range.SubFolder(args.path)
                },
                when (uiState.searchConditionsUiState.period) {
                    SearchConditionsUiState.Period.NONE -> SearchCondition.Period.NONE
                    SearchConditionsUiState.Period.HOUR_24 -> SearchCondition.Period.HOUR_24
                    SearchConditionsUiState.Period.WEEK_1 -> SearchCondition.Period.WEEK_1
                    SearchConditionsUiState.Period.MONTH_1 -> SearchCondition.Period.MONTH_1
                },
                when (uiState.searchConditionsUiState.order) {
                    SearchConditionsUiState.Order.NAME -> SearchCondition.Order.NAME
                    SearchConditionsUiState.Order.TIMESTAMP -> SearchCondition.Order.DATE
                    SearchConditionsUiState.Order.SIZE -> SearchCondition.Order.SIZE
                },
                when (uiState.searchConditionsUiState.sort) {
                    SearchConditionsUiState.Sort.ASC -> SearchCondition.Sort.ASC
                    SearchConditionsUiState.Sort.DESC -> SearchCondition.Sort.DESC
                }
            )
        }
    }

    override fun onChangeSearchCondition(searchCondition: SearchConditionsUiState.SearchCondition) {
        val searchConditionsUiState = uiState.searchConditionsUiState
        uiState = uiState.copy(
            searchConditionsUiState = when (searchCondition) {
                is SearchConditionsUiState.Order -> searchConditionsUiState.copy(order = searchCondition)
                is SearchConditionsUiState.Period -> searchConditionsUiState.copy(period = searchCondition)
                is SearchConditionsUiState.Range -> searchConditionsUiState.copy(range = searchCondition)
                is SearchConditionsUiState.Sort -> searchConditionsUiState.copy(sort = searchCondition)
            }
        )
        update()
    }

    override fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query)
        update()
    }

    override fun onReadLaterClick(file: File) {
        viewModel.addReadLater(file)
    }

    private fun update() {
        isScrollableTop = true
        if (isSkipFirstRefresh) {
            isSkipFirstRefresh = false
        }
    }

    override fun onFileInfoClick(file: File) {
        uiState = uiState.copy(file = file)
        navigator.navigateTo(SupportingPaneScaffoldRole.Extra)
    }

    override fun onFileInfoCloseClick() {
        navigator.navigateBack()
    }
}
