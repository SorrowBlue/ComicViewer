package com.sorrowblue.comicviewer.feature.tutorial

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

internal interface TutorialScreenState {
    val enabledBack: Boolean
    var uiState: TutorialScreenUiState
    val pageState: PagerState

    fun onNextClick(onComplete: () -> Unit)
    fun updateReadingDirection(bindingDirection: BindingDirection)
    fun onBack()
    fun onDocumentDownloadClick()
}

@Composable
internal fun rememberTutorialScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    manageViewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase = koinInject(),
    pageState: PagerState = rememberPagerState { TutorialSheet.entries.size },
    uriHandler: UriHandler = LocalUriHandler.current,
): TutorialScreenState {
    return remember {
        TutorialScreenStateImpl(
            scope = scope,
            manageViewerOperationSettingsUseCase = manageViewerOperationSettingsUseCase,
            uriHandler = uriHandler,
            pageState = pageState
        )
    }
}

@Stable
private class TutorialScreenStateImpl(
    private val scope: CoroutineScope,
    private val manageViewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase,
    private val uriHandler: UriHandler,
    override val pageState: PagerState,
) : TutorialScreenState {

    override val enabledBack: Boolean get() = pageState.currentPage != 0
    override var uiState by mutableStateOf(TutorialScreenUiState())

    init {
        manageViewerOperationSettingsUseCase.settings.onEach {
            uiState = uiState.copy(
                directionSheetUiState = uiState.directionSheetUiState.copy(
                    direction = it.bindingDirection
                )
            )
        }.launchIn(scope)
    }

    override fun onDocumentDownloadClick() {
        uriHandler.openUri(APP_DOWNLOAD_LINK)
    }

    override fun updateReadingDirection(bindingDirection: BindingDirection) {
        scope.launch {
            manageViewerOperationSettingsUseCase.edit { it.copy(bindingDirection = bindingDirection) }
        }
    }

    override fun onNextClick(onComplete: () -> Unit) {
        if (pageState.isLastPage) {
            onComplete()
        } else {
            scope.launch {
                pageState.animateScrollToPage(pageState.currentPage + 1)
            }
        }
    }

    override fun onBack() {
        scope.launch {
            pageState.animateScrollToPage(pageState.currentPage - 1)
        }
    }
}

internal val PagerState.isLastPage: Boolean
    get() = currentPage == pageCount - 1

internal expect val APP_DOWNLOAD_LINK: String
