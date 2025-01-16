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
import org.koin.compose.viewmodel.koinViewModel
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.moduleNames
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.ktx.status
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheetUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

private const val DocumentModule = "document"

@Stable
internal class TutorialScreenState(
    private val scope: CoroutineScope,
    private val viewModel: TutorialViewModel,
    val pageState: PagerState,
) : SplitInstallStateUpdatedListener {

    val enabledBack: Boolean get() = pageState.currentPage != 0
    var uiState by mutableStateOf(TutorialScreenUiState())

    init {
        uiState = if (viewModel.splitInstallManager.installedModules.contains(DocumentModule)) {
            logcat { "Feature module($DocumentModule) is already installed." }
            uiState.copy(documentSheetUiState = DocumentSheetUiState.INSTALLED)
        } else {
            logcat { "Feature module($DocumentModule) is not installed." }
            uiState.copy(documentSheetUiState = DocumentSheetUiState.NONE)
        }
        viewModel.viewerOperationSettingsUseCase.settings.onEach {
            uiState = uiState.copy(
                directionSheetUiState = uiState.directionSheetUiState.copy(
                    direction = it.bindingDirection
                )
            )
        }.launchIn(scope)
    }

    override fun onStateUpdate(state: SplitInstallSessionState) {
        if (state.moduleNames.contains(DocumentModule)) {
            val uiState = when (state.status) {
                SplitInstallSessionStatus.CANCELED -> DocumentSheetUiState.CANCELED
                SplitInstallSessionStatus.CANCELING -> DocumentSheetUiState.CANCELING
                SplitInstallSessionStatus.DOWNLOADED -> DocumentSheetUiState.DOWNLOADED
                SplitInstallSessionStatus.DOWNLOADING -> DocumentSheetUiState.DOWNLOADING(
                    (state.bytesDownloaded.toDouble() / state.totalBytesToDownload).toFloat()
                )

                SplitInstallSessionStatus.FAILED -> DocumentSheetUiState.FAILED(state.err)
                SplitInstallSessionStatus.INSTALLED -> DocumentSheetUiState.INSTALLED
                SplitInstallSessionStatus.INSTALLING -> DocumentSheetUiState.INSTALLING
                SplitInstallSessionStatus.PENDING -> DocumentSheetUiState.Pending
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> DocumentSheetUiState.RequestsUserConfirmation
                SplitInstallSessionStatus.UNKNOWN -> DocumentSheetUiState.NONE
                else -> DocumentSheetUiState.NONE
            }
            this.uiState = this.uiState.copy(documentSheetUiState = uiState)
        }
    }

    fun onStart() {
        viewModel.splitInstallManager.registerListener(this)
    }

    fun onStop() {
        viewModel.splitInstallManager.unregisterListener(this)
    }

    fun onDocumentDownloadClick() {
        scope.launch {
            viewModel.splitInstallManager.requestInstall(listOf(DocumentModule))
        }
    }

    fun updateReadingDirection(rtl: BindingDirection) {
        scope.launch {
            viewModel.viewerOperationSettingsUseCase.edit { it.copy(bindingDirection = rtl) }
        }
    }

    fun onNextClick(onComplete: () -> Unit) {
        if (pageState.isLastPage) {
            onComplete()
        } else {
            scope.launch {
                pageState.animateScrollToPage(pageState.currentPage + 1)
            }
        }
    }

    fun onBack() {
        scope.launch {
            pageState.animateScrollToPage(pageState.currentPage - 1)
        }
    }
}

@Composable
internal fun rememberTutorialScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: TutorialViewModel = koinViewModel(),
    pageState: PagerState = rememberPagerState { TutorialSheet.entries.size },
) = remember {
    TutorialScreenState(scope, viewModel, pageState)
}
