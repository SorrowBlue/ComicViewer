/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.tutorial

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal interface TutorialScreenState {
    val enabledBack: Boolean
    var uiState: TutorialScreenUiState
    val pageState: PagerState

    fun onNextClick(onComplete: () -> Unit)

    fun updateReadingDirection(bindingDirection: BindingDirection)

    fun onBack()
}

@Composable
internal fun rememberTutorialScreenState(
    viewModel: TutorialViewModel = metroViewModel<TutorialViewModel>(),
): TutorialScreenState {
    val coroutineScope = rememberCoroutineScope()
    val pageState = rememberPagerState { TutorialSheet.entries.size }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return remember(lifecycle) {
        TutorialScreenStateImpl(
            coroutineScope = coroutineScope,
            pageState = pageState,
            lifecycle = lifecycle,
            bindingDirection = viewModel.bindingDirection,
            updateBindingDirection = viewModel::updateBindingDirection,
        )
    }
}

private class TutorialScreenStateImpl(
    private val coroutineScope: CoroutineScope,
    override val pageState: PagerState,
    lifecycle: Lifecycle,
    bindingDirection: SharedFlow<BindingDirection>,
    private val updateBindingDirection: (BindingDirection) -> Unit,
) : TutorialScreenState {

    override var uiState by mutableStateOf(TutorialScreenUiState())

    override val enabledBack: Boolean get() = pageState.currentPage != 0

    init {
        bindingDirection
            .flowWithLifecycle(lifecycle)
            .onEach {
                uiState = uiState.copy(bindingDirection = it)
            }
            .launchIn(coroutineScope)
    }

    override fun updateReadingDirection(bindingDirection: BindingDirection) {
        updateBindingDirection(bindingDirection)
    }

    override fun onNextClick(onComplete: () -> Unit) {
        if (pageState.isLastPage) {
            onComplete()
        } else {
            coroutineScope.launch {
                pageState.animateScrollToPage(pageState.currentPage + 1)
            }
        }
    }

    override fun onBack() {
        coroutineScope.launch {
            pageState.animateScrollToPage(pageState.currentPage - 1)
        }
    }
}

internal val PagerState.isLastPage: Boolean
    get() = currentPage == pageCount - 1
