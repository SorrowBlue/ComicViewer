package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface ReadLaterScreenState : SaveableScreenState {
    val pagingDataFlow: Flow<PagingData<File>>
    val navigator: ThreePaneScaffoldNavigator<File>
    val lazyGridState: LazyGridState
    fun onFileInfoClick(file: File)
    fun onExtraPaneCloseClick()
    fun onReadLaterClick(file: File)
    fun onClearAllClick()
    fun onNavClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Destination
@Composable
internal fun rememberReadLaterScreenState(
    navigator: ThreePaneScaffoldNavigator<File> = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ReadLaterViewModel = hiltViewModel(),
): ReadLaterScreenState = rememberSaveableScreenState {
    ReadLaterScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        lazyGridState = lazyGridState,
        scope = scope,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private class ReadLaterScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<File>,
    override val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
    private val viewModel: ReadLaterViewModel,
) : ReadLaterScreenState {
    override val pagingDataFlow = viewModel.pagingDataFlow

    override fun onFileInfoClick(file: File) {
        navigator.navigateTo(SupportingPaneScaffoldRole.Extra, file)
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

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }
}
