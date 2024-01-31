package com.sorrowblue.comicviewer.feature.history

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
interface HistoryScreenState : SaveableScreenState {
    val navigator: ThreePaneScaffoldNavigator
    val pagingDataFlow: Flow<PagingData<Book>>
    val uiState: HistoryScreenUiState
    fun onFileInfoClick(file: File)
    fun onExtraPaneCloseClick()
    fun onReadLaterClick(file: File)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberHistoryScreenState(
    navigator: ThreePaneScaffoldNavigator = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
    viewModel: HistoryViewModel = hiltViewModel(),
): HistoryScreenState = rememberSaveableScreenState {
    HistoryScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
private class HistoryScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator,
    private val viewModel: HistoryViewModel,
) : HistoryScreenState {
    override val pagingDataFlow = viewModel.pagingDataFlow

    override var uiState: HistoryScreenUiState by savedStateHandle.saveable {
        mutableStateOf(HistoryScreenUiState())
    }
        private set

    override fun onFileInfoClick(file: File) {
        uiState = uiState.copy(file = file)
        navigator.navigateTo(SupportingPaneScaffoldRole.Extra)
    }

    override fun onExtraPaneCloseClick() {
        navigator.navigateBack()
    }

    override fun onReadLaterClick(file: File) {
        viewModel.addToReadLater(file = file)
    }
}
