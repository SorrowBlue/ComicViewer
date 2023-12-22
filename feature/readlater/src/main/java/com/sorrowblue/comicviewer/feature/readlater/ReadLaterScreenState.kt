package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavBackStackEntry
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface ReadLaterScreenState {
    val navigator: ThreePaneScaffoldNavigator
    val pagingDataFlow: Flow<PagingData<File>>
    val uiState: ReadLaterScreenUiState
    fun onFileInfoClick(file: File)
    fun onExtraPaneCloseClick()
    fun onReadLaterClick(file: File)
    fun onClearAllClick()
}

context(NavBackStackEntry)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberReadLaterScreenState(
    navigator: ThreePaneScaffoldNavigator = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
    viewModel: ReadLaterViewModel = hiltViewModel(),
): ReadLaterScreenState = remember {
    ReadLaterScreenStateImpl(
        savedStateHandle = savedStateHandle,
        navigator = navigator,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
private class ReadLaterScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator,
    private val viewModel: ReadLaterViewModel,
) : ReadLaterScreenState {
    override val pagingDataFlow = viewModel.pagingDataFlow

    override var uiState: ReadLaterScreenUiState by savedStateHandle.saveable {
        mutableStateOf(
            ReadLaterScreenUiState()
        )
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

    override fun onClearAllClick() {
        viewModel.clearAll()
    }
}
