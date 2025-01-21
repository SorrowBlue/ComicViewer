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
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheetUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import org.koin.compose.koinInject

internal interface TutorialScreenState {
    val enabledBack: Boolean
    var uiState: TutorialScreenUiState
    val pageState: PagerState

    fun onNextClick(onComplete: () -> Unit)
    fun updateReadingDirection(bindingDirection: BindingDirection)
    fun onBack()
    fun onDocumentDownloadClick()
    fun onStart()
    fun onStop()
}

@Composable
internal fun rememberTutorialScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    androidSplitInstallManager: AndroidSplitInstallManager = koinInject(),
    manageViewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase = koinInject(),
    pageState: PagerState = rememberPagerState { TutorialSheet.entries.size },
): TutorialScreenState {
    return remember {
        TutorialScreenStateImpl(
            scope = scope,
            androidSplitInstallManager = androidSplitInstallManager,
            manageViewerOperationSettingsUseCase = manageViewerOperationSettingsUseCase,
            pageState = pageState
        )
    }
}

@Stable
private class TutorialScreenStateImpl(
    private val scope: CoroutineScope,
    private val androidSplitInstallManager: AndroidSplitInstallManager,
    private val manageViewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase,
    override val pageState: PagerState,
) : TutorialScreenState {

    override val enabledBack: Boolean get() = pageState.currentPage != 0
    override var uiState by mutableStateOf(TutorialScreenUiState())

    init {
        uiState = if (androidSplitInstallManager.installedModulesSet.contains(DocumentModule)) {
            logcat { "Feature module($DocumentModule) is already installed." }
            uiState.copy(documentSheetUiState = DocumentSheetUiState.INSTALLED)
        } else {
            logcat { "Feature module($DocumentModule) is not installed." }
            uiState.copy(documentSheetUiState = DocumentSheetUiState.NONE)
        }
        manageViewerOperationSettingsUseCase.settings.onEach {
            uiState = uiState.copy(
                directionSheetUiState = uiState.directionSheetUiState.copy(
                    direction = it.bindingDirection
                )
            )
        }.launchIn(scope)

        androidSplitInstallManager.setStateListener { documentSheetUiState ->
            uiState = uiState.copy(documentSheetUiState = documentSheetUiState)
        }
    }

    override fun onStart() {
        androidSplitInstallManager.registerListener()
    }

    override fun onStop() {
        androidSplitInstallManager.unregisterListener()
    }

    override fun onDocumentDownloadClick() {
        scope.launch {
            androidSplitInstallManager.requestInstall(DocumentModule)
        }
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
